package com.codeone.hubspot.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@Tag(name = "Webhook", description = "Webhook operations")
public class WebhookController {

    private static final Logger logger = LoggerFactory.getLogger(WebhookController.class);

    @Value("${hubspot.webhook-secret}")
    private String webhookSecret;

    @PostMapping("/webhook")
    public ResponseEntity<Map<String, String>> handleWebhook(
            @RequestBody String payload,
            @RequestHeader(value = "X-HubSpot-Signature", required = false) String signature) {

        if (!isValidSignature(payload, signature)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).
                    body(Map.of("error", "Invalid signature"));
        }

        logger.info("Webhook received:");
        logger.info(payload);

        return ResponseEntity.status(HttpStatus.OK).
                body(Map.of("message", "Webhook received successfully."));
    }

    private boolean isValidSignature(
            String payload, String signature) {
        try {
            String algorithm = "HmacSHA256";
            Mac mac = Mac.getInstance(algorithm);
            SecretKeySpec secretKeySpec =
                    new SecretKeySpec(webhookSecret.getBytes(StandardCharsets.UTF_8), algorithm);

            mac.init(secretKeySpec);
            byte[] hash = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            String expectedSignature = Base64.getEncoder().encodeToString(hash);
            return expectedSignature.equals(signature);
        } catch (Exception exception) {
            logger.error(exception.getMessage());
            return false;
        }
    }
}