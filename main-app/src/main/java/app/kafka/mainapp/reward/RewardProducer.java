package app.kafka.mainapp.reward;

import java.util.UUID;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import app.kafka.mainapp.ApplicationProperties;
import app.kafka.commonlib.MessageTypes;
import app.kafka.commonlib.RecordHeaderNames;
import app.kafka.commonlib.RecordHeadersLogger;
import app.kafka.errordemo.schema.RewardCreated;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class RewardProducer {
	@Autowired
	private KafkaTemplate<Integer, RewardCreated> template;

	@Autowired
	private ApplicationProperties properties;

	public void sendRewardCreatedMessage(Reward reward) {
		// Build message.
		RewardCreated rewardCreated = RewardCreated.newBuilder()
				.setCustomerId(reward.customerId())
				.setProgramme(reward.programme())
				.setMembershipId(reward.membershipId())
				.build();

		// Create record.
		ProducerRecord<Integer, RewardCreated> record = new ProducerRecord<Integer, RewardCreated>(
				properties.rewardsTopic, reward.customerId(), rewardCreated);

		// Add headers.
		record.headers().add(new RecordHeader(RecordHeaderNames.MESSAGE_ID,
				UUID.randomUUID().toString().getBytes()));
		record.headers()
				.add(new RecordHeader(RecordHeaderNames.MESSAGE_TYPE,
						MessageTypes.REWARD_CREATED.getBytes()));

		// Send.
		this.template.send(record);

		log.info(String.format("Produced -> key: %s, value: %s", record.key(),
				record.value()));
		RecordHeadersLogger.log(record.headers());
	}
}