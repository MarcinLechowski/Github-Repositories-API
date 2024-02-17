package org.kaer.api_git_users_repo.services;

import org.kaer.api_git_users_repo.dto.*;
import org.kaer.api_git_users_repo.mappers.GithubPaginationHandler;
import org.kaer.api_git_users_repo.mappers.GithubResponseMapper;
import org.kaer.api_git_users_repo.mappers.UrlBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class GithubRepositoryService {

    private final RestTemplate restTemplate;
    private final UrlBuilder urlBuilder;
    private final GithubResponseMapper responseMapper;

    public GithubRepositoryService(RestTemplate restTemplate, UrlBuilder urlBuilder, GithubResponseMapper responseMapper) {
        this.restTemplate = restTemplate;
        this.urlBuilder = urlBuilder;
        this.responseMapper = responseMapper;
    }

    public GithubResponseDto getRepositories(String username, Integer page, Integer pageSize) {
        if (username == null) {
            throw new IllegalArgumentException("Username cannot be null");
        }

        try {
            String url = urlBuilder.buildUserReposUrl(username, page, pageSize);
            ResponseEntity<List<Map<?, ?>>> responseEntity = restTemplate.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<List<Map<?, ?>>>() {});
            List<Map<?, ?>> repositoriesResponse = responseEntity.getBody();

            if (repositoriesResponse != null) {
                return responseMapper.mapToResponseDto(username, repositoriesResponse, page, pageSize);
            } else {

                GithubResponseDto emptyResponseDto = new GithubResponseDto();
                emptyResponseDto.setItems(List.of());
                emptyResponseDto.setTotal(0);
                emptyResponseDto.setStatus(HttpStatus.OK.value());
                emptyResponseDto.setMessage("No repositories found for the user.");

                return emptyResponseDto;
            }
        } catch (RestClientException e) {
            throw new RuntimeException("Error communicating with GitHub API", e);
        }
    }
}