package com.codeone.hubspot.service;

import org.springframework.stereotype.Service;

@Service
public class TokenService {

    private String accessToken;

    public void saveToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getToken() {
        return accessToken;
    }

    public boolean isTokenAvailable() {
        return accessToken != null;
    }
}
