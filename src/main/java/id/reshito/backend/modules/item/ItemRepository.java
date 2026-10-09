package id.reshito.backend.modules.item;


import id.reshito.backend.modules.item.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Long> {

}
