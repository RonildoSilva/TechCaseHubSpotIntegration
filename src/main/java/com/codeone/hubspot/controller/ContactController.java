package com.codeone.hubspot.controller;

import com.codeone.hubspot.service.TokenService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@RestController
@Tag(name = "Contact", description = "Contact operations")
public class ContactController {

    @Value("${hubspot.oauth.uri}")
    private String hubspotOAuthURI;

    private final TokenService tokenService;
    private final RestTemplate restTemplate;

    public ContactController(TokenService tokenService, RestTemplate restTemplate) {
        this.tokenService = tokenService;
        this.restTemplate = restTemplate;
    }

    @PostMapping("/contacts")
    public ResponseEntity<Object> create(@RequestBody Map<String, Object> contactDetails) {
        if (!tokenService.isTokenAvailable()) {
            return ResponseEntity.status(
                    HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid or missing credentials.")
            );
        }

        URI hubSpoturi = URI.create(hubspotOAuthURI);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(tokenService.getToken());
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("properties", contactDetails);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    hubSpoturi,
                    HttpMethod.POST,
                    request,
                    new ParameterizedTypeReference<>() {}
            );

            return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
        } catch (HttpStatusCodeException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString());
        } catch (RestClientException restClientException){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(restClientException.getMessage());
        }
    }
}
