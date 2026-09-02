    package com.example.userservice.service;

    import io.jsonwebtoken.Jwts;
    import io.jsonwebtoken.io.Decoders;
    import io.jsonwebtoken.security.Keys;
    import org.springframework.beans.factory.annotation.Value;
    import org.springframework.stereotype.Component;

    import javax.crypto.SecretKey;
    import java.util.Date;
    import java.util.UUID;

    @Component
    public class JwtService {
        @Value("${jwt.secret}")
        private String SECRET;

        @Value("${jwt.expire}")
        private Long EXPIRE_ACCESS_TOKEN;

        @Value("${jwt.expire2}")
        private Long EXPIRE_REFRESH_TOKEN;

        private SecretKey getSignInKey(){
            byte[] keyBytes = Decoders.BASE64.decode(SECRET);
            return Keys.hmacShaKeyFor(keyBytes);
        }

        public String generateAccessToken(UUID userId){
            return Jwts.builder()
                    .claim("userId", userId)
                    .expiration(new Date(System.currentTimeMillis() + EXPIRE_ACCESS_TOKEN))
                    .signWith(getSignInKey())
                    .compact();
        }

        public String generateRefreshToken(UUID userId){
            return Jwts.builder()
                    .claim("userId", userId)
                    .expiration(new Date(System.currentTimeMillis() + EXPIRE_REFRESH_TOKEN))
                    .signWith(getSignInKey())
                    .compact();
        }

        public UUID getUser(String refreshToken){
            var result =Jwts.parser()
                    .verifyWith(getSignInKey())
                    .build()
                    .parseSignedClaims(refreshToken)
                    .getPayload();
            if (result.getExpiration().before(new Date())){
                throw new RuntimeException("sorry token expired");
            }
            return UUID.fromString(result.get("userId", String.class));
        }


    }
