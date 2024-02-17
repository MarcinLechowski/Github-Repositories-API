package org.kaer.api_git_users_repo.dto;

import lombok.Data;

@Data
public class GithubPaginationDto {
    private Integer Page;
    private Integer PageSize;
    private Integer NextPage;
    private Integer PreviousPage;
    private String PreviousPageLink;
    private String NextPageLink;
    private String TotalPages;
}
