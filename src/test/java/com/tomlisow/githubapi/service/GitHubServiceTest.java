package com.tomlisow.githubapi.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tomlisow.githubapi.model.GitHubBranch;
import com.tomlisow.githubapi.model.GitHubRepository;
import com.tomlisow.githubapi.model.RepositoriesResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GitHubServiceTest {

    @Mock
    GitHubClient gitHubClient;

    @InjectMocks
    private GitHubService gitHubService;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Path RESOURCES_ROOT = Path.of("src","test","resources");

    @Test
    public void getValidRepositoriesResultWhenGitHubClientReturnsValidResponse() throws IOException {
        Path validResponsePath = Path.of(RESOURCES_ROOT.toString(), "validResponse.json");
        Path validRepositoriesPath = Path.of(RESOURCES_ROOT.toString(), "validRepositories.json");
        Path validBranchesPath = Path.of(RESOURCES_ROOT.toString(), "validBranches.json");
        List<GitHubBranch> gitHubBranches = objectMapper.readValue(Files.readString(validBranchesPath), new TypeReference<>() {});
        List<GitHubRepository> gitHubRepositories = objectMapper.readValue(Files.readString(validRepositoriesPath), new TypeReference<>() {});
        List<RepositoriesResult> expectedResult = objectMapper.readValue(Files.readString(validResponsePath), new TypeReference<>() {});

        when(gitHubClient.getBranchesFromRepository(any())).thenReturn(gitHubBranches);
        when(gitHubClient.getRepositoriesForUsername(any())).thenReturn(gitHubRepositories);
        List<RepositoriesResult> result = gitHubService.getRepositoriesResult(anyString());

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(expectedResult, result);
    }

    @Test
    public void getEmptyResponseWhenRepositoriesResultIsEmpty() throws IOException {
        List<RepositoriesResult> expectedResult = List.of();

        when(gitHubClient.getRepositoriesForUsername(any())).thenReturn(List.of());
        List<RepositoriesResult> result = gitHubService.getRepositoriesResult(anyString());

        assertNotNull(result);
        assertEquals(0, result.size());
        assertEquals(expectedResult, result);
    }

    @Test
    public void getValidResponseWithEmptyBranchListWhenBranchesResultIsEmpty() throws IOException {
        Path validResponsePath = Path.of(RESOURCES_ROOT.toString(), "validResponseWithoutBranches.json");
        Path validRepositoriesPath = Path.of(RESOURCES_ROOT.toString(), "validRepositories.json");
        List<GitHubRepository> gitHubRepositories = objectMapper.readValue(Files.readString(validRepositoriesPath), new TypeReference<>() {});
        List<RepositoriesResult> expectedResult = objectMapper.readValue(Files.readString(validResponsePath), new TypeReference<>() {});

        when(gitHubClient.getBranchesFromRepository(any())).thenReturn(List.of());
        when(gitHubClient.getRepositoriesForUsername(any())).thenReturn(gitHubRepositories);
        List<RepositoriesResult> result = gitHubService.getRepositoriesResult(anyString());

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(expectedResult, result);
        assertEquals(0, result.get(0).branches().size());
    }

    @Test
    public void getNpeWhenRepositoriesResultIsNull() {
        when(gitHubClient.getRepositoriesForUsername(any())).thenReturn(null);
        assertThrows(NullPointerException.class, () -> {
            gitHubService.getRepositoriesResult(anyString());
        });
    }
}