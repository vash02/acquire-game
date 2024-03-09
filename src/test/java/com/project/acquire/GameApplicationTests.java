package com.project.acquire;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT,
        properties = {"spring.cloud.config.enabled=false"})
@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class})
class GameApplicationTests {

    @Test
    void contextLoads() {
    }

}
