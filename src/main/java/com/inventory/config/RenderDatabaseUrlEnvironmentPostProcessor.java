package com.inventory.config;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

public class RenderDatabaseUrlEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        String databaseUrl = environment.getProperty("DATABASE_URL");

        String configuredDataSourceUrl = environment.getProperty("spring.datasource.url");
        if (databaseUrl == null || databaseUrl.isBlank()
                || (configuredDataSourceUrl != null && !configuredDataSourceUrl.isBlank())) {
            return;
        }

        if (databaseUrl.startsWith("jdbc:")) {
            environment.getPropertySources().addFirst(new MapPropertySource(
                    "renderDatabaseUrl",
                    Map.of("spring.datasource.url", databaseUrl,
                            "spring.datasource.driver-class-name", "org.postgresql.Driver")));
            return;
        }

        URI databaseUri = URI.create(databaseUrl);
        String databasePath = databaseUri.getRawPath();
        int port = databaseUri.getPort() == -1 ? 5432 : databaseUri.getPort();
        String jdbcUrl = "jdbc:postgresql://" + databaseUri.getHost() + ":" + port + databasePath;
        Map<String, Object> properties = new HashMap<>();
        properties.put("spring.datasource.url", jdbcUrl);
        properties.put("spring.datasource.driver-class-name", "org.postgresql.Driver");

        if (databaseUri.getRawUserInfo() != null) {
            String[] credentials = databaseUri.getRawUserInfo().split(":", 2);
            properties.put("spring.datasource.username", URLDecoder.decode(credentials[0], StandardCharsets.UTF_8));
            if (credentials.length == 2) {
                properties.put("spring.datasource.password", URLDecoder.decode(credentials[1], StandardCharsets.UTF_8));
            }
        }

        environment.getPropertySources().addFirst(new MapPropertySource(
                "renderDatabaseUrl",
                properties));
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
