# Order Creation Implementation Plan

## Goal

Add an authenticated seller flow to create a purchase order for a customer, with line items, an address snapshot, shipping choice and shipping cost, and a generated invoice identifier/date. Persist the order and its details atomically.

## Repository facts to account for

- The backend is Spring Boot with Spring Data JPA, PostgreSQL, and Flyway migrations.
- `User.id` and `Product.id` are UUIDs; user phone numbers are normalized on application writes and uniquely indexed when non-null. `User.uid` is required and unique, and users have role sets.
- Product prices are `BigInteger`, while the proposed order fields use `Long`. Use `BigInteger` or a consistently bounded `Long` for all monetary values; do not silently truncate product prices.
- Existing tables are defined in Flyway migration `V1`; schema changes belong in a new migration, not edits to V1.

## API contract

Implemented as authenticated `POST /api/orders`. The seller comes from the JWT subject; seller identity is not accepted in the request.

Implemented as authenticated `GET /api/customers?query=...&page=1&size=20`. Search is seller-scoped and returns paginated customer IDs, names, and phone numbers.

Request fields:

- Optional `customerId` (UUID)
- Required `customerName`, `customerPhoneNumber`, `customerAddress`
- Required non-empty `items`, each with `productId` (UUID) and positive `quantity`
- Optional `shippingId` and `shippingCost`; provide both for delivery or omit both for store pickup

Return the created order with its public order ID, customer snapshot, line item price/quantity/subtotal, total, shipping details, invoice ID, and invoice issue time. Add DTOs and validation rather than binding JPA entities directly.

Orders have these statuses: `PENDING`, `CONFIRMED`, `PROCESSING`, `READY_FOR_PICKUP`, `SHIPPED`, `COMPLETED`, and `CANCELLED`. The authenticated seller can list orders at `GET /api/orders` with optional `status`, `fromDate`, `toDate`, `orderId`, and `invoiceId` filters and `page`/`size` pagination; the response uses `ListResponse`. Date filters apply inclusively to invoice issue dates in UTC. `PATCH /api/orders/{orderUuid}/status` accepts `{ "status": "CONFIRMED" }` and returns the updated order. Both endpoints are seller-scoped.

## Customer matching and creation

