package com.tomlisow.githubapi.model;

import java.util.List;

public record RepositoriesResult(String repoName,
                                 String userLogin,
                                 List<GitHubBranch> branches) {
}
