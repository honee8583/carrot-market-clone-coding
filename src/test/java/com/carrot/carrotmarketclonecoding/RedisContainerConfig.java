package com.carrot.carrotmarketclonecoding;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

@ActiveProfiles("test")
public class RedisContainerConfig implements BeforeAllCallback {
    private static final String REDIS_IMAGE = "redis:7.4.0-alpine";
    private static final int REDIS_PORT = 6379;

    private GenericContainer redisContainer;

    @Override
    public void beforeAll(ExtensionContext context) {
        redisContainer = new GenericContainer(DockerImageName.parse(REDIS_IMAGE))
                .withExposedPorts(REDIS_PORT)
                .withReuse(true);
        redisContainer.start();

        System.setProperty("spring.data.redis.host", redisContainer.getHost());
        System.setProperty("spring.data.redis.port", redisContainer.getMappedPort(6379).toString());
    }
}
