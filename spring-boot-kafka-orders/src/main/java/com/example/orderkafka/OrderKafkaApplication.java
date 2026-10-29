package com.example.orderkafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.TopicBuilder;

@SpringBootApplication
public class OrderKafkaApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderKafkaApplication.class, args);
    }

    @Bean
    NewTopic ordersCreatedTopic(@Value("${app.kafka.orders-created-topic}") String topicName) {
        return TopicBuilder.name(topicName).partitions(1).replicas(1).build();
    }
}
