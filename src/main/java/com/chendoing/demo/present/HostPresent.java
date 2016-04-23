package com.chendoing.demo.present;

import com.chendoing.demo.api.GithubService;
import com.chendoing.demo.model.Repos;
import com.chendoing.demo.ui.Hosts;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.List;

public class HostPresent {

    private Hosts hosts;
    private GithubService githubService;

    private boolean isRequesting;

    public HostPresent(Hosts hosts, GithubService githubService) {
        this.hosts = hosts;
        this.githubService = githubService;
    }

    public void onCreate(){
        askForNewData();
    }

    public void askForNewData(){
        isRequesting = true;
        hosts.showLoadingMessage();
        githubService.getRepositories().enqueue(new Callback<List<Repos>>() {
            @Override
            public void onResponse(Call<List<Repos>> call, Response<List<Repos>> response) {
                hosts.hideLoadingMessage();
                hosts.updateHosts(response.body());
            }

            @Override
            public void onFailure(Call<List<Repos>> call, Throwable throwable) {
                hosts.hideLoadingMessage();
                hosts.showErrorMessage();
            }
        });
        isRequesting = false;
    }
}
