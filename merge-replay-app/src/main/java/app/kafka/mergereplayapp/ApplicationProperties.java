package app.kafka.mergereplayapp;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ApplicationProperties {
    // Topic names.
    @Value("${app.topic.rewards.consumer.dlt}")
    public String rewardsConsumerDLT;

    @Value("${app.topic.rewards.consumer.retry-0}")
    public String rewardsConsumerRetryTopic;

    // Consumers.
    @Value("${app.rewards.kafka.consumer.group-id}")
    public String rewardsConsumerGroupId;

    @Value("${app.rewards.kafka.consumer.partition}")
    public int partition;

    @Value("${app.rewards.kafka.consumer.offset}")
    public long offset;

    @Value("${app.rewards.kafka.consumer.seekToCurrent}")
    public Boolean seekToCurrent;
}
