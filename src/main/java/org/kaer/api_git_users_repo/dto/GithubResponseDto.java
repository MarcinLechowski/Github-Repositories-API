package org.kaer.api_git_users_repo.dto;

import lombok.Data;

import java.util.List;

@Data
public class GithubResponseDto {
    private int status;
    private String message;
    private List<GithubRepositoryDto> items;
    private GithubPaginationDto pagination;
    private int total;
}