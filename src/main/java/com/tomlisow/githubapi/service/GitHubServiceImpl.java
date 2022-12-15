package com.tomlisow.githubapi.service;

import com.google.gson.Gson;
import com.tomlisow.githubapi.model.GitHubBranch;
import com.tomlisow.githubapi.model.GitHubRepository;
import com.tomlisow.githubapi.model.RepositoriesResult;
import net.minidev.json.JSONArray;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class GitHubServiceImpl implements GitHubService {
    private static final String API_CORE = "https://api.github.com/";
    private final RestTemplate template;

    public GitHubServiceImpl(RestTemplate template) {
        this.template = template;
    }

    public List<RepositoriesResult> getRepositoriesResult(String username) {
        GitHubRepository[] gitHubRepositories = getRepositories(username);
        return buildRepositoriesResults(gitHubRepositories);
    }

    private GitHubRepository[] getRepositories(String username) throws HttpClientErrorException {
        String endpoint = getEndpointForReposForUser(username);
        JSONArray object = getJsonFromEndpoint(endpoint);
        return new Gson().fromJson(object.toString(), GitHubRepository[].class);
    }

    private List<RepositoriesResult> buildRepositoriesResults(GitHubRepository[] gitHubRepositories) {
        List<RepositoriesResult> result = new ArrayList<>();

        for (GitHubRepository repo : gitHubRepositories) {
            if (repo.getFork()) {
                continue;
            }

            String branchesEndpoint = repo.getBranchesUrl().replace("{/branch}", "");
            GitHubBranch[] branches = getBranches(branchesEndpoint);

            RepositoriesResult repositoriesResult = new RepositoriesResult(
                    repo.getName(),
                    repo.getOwner().login(),
                    branches);
            result.add(repositoriesResult);
        }
        return result;
    }

    private JSONArray getJsonFromEndpoint(String endpoint) {
        JSONArray object = template.getForObject(endpoint, JSONArray.class);
        assert object != null : "Object returned from RestTemplate under \"" + endpoint + "\" is null";
        return object;
    }

    //FIXME - should throw dedicated exception type
    //such error is possible if the response from GitHub contains invalid endpoint to the branches resource
    private GitHubBranch[] getBranches(String branchesEndpoint) throws HttpClientErrorException {
        JSONArray object = getJsonFromEndpoint(branchesEndpoint);
        return new Gson().fromJson(object.toString(), GitHubBranch[].class);
    }

    private static String getEndpointForReposForUser(String username) {
        return API_CORE + "users/" + username + "/repos";
    }
}
