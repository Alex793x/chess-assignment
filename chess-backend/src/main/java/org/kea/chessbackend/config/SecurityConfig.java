package org.kea.chessbackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtGrantedAuthoritiesConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.savedrequest.NoOpServerRequestCache;


@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    SecurityWebFilterChain filterChain(ServerHttpSecurity http) {
        return http
                .authorizeExchange(exchange -> exchange.anyExchange().authenticated())
                .oauth2ResourceServer(resourceServer -> resourceServer.jwt(Customizer.withDefaults()))
                .requestCache(requestCacheSpec ->
                        requestCacheSpec.requestCache(NoOpServerRequestCache.getInstance()))
                .csrf(ServerHttpSecurity.CsrfSpec::disable)

                .build();
    }

    @Bean
    public ReactiveJwtAuthenticationConverter reactiveJwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");
        jwtGrantedAuthoritiesConverter.setAuthoritiesClaimName("roles");

        ReactiveJwtGrantedAuthoritiesConverterAdapter reactiveJwtGrantedAuthoritiesConverterAdapter = new ReactiveJwtGrantedAuthoritiesConverterAdapter(jwtGrantedAuthoritiesConverter);

        ReactiveJwtAuthenticationConverter reactiveJwtAuthenticationConverter = new ReactiveJwtAuthenticationConverter();
        reactiveJwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(reactiveJwtGrantedAuthoritiesConverterAdapter);

        return reactiveJwtAuthenticationConverter;
    }

}
