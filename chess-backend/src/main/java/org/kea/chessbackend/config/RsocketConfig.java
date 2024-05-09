package org.kea.chessbackend.config;

import io.rsocket.core.Resume;
import io.rsocket.frame.decoder.PayloadDecoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.rsocket.server.RSocketServerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.RSocketStrategies;
import org.springframework.messaging.rsocket.annotation.support.RSocketMessageHandler;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.util.pattern.PathPatternRouteMatcher;
import reactor.util.retry.Retry;


import java.time.Duration;
import java.util.Map;

@Configuration
public class RsocketConfig {

    @Value("${rsocket-chess-engine.host}")
    String rsocketChessEngineHost;

    @Value("${rsocket-chess-engine.port}")
    int rsocketChessEnginePort;

    @Bean
    public RSocketStrategies rSocketStrategies() {
        return RSocketStrategies.builder()
                .metadataExtractorRegistry(registry -> {
                    registry.metadataToExtract(MimeTypeUtils.APPLICATION_JSON, Map.class, "headers");
                })
                .decoder(new Jackson2JsonDecoder())
                .encoder(new Jackson2JsonEncoder())
                .build();
    }

    @Bean
    public RSocketMessageHandler messageHandler(RSocketStrategies socketStrategies) {
        RSocketMessageHandler handler = new RSocketMessageHandler();
        handler.setRSocketStrategies(socketStrategies);
        handler.setRouteMatcher(new PathPatternRouteMatcher());
        return handler;
    }

    @Bean
    public Resume resume() {
        return new Resume()
                .sessionDuration(Duration.ofMinutes(5))
                .cleanupStoreOnKeepAlive()
                .retry(Retry.backoff(10, Duration.ofSeconds(1)).maxBackoff(Duration.ofMinutes(1)));
    }

    @Bean
    public RSocketServerCustomizer rSocketServerCustomizer() {
        return rSocketServer -> rSocketServer
                .payloadDecoder(PayloadDecoder.ZERO_COPY)
                .fragment(65536)
                .resume(resume());
    }

    @Bean
    public RSocketRequester chessEngineRsocketRequester(RSocketRequester.Builder builder) {
        return builder
                .rsocketStrategies(rSocketStrategies())
                .tcp(rsocketChessEngineHost, rsocketChessEnginePort);

    }
}
