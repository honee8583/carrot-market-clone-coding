package com.carrot.carrotmarketclonecoding;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;

public abstract class MongoTestContainerTest {
    static final String MONGODB_IMAGE = "mongo:8.0.3";
    static final int MONGODB_PORT = 27017;
    static final GenericContainer MONGO_CONTAINER;

    static {
        MONGO_CONTAINER = new GenericContainer<>(MONGODB_IMAGE)
                .withExposedPorts(MONGODB_PORT)
                .withReuse(true);
        MONGO_CONTAINER.start();
    }

    @DynamicPropertySource
    public static void overrideProps(DynamicPropertyRegistry registry){
        registry.add("spring.data.mongodb.host", MONGO_CONTAINER::getHost);
        registry.add("spring.data.mongodb.port", () -> "" + MONGO_CONTAINER.getMappedPort(MONGODB_PORT));
    }
}
