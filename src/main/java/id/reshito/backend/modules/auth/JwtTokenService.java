package id.reshito.backend.modules.auth;

import id.reshito.backend.modules.auth.dto.TokenResponse;
import id.reshito.backend.modules.user.RoleEnum;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Set;

@Service
public class JwtTokenService {
    private final JwtEncoder jwtEncoder;
    private final String issuer;
    private final long ttlSeconds;

    public JwtTokenService(
            JwtEncoder jwtEncoder,
            @Value("${app.jwt.issuer}") String issuer,
            @Value("${app.jwt.access-token-ttl-seconds}") long ttlSeconds
    ) {
        this.jwtEncoder = jwtEncoder;
        this.issuer = issuer;
        this.ttlSeconds = ttlSeconds;
    }

    public String generateToken(String userId, Instant expiredAt, List<RoleEnum> roles) {
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(this.issuer)
                .subject(userId)
                .claim("roles", roles)
                .issuedAt(expiredAt.minusSeconds(this.ttlSeconds))
                .expiresAt(expiredAt)
                .build();

        JwsHeader header = JwsHeader
                .with(MacAlgorithm.HS256)
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }


    public TokenResponse createAccessToken(String userId, List<RoleEnum> roles) {
        Instant now = Instant.now();
        var expiredAt = now.plusSeconds(this.ttlSeconds);
        var expiredRefreshToken = now.plusSeconds(this.ttlSeconds * 5);
        var accessToken = this.generateToken(userId, expiredAt, roles);
        return new TokenResponse(
                accessToken,
                "Bearer",
                expiredAt.toEpochMilli()
        );
    }


}
