
package org.kaer.api_git_users_repo.controllers;

import org.kaer.api_git_users_repo.dto.GithubResponseDto;
import org.kaer.api_git_users_repo.mappers.GithubResponseMapper;
import org.kaer.api_git_users_repo.mappers.UrlBuilder;
import org.kaer.api_git_users_repo.services.GithubRepositoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

    @RestController
    @RequestMapping("/api")
    public class GithubRepositoryController {

        private final GithubRepositoryService githubRepositoryService;
        private final UrlBuilder urlBuilder;


        public GithubRepositoryController(GithubRepositoryService githubRepositoryService, UrlBuilder urlBuilder) {
            this.githubRepositoryService = githubRepositoryService;
            this.urlBuilder = urlBuilder;
        }

        @GetMapping("/repositories/{username}")
        public ResponseEntity<GithubResponseDto> getRepositories(
                @PathVariable String username,
                @RequestParam(required = false) Integer page,
                @RequestParam(required = false) Integer pageSize) {
            try {
                GithubResponseDto responseDto = githubRepositoryService.getRepositories(username, page, pageSize);

                if (page != null && pageSize != null) {
                    String nextPageLink = urlBuilder.buildNextPageLink(username, page, pageSize);
                    responseDto.getPagination().setNextPageLink(nextPageLink);
                }

                return ResponseEntity.ok(responseDto);

            } catch (HttpClientErrorException.NotFound e) {
                GithubResponseDto errorDto = new GithubResponseDto();
                errorDto.setStatus(HttpStatus.NOT_FOUND.value());
                errorDto.setMessage("User not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorDto);
            }
        }
    }
