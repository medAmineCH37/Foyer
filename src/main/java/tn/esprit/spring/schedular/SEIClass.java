package tn.esprit.spring.schedular;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
public class SEIClass {

    @Scheduled(fixedDelay = 3000)
    public void fixedDelayMethod() {
        log.info("Hello fixedDelay "+ LocalDateTime.now());
    }

    @Scheduled(fixedRate = 3000)
    public void fixedRateMethod() {
       log.error("Hello fixedRate "+ LocalDateTime.now());
    }
}
