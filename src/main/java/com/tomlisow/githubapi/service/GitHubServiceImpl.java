package com.tomlisow.githubapi.service;

import com.tomlisow.githubapi.model.GitHubBranch;
import com.tomlisow.githubapi.model.GitHubRepository;
import com.tomlisow.githubapi.model.RepositoriesResult;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GitHubServiceImpl {
    final
    GitHubClient gitHubClient;

    public GitHubServiceImpl(GitHubClient gitHubClient) {
        this.gitHubClient = gitHubClient;
    }

    public List<RepositoriesResult> getRepositoriesResult(String username) {
        return buildRepositoriesResults(gitHubClient.getRepositoriesForUsername(username));
    }


    private List<RepositoriesResult> buildRepositoriesResults(List<GitHubRepository> gitHubRepositories) {
        List<RepositoriesResult> result = new ArrayList<>();

        for (GitHubRepository repo : gitHubRepositories) {
            if (repo.getFork()) {
                continue;
            }

            List<GitHubBranch> branches = gitHubClient.getBranchesFromRepository(repo);
            RepositoriesResult repositoriesResult = new RepositoriesResult(
                    repo.getName(),
                    repo.getOwner().login(),
                    branches);
            result.add(repositoriesResult);
        }
        return result;
    }
}