1. If `customerId` is supplied, require that it identifies a customer associated with the authenticated seller. Reject the request as forbidden (or the API's consistent equivalent) if it is another seller's customer; do not silently replace an explicitly supplied, invalid ID. Return not found for an unknown ID.
2. Without an ID, normalize the phone number and search for an existing user. A non-null normalized phone number must be unique across users, so an exact phone match resolves to at most one user. If there is no phone match, search by name only under the defined ambiguity policy; reject ambiguous matches rather than choosing arbitrarily.
3. If no match exists, create a buyer user with a `manual:{UUID}` internal UID and BUYER role, then link it to the seller. This internal UID does not create authentication credentials.
4. Prevent duplicate customer creation with a database uniqueness constraint on non-null normalized phone values. Null phone values remain allowed, but order-created buyers require a phone number.

## Seller-customer relationship and buyer search

Persist seller-customer membership explicitly (or use an existing equivalent relationship if discovered during implementation). The relationship has a generated BIGINT ID and required seller/customer `User` foreign keys, with `UNIQUE(seller_id, customer_id)`; this reconciles the requested ID with the pairwise uniqueness rule. Register a customer for a seller when that seller creates or otherwise adds the customer. Buyer search and supplied `customerId` validation must both query this relationship scoped to the authenticated seller. Define whether customer records can be linked to multiple sellers; the unique pair supports that while preventing duplicate links for one seller.

Search API behavior:

- `GET /api/customers?query={name-or-phone}&page={page}&size={size}` (or the repository's established pagination convention).
- Require a non-blank query and cap page size.
- Search name case-insensitively and phone using normalized digits/country code; return distinct customers even if both fields match.
- Scope every result to the authenticated seller's customer links; never treat a caller-supplied seller ID as authorization.
- Return a compact buyer DTO containing `id`, `name`, and `phoneNumber`; do not return authentication fields, roles, or unrelated profile data.
- Add indexes that support seller-to-customer lookup and name/phone search. Confirm query plan needs before adding specialized PostgreSQL indexes.

## Persistence model

Create an `order`/`purchase_orders` module containing entities, repositories, service, controller, request/response DTOs, and mappers consistent with existing package style.

- `PurchaseOrder`: UUID primary key; unique human-readable `order_id`; seller and resolved customer foreign keys; immutable customer name, phone, and address snapshots; monetary total and profit; shipping relation/details; unique invoice ID; invoice issue timestamp.
- `PurchaseOrderDetail`: generated BIGINT ID; required order and product references; unit-price and initial-cost snapshots, positive quantity, line subtotal, and line total initial cost. Product name snapshot preserves historical order details after catalog edits.
- `Shipping`: generated BIGINT ID and required name. Treat as catalog data managed separately; do not let order creation create arbitrary shipping methods.
- `ShippingDetail`: generated BIGINT ID; one-to-one required order reference and non-negative shipping-cost snapshot. Store a shipping-name/method snapshot too if shipping catalog names can change.
- Seller/customer association: do not create a separate `Customer` join entity unless customer membership has independent lifecycle/data. If it is needed, model seller and customer as two required `User` references and add `UNIQUE(seller_id, customer_id)`; the stated `id` plus composite primary-key constraint is contradictory. If retaining an ID, make it the primary key and the pair a unique constraint.

Use explicit table/column names and foreign keys. Add indexes for seller plus creation time, customer, order ID, and invoice ID as appropriate. Decide delete behavior explicitly; historical orders should generally prevent hard deletion or retain nullable/soft-deleted references and snapshots.

## Transactional creation flow

Implement creation in one `@Transactional` service operation:

1. Resolve authenticated seller and enforce seller role/authorization.
2. Validate required fields, positive quantities, non-negative shipping cost, and non-empty items.
3. Resolve or create customer according to the rules above.
4. Ensure the resolved/created buyer is linked to the authenticated seller. Load the shipping method and verify it exists and is active, if the catalog supports activation.
5. Load all requested products in one query; reject missing, inactive, or seller-inaccessible products. Confirm how product ownership is represented (`Product.uid` currently exists, but its relationship to seller should be verified).
6. Snapshot each unit price from the product at order time. Compute each line subtotal and order total using exact arithmetic; define whether total includes shipping (recommended: product subtotal plus shipping cost, with field naming/documentation explicit).
7. Generate unique order and invoice IDs in the required `PO-{YYMMDD}-{Unique Code}` and `INV-{YYMMDD}-{Unique Code}` formats using the agreed timezone and collision-safe database uniqueness/retry strategy. Do not rely on timestamps alone for uniqueness.
8. Persist order and details, issue invoice timestamp from the server clock, and return the response. Any failure rolls back the complete operation.

Inventory decrement/reservation is not specified in the request. Confirm the intended stock behavior before coupling order creation to inventory; if required, lock/version-check stock, reject insufficient stock, and record stock movements in the same transaction.

## Database migration

Add a new Flyway migration after V1 for shipping, purchase orders, order details, shipping details, constraints, indexes, and any chosen customer/phone uniqueness changes. Use PostgreSQL-compatible UUID, `BIGINT`/`NUMERIC`, and `TIMESTAMP WITH TIME ZONE` types. Make required columns `NOT NULL`, enforce positive quantity and non-negative amount checks, and add unique constraints for public order and invoice IDs. Ensure the JPA mappings match the migration exactly.

## Implementation sequence

1. Settle domain decisions listed below, especially new-user identity, buyer name matching, product ownership, stock handling, and money type.
2. Add schema migration and entity/repository mappings.
3. Add validated create-order request/response DTOs and ID generator.
4. Implement transactional customer resolution, product/shipping validation, price snapshotting, totals, and persistence.
5. Add authenticated controller and consistent error responses for invalid input, missing resources, ambiguous customer matches, and forbidden access.
6. Verify migration and mappings, then exercise success, validation, rollback, and concurrency cases.

## Decisions to confirm before implementation

- How a newly created customer receives the required `User.uid` and authentication credentials; whether that user gets a customer role.
- How buyer creation links to seller-customer membership, and whether buyers may be shared across sellers.
- Name-only matching rules for order creation, including ambiguity handling and normalization. Phone numbers are nullable for general users, but any non-null number must be unique; order-created buyers require a phone.
- Whether a seller can order only their own products and where that ownership is represented.
- Whether order creation reserves/decrements inventory.
- Whether `totalPrice` includes shipping, and whether monetary fields stay `BigInteger`/`NUMERIC` or change to `Long`/`BIGINT`.
- The timezone used for the date portion of generated identifiers (recommend UTC) and the unique-code strategy.
- Whether a seller-customer relationship is a persistent domain entity or simply the two order foreign keys.

## Implemented decisions and boundaries

- Money uses `BigInteger`/`NUMERIC(38,0)` to match product prices. The returned order total includes item subtotal plus shipping cost.
- ID date components use UTC; each PO and invoice ID uses a six-character hexadecimal code with unique database constraints.
- Orders accept active products owned by the authenticated seller's UID. Inventory is not changed because stock reservation/decrement behavior was not specified.
- A supplied customer ID that does not exist returns 404; one not linked to the seller returns 403.
- Shipping is optional for store pickup. For delivery, both catalog `shippingId` and non-negative cost are required; pickup adds zero shipping cost. Shipping methods must be provisioned before use.
- Migration V3 adds the order status column, defaults existing orders to `PENDING`, constrains its values, and adds a seller/status/date index.
- Migration V3 also snapshots initial cost per item and total initial cost per order detail, then stores order total initial cost and profit. Profit is subtotal minus initial product cost; shipping revenue/cost is excluded because actual carrier expense is not modeled.
