package org.microservice.tenantservice.service.impl;

import io.jsonwebtoken.Jwts;
import org.microservice.tenantservice.service.JwtService;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtServiceImpl implements JwtService {

    private static final long EXPIRATION = 24 * 60 * 60 * 1000;
    private static final String SECRET = "J5bTVqv5h2RREWAqLshZ+f2oWG8IqirEcRYLAFI8nXI=";

    private static SecretKey generalKey() {
        byte[] encodedKey = Base64.getEncoder().encode(SECRET.getBytes());
        return new SecretKeySpec(encodedKey, 0, encodedKey.length, "HmacSHA256");
    }

    @Override
    public String generateToken(String email, String tenantId, String role) {
        Map<String, Object> claims = new HashMap<>();
        if (tenantId != null) {
            claims.put("tenantId", tenantId);
        }
        claims.put("role", role);

        Date now = new Date();

        return Jwts.builder()
                .claims()
                .add(claims)
                .subject(email)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + EXPIRATION))
                .and()
                .signWith(generalKey())
                .compact();
    }
}
