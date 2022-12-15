package com.tomlisow.githubapi.model;

import com.google.gson.annotations.SerializedName;

public class GitHubRepository {
    private String name;
    private GitHubRepositoryOwner owner;
    @SerializedName(value = "fork")
    private Boolean isFork;
    @SerializedName(value = "branches_url")
    private String branchesUrl;

    public GitHubRepository(String name, GitHubRepositoryOwner owner, Boolean isFork, String branchesUrl) {
        this.name = name;
        this.owner = owner;
        this.isFork = isFork;
        this.branchesUrl = branchesUrl;
    }

    public String getName() {
        return name;
    }

    public GitHubRepositoryOwner getOwner() {
        return owner;
    }

    public Boolean getFork() {
        return isFork;
    }

    public String getBranchesUrl() {
        return branchesUrl;
    }
}
