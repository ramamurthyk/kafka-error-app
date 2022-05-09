package app.kafka.mainapp.reward;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class RewardController {
    @Autowired
    private RewardProducer rewardProducer;

    @PostMapping(path = "/api/rewards")
    public void CreateReward(@RequestBody Reward reward) {
        log.info("Received POST request to create reward");

        rewardProducer.sendRewardCreatedMessage(reward);
    }
}
