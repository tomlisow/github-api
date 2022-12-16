package com.tomlisow.githubapi.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tomlisow.githubapi.model.ErrorResponse;
import com.tomlisow.githubapi.model.GitHubBranch;
import com.tomlisow.githubapi.model.GitHubRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.match.MockRestRequestMatchers;
import org.springframework.test.web.client.response.MockRestResponseCreators;
import org.springframework.web.client.HttpClientErrorException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@RestClientTest(GitHubClient.class)
public class GitHubClientTest {
    @Autowired
    MockRestServiceServer mockRestServiceServer;

    @Autowired
    GitHubClient gitHubClient;

    @Autowired
    ObjectMapper objectMapper;

    private final Path RESOURCES_ROOT = Path.of("src", "test", "resources");

    @Test
    public void getValidRepositoriesResultWhenApiReturnsValidResponse() throws IOException {
        Path validRepositoriesPath = Path.of(RESOURCES_ROOT.toString(), "validRepositories.json");
        String repositoriesJson = Files.readString(validRepositoriesPath);
        List<GitHubRepository> expectedResult = objectMapper.readValue(repositoriesJson, new TypeReference<>() {
        });

        mockRestServiceServer
                .expect(MockRestRequestMatchers.requestTo("https://api.github.com/users/ownerLogin/repos"))
                .andRespond(MockRestResponseCreators.withSuccess(repositoriesJson, MediaType.APPLICATION_JSON));

        List<GitHubRepository> result = gitHubClient.getRepositoriesForUsername("ownerLogin");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(expectedResult, result);
    }

    @Test
    public void getValidBranchesResultWhenApiReturnsValidResponse() throws IOException {
        Path validGitHubRepositoriesPath = Path.of(RESOURCES_ROOT.toString(), "validGitHubRepositories.json");
        Path validBranchesPath = Path.of(RESOURCES_ROOT.toString(), "validBranches.json");
        String branchesJson = Files.readString(validBranchesPath);
        List<GitHubBranch> expectedResult = objectMapper.readValue(branchesJson, new TypeReference<>() {});
        List<GitHubRepository> repositories = objectMapper.readValue(Files.readString(validGitHubRepositoriesPath), new TypeReference<>() {});

        mockRestServiceServer
                .expect(MockRestRequestMatchers.requestTo("https://api.github.com/repos/tomlisow/git-test/branches"))
                .andRespond(MockRestResponseCreators.withSuccess(branchesJson, MediaType.APPLICATION_JSON));

        List<GitHubBranch> result = gitHubClient.getBranchesFromRepository(repositories.get(0));

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(expectedResult, result);
    }

    @Test
    public void getHttpClientErrorExceptionWhenInvalidUsername() throws IOException, HttpClientErrorException {
        Path path = Path.of(RESOURCES_ROOT.toString(), "invalidUsernameResponse.json");
        String responseJson = Files.readString(path);
        ErrorResponse expectedResult = objectMapper.readValue(responseJson, new TypeReference<>() {
        });

        mockRestServiceServer
                .expect(MockRestRequestMatchers.requestTo("https://api.github.com/users/tomlisowaaa/repos"))
                .andRespond(MockRestResponseCreators
                        .withStatus(HttpStatus.NOT_FOUND)
                        .body(expectedResult.toString()));

        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () -> {
            gitHubClient.getRepositoriesForUsername("tomlisowaaa");
        });

        assertNotNull(exception);
        assertEquals(expectedResult.status(), exception.getStatusCode().value());
        assertTrue(exception.getMessage().contains(expectedResult.toString()));
    }

    @Test
    public void getHttpClientErrorExceptionWhenUsernameIsNull() throws IOException, HttpClientErrorException {
        Path path = Path.of(RESOURCES_ROOT.toString(), "invalidUsernameResponse.json");
        String responseJson = Files.readString(path);
        ErrorResponse expectedResult = objectMapper.readValue(responseJson, new TypeReference<>() {
        });

        mockRestServiceServer
                .expect(MockRestRequestMatchers.requestTo("https://api.github.com/users/null/repos"))
                .andRespond(MockRestResponseCreators
                        .withStatus(HttpStatus.NOT_FOUND)
                        .body(expectedResult.toString()));

        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () -> {
            gitHubClient.getRepositoriesForUsername(null);
        });

        assertNotNull(exception);
        assertEquals(expectedResult.status(), exception.getStatusCode().value());
        assertTrue(exception.getMessage().contains(expectedResult.toString()));
    }

    @Test
    public void getHttpClientErrorExceptionWhenInvalidBranchesUrl() throws IOException, HttpClientErrorException {
        Path path = Path.of(RESOURCES_ROOT.toString(), "invalidUsernameResponse.json");
        String responseJson = Files.readString(path);
        ErrorResponse expectedResult = objectMapper.readValue(responseJson, new TypeReference<>() {});
        Path validGitHubRepositoriesPath = Path.of(RESOURCES_ROOT.toString(), "validGitHubRepositoriesWithInvalidBranchesUrl.json");
        List<GitHubRepository> repositories = objectMapper.readValue(Files.readString(validGitHubRepositoriesPath), new TypeReference<>() {});

        mockRestServiceServer
                .expect(MockRestRequestMatchers.requestTo("https://api.github.com/repos/tomlisow/git-test/wrongBranchesUrl"))
                .andRespond(MockRestResponseCreators
                        .withStatus(HttpStatus.NOT_FOUND)
                        .body(expectedResult.toString()));

        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () -> {
            gitHubClient.getBranchesFromRepository(repositories.get(0));
        });

        assertNotNull(exception);
        assertEquals(expectedResult.status(), exception.getStatusCode().value());
    }

    @Test
    public void getHttpClientErrorExceptionWhenGitHubRepositoryIsNull() {
        List<GitHubBranch> expectedResult = List.of();

        List<GitHubBranch> result = gitHubClient.getBranchesFromRepository(null);

        assertNotNull(result);
        assertEquals(expectedResult, result);
    }
}
