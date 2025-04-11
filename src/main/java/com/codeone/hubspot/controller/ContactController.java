package com.codeone.hubspot.controller;

import com.codeone.hubspot.dto.ContactDTO;
import com.codeone.hubspot.service.ContactService;
import com.codeone.hubspot.service.TokenService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;

import java.util.Map;

@RestController
@Tag(name = "Contact", description = "Contact operations")
public class ContactController {

    @Value("${hubspot.oauth.uri}")
    private String hubspotOAuthURI;

    private final TokenService tokenService;
    private final ContactService contactService;

    public ContactController(TokenService tokenService, ContactService contactService) {
        this.tokenService = tokenService;
        this.contactService = contactService;
    }

    @PostMapping("/contact")
    public ResponseEntity<Object> create(@RequestBody ContactDTO contactDTO) {
        if (!tokenService.isTokenAvailable()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).
                    body(Map.of("error", "Invalid or missing credentials."));
        }

        try {
            ResponseEntity<Map<String, Object>> response = contactService.create(contactDTO);
            return ResponseEntity.status(response.getStatusCode()).
                    body(response.getBody());
        } catch (HttpStatusCodeException httpStatusCodeException) {
            return ResponseEntity.status(httpStatusCodeException.getStatusCode()).
                    body(httpStatusCodeException.getResponseBodyAsString());
        } catch (RestClientException restClientException) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).
                    body(restClientException.getMessage());
        }
    }

}
