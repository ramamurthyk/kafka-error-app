package app.kafka.errordemo.reward;

import java.nio.charset.StandardCharsets;

import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Headers;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.TopicSuffixingStrategy;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

import app.kafka.errordemo.common.MessageTypes;
import app.kafka.errordemo.common.RecordHeaderNames;
import app.kafka.errordemo.common.RecordHeaders;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class RewardConsumer {
    public void process(Headers headers, Object message) {
        // Log headers.
        RecordHeaders.log(headers);

        // Get MessageType and process.
        String messageType = new String(headers.lastHeader(RecordHeaderNames.MESSAGE_TYPE).value(),
                StandardCharsets.UTF_8);

        switch (messageType) {
            case MessageTypes.REWARD_CREATED:
                // Convert the message to a specific object..
                log.info(String.format("Processed messageType: %s", messageType));
                break;

            default:
                log.info(String.format("Processed messageType: %s", messageType));
                break;
        }
    }

    @RetryableTopic(attempts = "${app.rewards.kafka.consumer.retry-attempts}", retryTopicSuffix = "${app.rewards.kafka.consumer.retryTopicSuffix}", topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE, dltTopicSuffix = "${app.rewards.kafka.consumer.dltTopicSuffix}", backoff = @Backoff(delayExpression = "${app.rewards.kafka.consumer.backoff-delay-milliseconds}", maxDelayExpression = "${app.rewards.kafka.consumer.backoff-maxdelay-milliseconds}", multiplierExpression = "${app.rewards.kafka.consumer.backoff-multiplier}"))
    @KafkaListener(id = "${app.rewards.kafka.consumer.group-id}", topics = "${app.topic.rewards}")
    public void process(ConsumerRecord<Integer, Object> record,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic, @Header(KafkaHeaders.OFFSET) long offset) {
        log.info(String.format("process: Received <- key: %s. value: %s in topic: %s, offset: %s",
                record.key(), record.value(), topic, offset));

        // DEBUG stuffs:
        var sr = (SpecificRecord) record.value();
        var programme = sr.get(2).toString();

        if (programme.contains("fail")) {
            throw new RuntimeException("failed");
        }
    }

    @DltHandler
    public void processDlt(ConsumerRecord<Integer, Object> record,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic, @Header(KafkaHeaders.OFFSET) long offset) {
        log.info(String.format("processDlt: Received <- key: %s. value: %s in topic: %s, offset: %s",
                record.key(), record.value(), topic, offset));
    }
}
