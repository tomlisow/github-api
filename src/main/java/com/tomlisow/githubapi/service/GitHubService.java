package com.tomlisow.githubapi.service;

import com.tomlisow.githubapi.model.GitHubBranch;
import com.tomlisow.githubapi.model.GitHubRepository;
import com.tomlisow.githubapi.model.RepositoriesResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class GitHubService {
    final GitHubClient gitHubClient;

    public List<RepositoriesResult> getRepositoriesResult(String username) {
        log.debug("{} triggered getRepositoriesResult({})", getClass().getName(), username);
        return buildRepositoriesResults(gitHubClient.getRepositoriesForUsername(username));
    }


    private List<RepositoriesResult> buildRepositoriesResults(List<GitHubRepository> gitHubRepositories) {
        List<RepositoriesResult> result = new ArrayList<>();
        log.debug("{} triggered buildRepositoriesResults(..) for {} repositories", getClass().getName(), gitHubRepositories.size());
        for (GitHubRepository repo : gitHubRepositories) {
            if (repo.fork()) {
                continue;
            }
            List<GitHubBranch> branches = gitHubClient.getBranchesFromRepository(repo);
            log.debug("{} branches were received", branches.size());
            RepositoriesResult repositoriesResult = new RepositoriesResult(
                    repo.name(),
                    repo.owner().login(),
                    branches);
            result.add(repositoriesResult);
            log.debug("{} added to the results", repositoriesResult);
        }
        return result;
    }
}
