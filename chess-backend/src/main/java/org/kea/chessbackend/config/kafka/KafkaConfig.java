package org.kea.chessbackend.config.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.afterburner.AfterburnerModule;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;


@Configuration
@EnableKafka
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.properties.security.protocol}")
    private String securityProtocol;

    @Value("${spring.kafka.properties.sasl.mechanism}")
    private String saslMechanism;

    @Value("${spring.kafka.properties.sasl.jaas.config}")
    private String saslJaasConfig;

    @Value("${spring.kafka.properties.concurrency-value}")
    private Integer concurrencyValue;
}
