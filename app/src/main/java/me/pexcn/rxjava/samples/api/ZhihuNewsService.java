package me.pexcn.rxjava.samples.api;

import java.util.List;

import me.pexcn.rxjava.samples.entity.ZhihuNews;
import retrofit2.http.GET;
import rx.Observable;

/**
 * Created by pexcn on 2017-05-03.
 */
public interface ZhihuNewsService {
    @GET("api/4/news/latest")
    Observable<List<ZhihuNews>> fetchNews();
}
