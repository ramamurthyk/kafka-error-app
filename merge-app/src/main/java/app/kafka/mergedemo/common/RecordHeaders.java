package app.kafka.mergedemo.common;

import java.nio.charset.StandardCharsets;

import org.apache.kafka.common.header.Headers;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class RecordHeaders {
    public static void log(Headers headers) {
        log.info("With record headers:");
        headers.forEach(header -> log
                .info(String.format("%s: %s", header.key(), new String(header.value(), StandardCharsets.UTF_8))));
    }
}
