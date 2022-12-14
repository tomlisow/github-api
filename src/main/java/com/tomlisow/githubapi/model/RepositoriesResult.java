package com.tomlisow.githubapi.model;

public record RepositoriesResult(String repoName,
                                 String userLogin,
                                 GitHubBranch[] branches) {
}
