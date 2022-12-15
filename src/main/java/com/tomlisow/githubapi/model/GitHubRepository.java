package com.tomlisow.githubapi.model;

public record  GitHubRepository(String name,
                                GitHubRepositoryOwner owner,
                                Boolean fork,
                                String branches_url) {
}