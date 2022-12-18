package com.tomlisow.githubapi.controller;

import com.tomlisow.githubapi.model.RepositoriesResult;
import com.tomlisow.githubapi.service.GitHubService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
public class ApiController {

    final GitHubService gitHubService;

    @GetMapping(value = "/repos/{username}", headers = "Accept=application/json")
    public ResponseEntity<?> getAllReposForUser(@PathVariable String username) {
        log.info("/repos/{} request was made.", username);
        List<RepositoriesResult> result = gitHubService.getRepositoriesResult(username);
        return ResponseEntity.ok(result);
    }
}
