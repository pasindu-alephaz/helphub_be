package lk.helphub.api.application.services.impl;

import lk.helphub.api.application.services.SmsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

/**
 * SMS service implementation using the textit.biz Basic HTTP API.
 *
 * API docs: https://textit.biz/integration_Basic_HTTP_API.php
 *
 * Request format:
 * GET
 * https://textit.biz/sendmsg/?id={username}&pw={password}&to={to}&text={url-encoded-message}
 *
 * Response format:
 * OK: {message-id} → success
 * FAIL: {error-code} → failure
 */
@Service
@Slf4j
public class TextitSmsServiceImpl implements SmsService {

    private static final String TEXTIT_API_BASE_URL = "https://www.textit.biz/sendmsg/";

    @Value("${textit.username}")
    private String username;

    @Value("${textit.password}")
    private String password;

    @Value("${textit.sms.enabled:false}")
    private boolean smsEnabled;

    private final RestTemplate restTemplate;

    public TextitSmsServiceImpl() {
        this.restTemplate = new RestTemplate();
    }

    @Override
    public void sendSms(String to, String message) {
        if (!smsEnabled) {
            log.info("SMS sending is disabled. Would have sent to {}: {}", to, message);
            return;
        }

        try {
            // DEBUG: Log the message before encoding to help diagnose issues
            log.debug("Original message: {}", message);

            // URL-encode the message properly to handle spaces and special characters
            String encodedMessage = java.net.URLEncoder.encode(message, java.nio.charset.StandardCharsets.UTF_8);
            log.debug("URL-encoded message: {}", encodedMessage);

            URI uri = UriComponentsBuilder.fromHttpUrl(TEXTIT_API_BASE_URL)
                    .queryParam("id", username)
                    .queryParam("pw", password)
                    .queryParam("to", to)
                    .queryParam("text", encodedMessage)
                    .build(false)
                    .toUri();

            log.debug("Full URI built: {}", uri.toString());

            String response = restTemplate.getForObject(uri, String.class);
            log.debug("Textit.biz raw response: {}", response);

            if (response != null && response.startsWith("OK")) {
                String messageId = response.contains(":") ? response.split(":", 2)[1].trim() : "unknown";
                log.info("SMS sent successfully to {}. Message ID: {}", to, messageId);
            } else {
                String errorCode = (response != null && response.contains(":"))
                        ? response.split(":", 2)[1].trim()
                        : response;
                log.error("SMS delivery failed for {}. Error: {}", to, errorCode);
                throw new RuntimeException("Textit.biz rejected the SMS. Error code: " + errorCode);
            }

        } catch (RuntimeException e) {
            throw e; // re-throw Textit errors as-is
        } catch (Exception e) {
            log.error("Failed to send SMS to {}: {}", to, e.getMessage(), e);
            throw new RuntimeException("Failed to send SMS: " + e.getMessage(), e);
        }
    }
}
