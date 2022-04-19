package app.kafka.errordemo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ApplicationProperties {
    // Server settings.
    @Value("${server.port}")
    public String port;

    @Value("${server.baseurl}")
    public String baseurl;

    // Kafka broker hostname.
    @Value("${spring.kafka.bootstrap-servers}")
    public String bootstrapServers;

    // Topic names.
    @Value("${app.topic.rewards}")
    public String rewardsTopic;

    // Consumers.
    @Value("${app.rewards.kafka.consumer.group-id}")
    public String rewardsConsumerGroupId;
}
