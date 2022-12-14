package com.tomlisow.githubapi.service;

import com.tomlisow.githubapi.model.GitHubRepository;
import com.tomlisow.githubapi.model.RepositoriesResult;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

public interface GitHubService {
    List<RepositoriesResult> getRepositoriesResult(String username);
}
