package com.tomlisow.githubapi.service;

import com.tomlisow.githubapi.model.GitHubBranch;
import com.tomlisow.githubapi.model.GitHubRepository;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class GitHubClient {
    private static final String API_CORE = "https://api.github.com/";
    private final RestTemplate template;

    public GitHubClient(RestTemplateBuilder restTemplateBuilder) {
        this.template = restTemplateBuilder.build();
    }

    public List<GitHubRepository> getRepositoriesForUsername(final String username) throws HttpClientErrorException {
        String endpoint = getEndpointForReposForUser(username);
        return template.exchange(endpoint,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<GitHubRepository>>() {
                }).getBody();
    }

    //FIXME - should throw dedicated exception type
    //such error is possible if the response from GitHub contains invalid endpoint to the branches resource
    public List<GitHubBranch> getBranchesFromRepository(final GitHubRepository gitHubRepository) throws HttpClientErrorException {
        if (isInvalidRepository(gitHubRepository)) {
            return List.of();
        }
        String branchesEndpoint = gitHubRepository.branches_url().replace("{/branch}", "");
        return template.exchange(branchesEndpoint,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<GitHubBranch>>() {
                }).getBody();
    }

    private static boolean isInvalidRepository(GitHubRepository gitHubRepository) {
        return gitHubRepository == null
                || gitHubRepository.branches_url() == null
                || gitHubRepository.branches_url().isBlank();
    }

    private static String getEndpointForReposForUser(String username) {
        return API_CORE + "users/" + username + "/repos";
    }
}
