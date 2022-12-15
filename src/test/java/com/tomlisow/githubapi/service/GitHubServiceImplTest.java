package com.tomlisow.githubapi.service;

import com.google.gson.Gson;
import com.tomlisow.githubapi.model.GitHubBranch;
import com.tomlisow.githubapi.model.GitHubCommit;
import com.tomlisow.githubapi.model.RepositoriesResult;
import net.minidev.json.JSONArray;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.match.MockRestRequestMatchers;
import org.springframework.test.web.client.response.MockRestResponseCreators;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

//@ExtendWith(MockitoExtension.class)
@RestClientTest(GitHubServiceImpl.class)
class GitHubServiceImplTest {

    @Autowired
    private GitHubServiceImpl gitHubService;

    @Autowired
    MockRestServiceServer mockRestServiceServer;

    @Test
    public void getRepositoriesResultWhenValidUsername() {
        List<RepositoriesResult> expectedResult = getValidResponse();
        JSONArray objects = new JSONArray();
        objects.addAll(expectedResult);

        RestTemplate restTemplate = new RestTemplate();

        MockRestServiceServer
                .bindTo(restTemplate)
                .build()
                .expect(requestTo(anyString()))
                .andRespond(withSuccess(objects.toJSONString(), MediaType.APPLICATION_JSON));

        List<RepositoriesResult> result = gitHubService.getRepositoriesResult("tomlisow");

        assertEquals(expectedResult, result);

//        List<RepositoriesResult> expectedResult = getValidResponse();
//        JSONArray objects = new JSONArray();
//        objects.addAll(expectedResult);
//
//        when(restTemplate.getForObject(anyString(), any())).thenReturn(objects);
//
//        List<RepositoriesResult> result = gitHubService.getRepositoriesResult("tomlisow");
//
//        assertEquals(expectedResult, result);
    }

    private List<RepositoriesResult> getValidResponse() {
        List<RepositoriesResult> result = new ArrayList<>();

        GitHubBranch branch1 = new GitHubBranch("main", new GitHubCommit("sha1-hash"));
        GitHubBranch[] branches = {branch1};
        RepositoriesResult repositoriesResult = new RepositoriesResult(
                "repo-name",
                "userLogin",
                branches);

        result.add(repositoriesResult);
        return result;
    }
}