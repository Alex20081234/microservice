package com.epam.microservice.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ComponentScan("com.epam.microservice")
@PropertySource("classpath:application.properties")
@PropertySource("classpath:application-${spring.profiles.active}.properties")
public class MicroserviceConfiguration {

    @Bean
    public String deadLetterQueueName() {
        return "submit-workload-dead-letter-queue";
    }
}
