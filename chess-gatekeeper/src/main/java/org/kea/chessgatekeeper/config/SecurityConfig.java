package org.kea.chessgatekeeper.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.client.oidc.web.server.logout.OidcClientInitiatedServerLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.server.WebSessionServerOAuth2AuthorizedClientRepository;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.security.web.server.authentication.logout.ServerLogoutSuccessHandler;
import org.springframework.security.web.server.csrf.CookieServerCsrfTokenRepository;
import org.springframework.security.web.server.csrf.CsrfToken;
import org.springframework.security.web.server.csrf.ServerCsrfTokenRequestAttributeHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.server.WebFilter;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Arrays;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {


    @Bean
    ServerOAuth2AuthorizedClientRepository authorizedClientRepository() {
        return new WebSessionServerOAuth2AuthorizedClientRepository();
    }

    @Bean
    SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http, ReactiveClientRegistrationRepository clientRegistrationRepository) {
        CorsConfiguration corsConfig = getCorsConfiguration();

        // Apply the CORS configuration to all paths
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);

         //Custom authentication entry point that redirects to /entry
        ServerAuthenticationEntryPoint entryPoint = (exchange, e) -> {
            ServerHttpResponse response = exchange.getResponse();
            response.setStatusCode(HttpStatus.FOUND); // 302 redirect
            response.getHeaders().setLocation(URI.create("/entry"));
            return Mono.empty();
        };

        return http
                .cors(corsSpec -> corsSpec.configurationSource(source))
                .authorizeExchange(exchange -> exchange
                        .pathMatchers("/_next/**", "/static/**", "/_next/webpack-hmr", "/_next/static/**",  "/public/**", "/media/**", "/*.css", "/*.js", "/*.woff2", "/*.svg", "/favicon.ico", "/images/**","/", "/about", "/privacy-policy").permitAll()
                        .pathMatchers(HttpMethod.GET, "/login").permitAll()
                        .anyExchange().authenticated()
                )

                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint(entryPoint))
                .oauth2Login(oauth2Login -> oauth2Login
                        .authenticationSuccessHandler((webFilterExchange, authentication) -> {
                            ServerHttpResponse response = webFilterExchange.getExchange().getResponse();
                            response.setStatusCode(HttpStatus.FOUND); // 302 redirect
                            response.getHeaders().setLocation(URI.create("/home"));
                            return Mono.empty();
                        }))
                .logout(logout -> logout.logoutSuccessHandler(oidcLogoutSuccessHandler(clientRegistrationRepository)))
                .csrf(csrf -> csrf
                        .csrfTokenRepository(CookieServerCsrfTokenRepository.withHttpOnlyFalse())
                        .csrfTokenRequestHandler(new ServerCsrfTokenRequestAttributeHandler()))
                .build();
    }

    private static CorsConfiguration getCorsConfiguration() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.setAllowedOrigins(Arrays.asList("http://localhost", "https://intellioptima.com", "https://authenticate.intellioptima.com", "null")); // Add your specific origins here
        corsConfig.addAllowedMethod("*"); // or specify: HttpMethod.POST, HttpMethod.GET, etc.
        corsConfig.addAllowedHeader("*");
        corsConfig.setAllowCredentials(true);
        return corsConfig;
    }

    private ServerLogoutSuccessHandler oidcLogoutSuccessHandler(ReactiveClientRegistrationRepository clientRegistrationRepository) {
        var oidcLogoutSuccessHandler = new OidcClientInitiatedServerLogoutSuccessHandler(clientRegistrationRepository);
        oidcLogoutSuccessHandler.setPostLogoutRedirectUri("{baseUrl}/");
        return oidcLogoutSuccessHandler;
    }

    @Bean
    WebFilter csrfWebFilter() {
        return (exchange, chain) -> {
            String path = exchange.getRequest().getURI().getPath();
            // Check if the request path matches the one you want to exclude from CSRF token logic
            if (path.startsWith("/_next/static/") ||
                path.startsWith("/_next/image") ||
                path.startsWith("/static") || 
                path.startsWith("/favicon.ico") ||
                path.startsWith("/public") ||
                path.startsWith("/media") ||
                path.startsWith("/images/")) {
                // Log the bypass
                System.out.println("CSRF check bypassed for path: " + path);
                // Skip CSRF token logic for this path
                return chain.filter(exchange);
            } else {
                // Log the CSRF token application
                System.out.println("CSRF token required for path: " + path);
                // Apply CSRF token logic for other paths
                exchange.getResponse().beforeCommit(() -> {
                    Mono<CsrfToken> csrfToken = exchange.getAttribute(CsrfToken.class.getName());
                    if (csrfToken != null) {
                    }
                    return Mono.empty(); // Return an empty Mono to satisfy the callback's return type
                });
                return chain.filter(exchange);
            }
        };
    }
}
