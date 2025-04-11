package com.codeone.hubspot.controller;

import com.codeone.hubspot.service.TokenService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@RestController
@Tag(name = "OAuth", description = "Hubspot OAuth operations")
public class OAuthController {

    @Value("${hubspot.client-id}")
    private String clientId;

    @Value("${hubspot.client-secret}")
    private String clientSecret;

    @Value("${hubspot.redirect-uri}")
    private String redirectUri;

    @Value("${hubspot.oauth.token.uri}")
    private String oauthTokenUri;

    @Value("${hubspot.authorize.uri}")
    private String oauthAuthorizeUri;

    private final TokenService tokenService;
    private final RestTemplate restTemplate;

    public OAuthController(TokenService tokenService, RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.tokenService = tokenService;
    }

    @GetMapping(value = "/oauth/authorize", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> getAuthorizationUrl(HttpServletRequest request) {
        String baseUrl = request.getRequestURL().toString().replace(request.getRequestURI(), "");
        String dynamicRedirectUri = baseUrl + "/oauth/callback";

        URI uri = UriComponentsBuilder.fromUriString(oauthAuthorizeUri)
                .queryParam("client_id", clientId)
                .queryParam("redirect_uri", dynamicRedirectUri)
                .queryParam("scope", "crm.objects.contacts.write crm.objects.contacts.read")
                .queryParam("response_type", "code")
                .build()
                .toUri();

        return ResponseEntity.ok(Map.of("authorization_url", uri.toString()));
    }

    @GetMapping("/oauth/callback")
    public ResponseEntity<Map<String, Object>> oauthCallback(@RequestParam String code) {
        URI tokenUri = URI.create(oauthTokenUri);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("redirect_uri", redirectUri);
        body.add("code", code);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                tokenUri,
                HttpMethod.POST,
                request,
                new ParameterizedTypeReference<>() {}
        );

        Map<String, Object> result = new HashMap<>();
        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            String accessToken = (String) response.getBody().get("access_token");
            tokenService.saveToken(accessToken);
            result.put("message", "Access token received.");
        }
        else {
            result.put("message", "Failed to retrieve access token");
        }
        return ResponseEntity.ok(result);
    }

}
