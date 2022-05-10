package app.kafka.mergereplayapp;

import java.util.Map;

import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.AbstractConsumerSeekAware;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import app.kafka.mergereplayapp.reward.Reward;
import app.kafka.mergereplayapp.reward.RewardProducer;
import lombok.extern.slf4j.Slf4j;

import org.springframework.kafka.support.KafkaHeaders;

@Component
@Slf4j
public class Merger extends AbstractConsumerSeekAware {
    @Autowired
    private ApplicationProperties applicationProperties;

    @Autowired
    private RewardProducer rewardProducer;

    @KafkaListener(id = "${app.rewards.kafka.consumer.group-id}", topics = "${app.topic.rewards.consumer.dlt}")
    public void process(ConsumerRecord<Integer, Object> record,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic, @Header(KafkaHeaders.OFFSET) long offset) {
        log.info(String.format("process: Received <- key: %s. value: %s in topic: %s, offset: %s",
                record.key(), record.value(), topic, offset));

        var sr = (SpecificRecord) record.value();
        var customerId = Integer.parseInt(sr.get(0).toString());
        var membershipId = sr.get(1).toString();
        var programme = sr.get(2).toString();

        Reward reward = new Reward(customerId, membershipId, programme);
        rewardProducer.resendRewardCreatedMessage(reward);
    }

    @Override
    public void onPartitionsAssigned(Map<TopicPartition, Long> assignments, ConsumerSeekCallback callback) {
        log.info("Assignments:" + assignments);
        super.onPartitionsAssigned(assignments, callback);

        callback.seekRelative(applicationProperties.rewardsConsumerDLT, applicationProperties.partition,
                applicationProperties.offset, applicationProperties.seekToCurrent);
    }
}