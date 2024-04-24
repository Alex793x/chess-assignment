package config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigLoader {
    private final Properties properties;

    public ConfigLoader() {
        properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            properties.load(input);
        } catch (IOException | NullPointerException e) {
            throw new RuntimeException("Error loading configuration", e);
        }
    }

    public int getServerPort() {
        return Integer.parseInt(properties.getProperty("server.port", "7000"));  // Default to 7000 if not specified
    }
}
