package org.kaer.api_git_users_repo.dto;

import lombok.Data;

@Data
public class BranchDto {
    private String name;
    private String lastCommitSha;
}
