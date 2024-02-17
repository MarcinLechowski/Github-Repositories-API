package org.kaer.api_git_users_repo.mappers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class UrlBuilder {

    private final String githubApiUrl;

    public UrlBuilder(@Value("${github.api.url}") String githubApiUrl) {
        this.githubApiUrl = githubApiUrl;
    }

    public String buildUserReposUrl(String username, int page, int pageSize) {
        return UriComponentsBuilder.fromUriString(githubApiUrl)
                .path("/users/{username}/repos")
                .queryParam("page", page)
                .queryParam("per_page", pageSize)
                .buildAndExpand(username)
                .toUriString();
    }

    public String buildRepositoryBranchesUrl(String username, String repositoryName) {
        return UriComponentsBuilder.fromUriString(githubApiUrl)
                .path("/repos/{username}/{repositoryName}/branches")
                .buildAndExpand(username, repositoryName)
                .toUriString();
    }

    public String buildBranchCommitsUrl(String username, String repositoryName, String branchName) {
        return UriComponentsBuilder.fromUriString(githubApiUrl)
                .path("/repos/{username}/{repositoryName}/commits")
                .queryParam("sha", branchName)
                .queryParam("per_page", 1)
                .buildAndExpand(username, repositoryName)
                .toUriString();
    }

    public String buildUserUrl(String username) {
        return UriComponentsBuilder.fromUriString(githubApiUrl)
                .path("/users/{username}")
                .buildAndExpand(username)
                .toUriString();
    }

    public String buildPageLink(String username, int page, int pageSize) {
        return UriComponentsBuilder.fromUriString("http://localhost:8080/api/repositories/{username}")
                .queryParam("page", page)
                .queryParam("pageSize", pageSize)
                .buildAndExpand(username)
                .toUriString();
    }
    public String buildNextPageLink(String username, int currentPage, int pageSize) {
        int nextPage = currentPage + 1;

        return UriComponentsBuilder.fromUriString("http://localhost:8080/api/repositories/{username}")
                .queryParam("page", nextPage)
                .queryParam("pageSize", pageSize)
                .buildAndExpand(username)
                .toUriString();
    }
    public String buildPreviousPageLink(String username, int currentPage, int pageSize) {
        if (currentPage <= 1) {
            return null;
        }
        int previousPage = currentPage - 1;

        return UriComponentsBuilder.fromUriString("http://localhost:8080/api/repositories/{username}")
                .queryParam("page", previousPage)
                .queryParam("pageSize", pageSize)
                .buildAndExpand(username)
                .toUriString();
    }

}

