package me.pexcn.rxjava.samples.rx.base;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import me.pexcn.android.utils.io.LogUtils;
import me.pexcn.rxjava.samples.BaseActivity;
import me.pexcn.rxjava.samples.R;
import me.pexcn.rxjava.samples.api.Api;
import me.pexcn.rxjava.samples.api.GitHubService;
import me.pexcn.rxjava.samples.entity.Repo;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by pexcn on 2017-05-02.
 */
@SuppressWarnings("DanglingJavadoc")
public class BaseUsageActivity extends BaseActivity {
    private ArrayList<String> mTitles;
    private ArrayAdapter<String> mTitleAdapter;
    private GitHubService mGitHubService;
    private Subscriber<String> mTitleSubscriber;

    @BindView(R.id.json_raw_data)
    TextView mSourceDataTextView;
    @BindView(R.id.title_list)
    ListView mTitleList;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_base_usage;
    }

    @Override
    protected void init() {
        super.init();

        mTitles = new ArrayList<>();
        mTitleAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mTitles);
        mTitleList.setAdapter(mTitleAdapter);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Api.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        mGitHubService = retrofit.create(GitHubService.class);
    }

    @OnClick(R.id.fetch_data)
    public void onFetchData(View view) {
        /**
         * 获得被观察者 Observable
         */
        mGitHubService.fetchRepos()

                /**
                 * 指定被观察者 Observable 的线程，即产生事件的线程，这里指 fetchStories() 发生在 io 线程
                 */
                .subscribeOn(Schedulers.io())

                /**
                 * 平铺对象，把 List<Story> 转换成 一个一个的 Story
                 */
                .flatMap(new Func1<List<Repo>, Observable<Repo>>() {
                    @Override
                    public Observable<Repo> call(List<Repo> repos) {
                        LogUtils.d("Observable -> flatMap() -> " + Thread.currentThread());
                        return Observable.from(repos);
                    }
                })

                /**
                 * 切换线程到 computation 线程
                 */
                .subscribeOn(Schedulers.computation())

                /**
                 * 转换对象
                 */
                .map(new Func1<Repo, String>() {
                    @Override
                    public String call(Repo repo) {
                        LogUtils.d("Observable -> map() -> " + Thread.currentThread());
                        return repo.getName();
                    }
                })

                /**
                 * 指定观察者 Observer 的线程，即处理事件的线程，这里指在 UI 线程处理事件
                 */
                .observeOn(AndroidSchedulers.mainThread())

                /**
                 * 生成订阅关系
                 */
                .subscribe(
                        /**
                         * 创建一个订阅者 Subscriber，它是 Observer 的实现类，和 Observer 相比主要有两点不同
                         * 1. onStart(): 在事件发生前被调用
                         * 2. unsubscribe(): 用于取消订阅
                         */
                        mTitleSubscriber = new Subscriber<String>() {
                            @Override
                            public void onStart() {
                                LogUtils.d("Subscriber -> onStart() -> " + Thread.currentThread());
                                if (!mTitles.isEmpty()) {
                                    mTitles.clear();
                                }
                            }

                            @Override
                            public void onCompleted() {
                                LogUtils.d("Subscriber -> onCompleted() -> " + Thread.currentThread());
                                mTitleAdapter.notifyDataSetChanged();
                                Toast.makeText(BaseUsageActivity.this, "加载完成：" + mTitles.size() + "条", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onError(Throwable e) {
                                LogUtils.d("Subscriber -> onError() -> " + Thread.currentThread());
                                Toast.makeText(BaseUsageActivity.this, "加载数据时出现错误", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onNext(String title) {
                                LogUtils.d("Subscriber -> onNext() -> " + Thread.currentThread());
                                mTitles.add(title);
                            }
                        }

//                        /**
//                         * 创建一个观察者 Observer, 它是事件消费者的最小构建模块
//                         */
//                        new Observer<String>() {
//                            @Override
//                            public void onCompleted() {
//
//                            }
//
//                            @Override
//                            public void onError(Throwable e) {
//
//                            }
//
//                            @Override
//                            public void onNext(String title) {
//
//                            }
//                        }

//                        /**
//                         * 观察者 Observer 的不完整回调 ActionX
//                         */
//                        new Action1<String>() {
//                            @Override
//                            public void call(String title) {
//
//                            }
//                        }
                );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        /**
         * 解除订阅关系
         */
        if (!mTitleSubscriber.isUnsubscribed()) {
            mTitleSubscriber.unsubscribe();
        }
    }

    @Override
    protected boolean isSubActivity() {
        return true;
    }
}
