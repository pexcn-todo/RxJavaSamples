package me.pexcn.rxjava.samples.utils;

import java.io.File;
import java.util.concurrent.TimeUnit;

import me.pexcn.android.utils.Utils;
import me.pexcn.rxjava.samples.BuildConfig;
import me.pexcn.rxjava.samples.api.Api;
import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by pexcn on 2017-05-05.
 */
public class RetrofitUtils {
    private volatile static Retrofit INSTANCE;

    public static <T> T createService(Class<T> clazz) {
        if (INSTANCE == null) {
            synchronized (Retrofit.class) {
                if (INSTANCE == null) {
                    INSTANCE = new Retrofit.Builder()
                            .baseUrl(Api.BASE_URL)
                            .client(buildOkHttpClient())
                            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
                }
            }
        }
        return INSTANCE.create(clazz);
    }

    private static OkHttpClient buildOkHttpClient() {
        return new OkHttpClient.Builder()
                .addInterceptor(buildLoggingInterceptor())
                .cache(buildCache())
                .retryOnConnectionFailure(true)
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .build();
    }

    private static Interceptor buildLoggingInterceptor() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.HEADERS : HttpLoggingInterceptor.Level.NONE);
        return interceptor;
    }

    @SuppressWarnings("ConstantConditions")
    private static Cache buildCache() {
        File file = new File(Utils.getContext().getExternalCacheDir().getAbsolutePath() + File.separator + "okhttp_cache");
        return new Cache(file, 1024 * 1024 * 32);
    }
}
