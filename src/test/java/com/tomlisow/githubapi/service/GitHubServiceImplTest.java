package com.tomlisow.githubapi.service;

import com.tomlisow.githubapi.model.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GitHubServiceImplTest {

    @Mock
    GitHubClient gitHubClient;

    @InjectMocks
    private GitHubServiceImpl gitHubService;

    private final GitHubRepositoryOwner owner = new GitHubRepositoryOwner("userLogin");
    private final GitHubBranch branch1 = new GitHubBranch("main", new GitHubCommit("sha1-hash"));
    private final List<GitHubBranch> branches = List.of(branch1);
    private final GitHubRepository repository = new GitHubRepository(
            "repo-name",
            owner,
            false,
            "branchesUrl"
    );
    private final List<GitHubRepository> repositories = List.of(repository);
    @Test
    public void test() {
        List<RepositoriesResult> expectedResult = getValidResponse();

        when(gitHubClient.getBranchesFromRepository(any())).thenReturn(branches);
        when(gitHubClient.getRepositoriesForUsername(any())).thenReturn(repositories);
        List<RepositoriesResult> result = gitHubService.getRepositoriesResult("tomlisow");

        assertEquals(expectedResult, result);
    }

    private List<RepositoriesResult> getValidResponse() {
        List<RepositoriesResult> result = new ArrayList<>();
        RepositoriesResult repositoriesResult = new RepositoriesResult(
                repository.getName(),
                owner.login(),
                branches);
        result.add(repositoriesResult);

        return result;
    }
}