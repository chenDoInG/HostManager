package com.chendoing.demo.api;

import com.chendoing.demo.model.Repos;
import retrofit2.http.GET;
import rx.Observable;

import java.util.List;

public interface GithubService {

    @GET("/users/chenDoInG/repos")
    Observable<List<Repos>> getRepositories();
}
