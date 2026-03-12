package lk.helphub.api.application.services.impl;

import lk.helphub.api.application.services.SmsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@Slf4j
public class TextitSmsServiceImpl implements SmsService {

    private static final String TEXTIT_API_URL = "https://api.textit.biz";

    @Value("${textit.api-key}")
    private String apiKey;

    private final RestTemplate restTemplate;

    public TextitSmsServiceImpl() {
        this.restTemplate = new RestTemplate();
    }

    @Override
    public void sendSms(String to, String message) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Accept", "*/*");
            headers.set("X-API-VERSION", "v1");
            headers.set("Authorization", "Basic " + apiKey);

            Map<String, String> body = Map.of(
                    "to", to,
                    "text", message
            );

            HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);
            String response = restTemplate.postForObject(TEXTIT_API_URL, request, String.class);
            log.info("SMS sent to {}, response: {}", to, response);
        } catch (Exception e) {
            log.error("Failed to send SMS to {}: {}", to, e.getMessage(), e);
            throw new RuntimeException("Failed to send SMS: " + e.getMessage(), e);
        }
    }
}
