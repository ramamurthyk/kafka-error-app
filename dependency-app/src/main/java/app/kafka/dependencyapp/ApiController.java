package app.kafka.dependencyapp;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class ApiController {
    @PostMapping(path = "/api/external")
    public void process(@RequestBody Customer customer) {
        log.info("Processing customer: " + customer.customerId());
    }
}
