package org.kaer.api_git_users_repo.configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ApiConfig {

    @Value("${github.api.url}")
    public String githubApiUrl;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}