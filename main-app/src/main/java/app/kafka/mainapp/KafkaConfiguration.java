package app.kafka.mainapp;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;

@Configuration
@EnableKafka
public class KafkaConfiguration {
    @Autowired
    private ApplicationProperties properties;

    // Topic creation.
    @Bean
    public NewTopic rewardsTopic() {
        return new NewTopic(properties.rewardsTopic, 1, (short) 1);
    }
}
