package com.epam.microservice.configuration;

import com.epam.microservice.dto.SubmitWorkloadChangesRequestBody;
import com.epam.microservice.service.DeadLetterQueueSenderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.jms.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;

@Configuration
@EnableJms
public class JmsConfiguration {

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper().registerModule(new JavaTimeModule());
    }

    @Bean
    public MessageConverter jacksonJmsMessageConverter() {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setTargetType(org.springframework.jms.support.converter.MessageType.TEXT);
        converter.setTypeIdMappings(Map.of(
                "com.epam.gymsystem.dto.SubmitWorkloadChangesRequestBody", SubmitWorkloadChangesRequestBody.class
        ));
        converter.setTypeIdPropertyName("_type");
        converter.setObjectMapper(objectMapper());
        return converter;
    }

    @Bean
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory(ConnectionFactory connectionFactory, DeadLetterQueueSenderService service) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jacksonJmsMessageConverter());
        factory.setErrorHandler(t -> {
            StringWriter sw = new StringWriter();
            t.printStackTrace(new PrintWriter(sw));
            String exceptionDetails = sw.toString();
            service.sendMessage("Exception Details: " + exceptionDetails);
        });
        return factory;
    }
}
