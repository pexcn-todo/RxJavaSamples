package me.pexcn.rxjava.samples.api;

import java.util.List;

import me.pexcn.rxjava.samples.entity.Repo;
import retrofit2.http.GET;
import rx.Observable;

/**
 * Created by pexcn on 2017-05-03.
 */
public interface GitHubService {
    @GET("users/pexcn/repos")
    Observable<List<Repo>> fetchRepos();

//    @GET("users/pexcn/repos")
//    Observable<Repo> fetchRepos();
}
