package com.chendoing.demo.api;

import com.chendoing.demo.model.Repos;
import retrofit2.Call;
import retrofit2.http.GET;

import java.util.List;

public interface GithubService {

    @GET("/users/chenDoInG/repos")
    Call<List<Repos>> getRepositories();
}
