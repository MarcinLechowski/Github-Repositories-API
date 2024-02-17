package org.kaer.api_git_users_repo.mappers;

import org.kaer.api_git_users_repo.dto.BranchDto;
import org.kaer.api_git_users_repo.dto.GithubPaginationDto;
import org.kaer.api_git_users_repo.dto.GithubRepositoryDto;
import org.kaer.api_git_users_repo.dto.GithubResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class GithubResponseMapper {

    private final UrlBuilder urlBuilder;

    private final RestTemplate restTemplate;

    public GithubResponseMapper(UrlBuilder urlBuilder, RestTemplate restTemplate) {
        this.urlBuilder = urlBuilder;
        this.restTemplate = restTemplate;
    }

    public GithubResponseDto mapToResponseDto(String username, List<Map<?, ?>> repositoriesResponse, Integer currentPage, Integer pageSize) {
        GithubResponseDto responseDto = new GithubResponseDto();

        if (repositoriesResponse != null && !repositoriesResponse.isEmpty()) {
            List<GithubRepositoryDto> repositoryDtos = new ArrayList<>();
            for (Map<?, ?> repoMap : repositoriesResponse) {
                GithubRepositoryDto repoDto = mapToRepositoryDto(username, repoMap);
                repositoryDtos.add(repoDto);
            }

            int totalItems = getTotalRepositoriesCount(username);

            responseDto.setItems(repositoryDtos);
            responseDto.setTotal(totalItems);
            responseDto.setStatus(HttpStatus.OK.value());
            responseDto.setMessage("Repositories retrieved successfully");

            GithubPaginationDto paginationDto = GithubPaginationHandler.handlePagination(totalItems, currentPage, pageSize, username, urlBuilder);

            if (currentPage != null && pageSize != null) {
                String nextPageLink = urlBuilder.buildNextPageLink(username, currentPage, pageSize);
                paginationDto.setNextPageLink(nextPageLink);

                String previousPageLink = urlBuilder.buildPreviousPageLink(username, currentPage, pageSize);
                paginationDto.setPreviousPageLink(previousPageLink);
                paginationDto.setPreviousPage(currentPage - 1);
            }

            responseDto.setPagination(paginationDto);
        } else {
            responseDto.setMessage("No repositories found for the user.");
            responseDto.setTotal(0);
            responseDto.setStatus(HttpStatus.NOT_FOUND.value());
        }

        return responseDto;
    }

    private GithubRepositoryDto mapToRepositoryDto(String username, Map<?, ?> repositoryMap) {
        GithubRepositoryDto repositoryDto = new GithubRepositoryDto();

        repositoryDto.setRepositoryName((String) repositoryMap.get("name"));

        Map<?, ?> ownerMap = (Map<?, ?>) repositoryMap.get("owner");

        repositoryDto.setUserLogin((String) ownerMap.get("login"));

        repositoryDto.setBranches(getBranchesForRepository(username, repositoryDto.getRepositoryName()));

        return repositoryDto;
    }

    private List<BranchDto> getBranchesForRepository(String username, String repositoryName) {
        String branchesUrl = urlBuilder.buildRepositoryBranchesUrl(username, repositoryName);
        List<Map<?, ?>> branchesResponse = restTemplate.getForObject(branchesUrl, List.class);

        List<BranchDto> branchDtos = new ArrayList<>();

        if (branchesResponse != null && !branchesResponse.isEmpty()) {
            for (Map<?, ?> branchMap : branchesResponse) {
                BranchDto branchDto = new BranchDto();
                branchDto.setName((String) branchMap.get("name"));

                String lastCommitSha = getLastCommitShaForBranch(username, repositoryName, branchDto.getName());
                branchDto.setLastCommitSha(lastCommitSha);

                branchDtos.add(branchDto);
            }
        }
        return branchDtos;
    }

    private String getLastCommitShaForBranch(String username, String repositoryName, String branchName) {
        String commitsUrl = urlBuilder.buildBranchCommitsUrl(username, repositoryName, branchName);
        List<Map<?, ?>> commitsResponse = restTemplate.getForObject(commitsUrl, List.class);

        if (commitsResponse != null && !commitsResponse.isEmpty()) {
            Map<?, ?> lastCommitDetails = (Map<?, ?>) commitsResponse.get(0);
            return (String) lastCommitDetails.get("sha");
        }
        return null;
    }

    private int getTotalRepositoriesCount(String username) {
        String url = urlBuilder.buildUserUrl(username);
        Map<?, ?> userResponse = restTemplate.getForObject(url, Map.class);

        if (userResponse != null) {
            return (int) userResponse.get("public_repos");
        } else {
            return 0;
        }
    }
}