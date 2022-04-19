package app.kafka.errordemo.reward;

import java.nio.charset.StandardCharsets;

import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Headers;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
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

    @RetryableTopic(attempts = "5", backoff = @Backoff(delay = 2_000, maxDelay = 10_000, multiplier = 2))
    @KafkaListener(id = "${app.rewards.kafka.consumer.group-id}", topics = "${app.topic.rewards}")
    public void process(ConsumerRecord<Integer, Object> record,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic, @Header(KafkaHeaders.OFFSET) long offset) {
        log.info(String.format("process: Received <- key: %s. value: %s in topic: %s, offset: %s",
                record.key(), record.value(), topic, offset));
        // process(record.headers(), record.value());

        // DEBUG stuffs:
        log.info("name: " + record.value().getClass().getName());
        var sr = (SpecificRecord) record.value();
        var programme = sr.get(2).toString();
        log.info("programme: " + programme);

        if (programme.contains("fail")) {
            throw new RuntimeException("failed");
        }
    }

    @DltHandler
    public void processDlt(ConsumerRecord<Integer, Object> record,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic, @Header(KafkaHeaders.OFFSET) long offset) {
        log.info(String.format("processDlt: Received <- key: %s. value: %s in topic: %s, offset: %s",
                record.key(), record.value(), topic, offset));
        // process(record.headers(), record.value());

        // DEBUG stuffs:
        log.info("name: " + record.value().getClass().getName());
        var sr = (SpecificRecord) record.value();
        var programme = sr.get(2).toString();
        log.info("programme: " + programme);
    }
}
