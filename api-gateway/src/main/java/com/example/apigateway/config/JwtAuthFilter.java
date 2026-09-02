package com.example.apigateway.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;

@Component
public class JwtAuthFilter implements GatewayFilter {
    @Value("${jwt.secret}")
    private String SECRET_KEY;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String token = extractJwtFromRequest(
                exchange.getRequest().getHeaders().getFirst("Authorization")
        );
        var claims = jwtParser(token);

        var userId = claims.get("userId").toString();
        var request = exchange.getRequest()
                .mutate()
                .header("User-Id", userId)
                .build();
        var mutatedExchange = exchange.mutate()
                .request(request).build();

        return chain.filter(mutatedExchange);
    }

    private String extractJwtFromRequest(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "token not set");
        }
        return authorizationHeader.substring(7);
    }

    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private Claims jwtParser(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSignInKey()).build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    "token yanlisdir ve ya muddeti bitib");
        }
    }


}
