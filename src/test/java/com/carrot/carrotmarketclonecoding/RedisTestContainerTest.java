package com.carrot.carrotmarketclonecoding;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;

public abstract class RedisTestContainerTest {
    static final String REDIS_IMAGE = "redis:7.4.0-alpine";
    static final int REDIS_PORT = 6379;
    static final GenericContainer REDIS_CONTAINER;

    static {
        REDIS_CONTAINER = new GenericContainer<>(REDIS_IMAGE)
                .withExposedPorts(REDIS_PORT)
                .withReuse(true);
        REDIS_CONTAINER.start();
    }

    @DynamicPropertySource
    public static void overrideProps(DynamicPropertyRegistry registry){
        registry.add("spring.data.redis.host", REDIS_CONTAINER::getHost);
        registry.add("spring.data.redis.port", () -> "" + REDIS_CONTAINER.getMappedPort(REDIS_PORT));
    }
}
