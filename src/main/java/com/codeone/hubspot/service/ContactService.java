package com.codeone.hubspot.service;

import com.codeone.hubspot.dto.ContactDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@Service
public class ContactService {

    @Value("${hubspot.oauth.uri}")
    private String hubspotOAuthURI;

    private final TokenService tokenService;
    private final RestTemplate restTemplate;

    public ContactService(TokenService tokenService, RestTemplate restTemplate) {
        this.tokenService = tokenService;
        this.restTemplate = restTemplate;
    }

    @Retryable(
            retryFor = HttpClientErrorException.class,
            maxAttempts = 2,
            backoff = @Backoff(delay = 1090)
    )
    public ResponseEntity<Map<String, Object>> create(ContactDTO contactDTO) {
        URI hubSpoturi = URI.create(hubspotOAuthURI);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(tokenService.getToken());
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("properties", contactDTO);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        return restTemplate.exchange(
                hubSpoturi,
                HttpMethod.POST,
                request,
                new ParameterizedTypeReference<>() {}
        );
    }
}
