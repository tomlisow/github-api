package com.tomlisow.githubapi.controller;

import com.tomlisow.githubapi.service.GitHubService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ApiController.class)
class ApiControllerTest {
    public static final String VALID_USER_ENDPOINT = "/repos/tomlisow";
    public static final String INVALID_ENDPOINT = "/someInvalidEndpoint";
    public static final String INVALID_USER_ENDPOINT = "/repos/tomlisowaaa";
    public static final String STATUS_404_MESSAGE = "{\"status\":404,\"message\":\"Non-existent GitHub username\"}";
    public static final String STATUS_406_MESSAGE = "{\"status\":406,\"message\":\"Wrong HTTP Media Type. Acceptable representations: [application/json].\"}";
    @Autowired
    MockMvc mockMvc;

    @MockBean
    GitHubService gitHubService;

    @Test
    void getEmptyBodyWhenValidUserAndMediaType() throws Exception {
        when(gitHubService.getRepositoriesResult(anyString())).thenReturn(List.of());

        mockMvc.perform(MockMvcRequestBuilders
                        .get(VALID_USER_ENDPOINT)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(content().string("[]"))
                .andExpect(status().isOk());
    }

    @Test
    void getAllReposWhenInvalidUserAndValidMediaType() throws Exception {
        when(gitHubService.getRepositoriesResult(anyString())).thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        mockMvc.perform(MockMvcRequestBuilders
                        .get(INVALID_USER_ENDPOINT)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(content().string(STATUS_404_MESSAGE))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllReposWhenInvalidEndpoint() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get(INVALID_ENDPOINT)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllReposWhenValidUserAndInvalidMediaType() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get(VALID_USER_ENDPOINT)
                        .accept(MediaType.APPLICATION_XML)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(content().string(STATUS_406_MESSAGE))
                .andExpect(status().isNotAcceptable());
    }
}