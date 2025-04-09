package com.codeone.hubspot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class HubspotApplicationConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}