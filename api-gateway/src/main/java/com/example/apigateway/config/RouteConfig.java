package com.example.apigateway.config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;

@Configuration
public class RouteConfig {
    private final JwtAuthFilter filter;

    public RouteConfig(JwtAuthFilter config) {
        this.filter = config;
    }

    @Value("${services.auth.url}")
    private String AUTH_URL;

    @Value("${services.animal.url}")
    private String ANIMAL_URL;

    @Value("${services.favorite.url}")
    private String FAVORITE_URL;

    @Value("${services.notification.url}")
    private String NOTIFICATION_URL;


    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder){
        return builder.routes()
                .route(
                        "auth", r -> r
                                .path("/api/v1/sign/**")
                                .uri(AUTH_URL)
                )
                .route(
                        "user-read", r -> r
                                .path("/api/v1/user/**")
                                .and().method(HttpMethod.GET)
                                .uri(AUTH_URL)
                )
                .route(
                        "user-write", r -> r
                                .path("/api/v1/user/**")
                                .filters(f->f.filter(filter))
                                .uri(AUTH_URL)
                )
                .route(
                        "animal-read", r -> r
                                .path("/api/v1/animal/**")
                                .and().method(HttpMethod.GET)
                                .uri(ANIMAL_URL)
                )
                .route(
                        "animal-write", r -> r
                                .path("/api/v1/animal/**")
                                .filters(f->f.filter(filter))
                                .uri(ANIMAL_URL)
                )
                .route(
                        "favorite", r -> r
                                .path("/api/v1/favorite/**")
                                .filters(f->f.filter(filter))
                                .uri(FAVORITE_URL)
                )
                .route(
                        "notification", r -> r
                                .path("/api/v1/notifications/**")
                                .filters(f->f.filter(filter))
                                .uri(NOTIFICATION_URL)
                )
                .route("user-docs", r -> r
                        .path("/v3/api-docs/user")
                        .filters(f -> f.rewritePath("/v3/api-docs/user", "/v3/api-docs"))
                        .uri(AUTH_URL))
                .route("animal-docs", r -> r
                        .path("/v3/api-docs/animal")
                        .filters(f -> f.rewritePath("/v3/api-docs/animal", "/v3/api-docs"))
                        .uri(ANIMAL_URL))
                .route("favorite-docs", r -> r
                        .path("/v3/api-docs/favorite")
                        .filters(f -> f.rewritePath("/v3/api-docs/favorite", "/v3/api-docs"))
                        .uri(FAVORITE_URL))
                .route("notification-docs", r -> r
                        .path("/v3/api-docs/notification")
                        .filters(f -> f.rewritePath("/v3/api-docs/notification", "/v3/api-docs"))
                        .uri(NOTIFICATION_URL))
                .build();
    }
}
