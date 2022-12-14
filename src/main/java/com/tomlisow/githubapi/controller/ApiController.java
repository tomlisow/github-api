package com.tomlisow.githubapi.controller;

import com.tomlisow.githubapi.model.RepositoriesResult;
import com.tomlisow.githubapi.service.GitHubService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ApiController {

    final GitHubService gitHubService;

    public ApiController(GitHubService gitHubService) {
        this.gitHubService = gitHubService;
    }

    @GetMapping(value = "/repos/{username}", headers = "Accept=application/json")
    public ResponseEntity<?> getAllReposForUser(@PathVariable String username) {
        List<RepositoriesResult> result = gitHubService.getRepositoriesResult(username);
        return ResponseEntity.ok(result);
    }
}
