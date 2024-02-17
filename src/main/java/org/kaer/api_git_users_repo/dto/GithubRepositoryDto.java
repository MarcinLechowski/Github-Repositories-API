package org.kaer.api_git_users_repo.dto;

import lombok.Data;

import java.util.List;

@Data
public class GithubRepositoryDto {
    private String repositoryName;
    private String userLogin;
    private List<BranchDto> branches;
}
