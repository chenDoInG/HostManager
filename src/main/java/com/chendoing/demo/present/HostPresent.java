package com.chendoing.demo.present;

import com.chendoing.demo.api.GithubService;
import com.chendoing.demo.ui.Hosts;
import rx.schedulers.Schedulers;

public class HostPresent {

    private Hosts hosts;
    private GithubService githubService;


    public HostPresent(Hosts hosts, GithubService githubService) {
        this.hosts = hosts;
        this.githubService = githubService;
    }

    public void onCreate() {
        askForNewData();
    }

    public void askForNewData() {
        hosts.showLoadingMessage();
        githubService.getRepositories()
                .subscribeOn(Schedulers.io())
                .subscribe(reposes -> {
                            hosts.hideLoadingMessage();
                            hosts.updateHosts(reposes);
                        },
                        throwable -> {
                            hosts.hideLoadingMessage();
                            hosts.showErrorMessage();
                        });
    }
}
