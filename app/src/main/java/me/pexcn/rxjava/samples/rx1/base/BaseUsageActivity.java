package me.pexcn.rxjava.samples.rx1.base;

import android.os.Looper;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import me.pexcn.android.utils.io.LogUtils;
import me.pexcn.rxjava.samples.BaseActivity;
import me.pexcn.rxjava.samples.R;
import me.pexcn.rxjava.samples.api.GitHubService;
import me.pexcn.rxjava.samples.entity.Repo;
import me.pexcn.rxjava.samples.utils.RetrofitUtils;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by pexcn on 2017-05-02.
 */
@SuppressWarnings("FieldCanBeLocal")
public class BaseUsageActivity extends BaseActivity {
    private Button mButton;
    private ListView mListView;

    private ArrayAdapter<String> mAdapter;
    private ArrayList<String> mItems;

    private GitHubService mService;
    private Subscriber<String> mSubscriber;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_base_usage;
    }

    @Override
    protected void init() {
        super.init();

        mButton = (Button) findViewById(R.id.fetch_data);
        mListView = (ListView) findViewById(R.id.list_view);
        mItems = new ArrayList<>();
        mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mItems);
        mListView.setAdapter(mAdapter);

        mService = RetrofitUtils.createService(GitHubService.class);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.isEnabled()) {
                    v.setEnabled(false);
                }

                mService.fetchRepos()
                        .subscribeOn(Schedulers.io())
                        .observeOn(Schedulers.io())
                        .flatMap(new Func1<List<Repo>, Observable<Repo>>() {
                            @Override
                            public Observable<Repo> call(List<Repo> repos) {
                                LogUtils.d("flatMap() -> " + Thread.currentThread().getName());
                                return Observable.from(repos);
                            }
                        })
                        .map(new Func1<Repo, String>() {
                            @Override
                            public String call(Repo repo) {
                                LogUtils.d("map() -> " + Thread.currentThread().getName());
                                return repo.getName();
                            }
                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                mSubscriber = new Subscriber<String>() {
                                    @Override
                                    public void onStart() {
                                        LogUtils.d("onStart() -> " + Thread.currentThread().getName());
                                        if (!mItems.isEmpty()) {
                                            mItems.clear();

                                            if (Looper.myLooper() != Looper.getMainLooper()) {
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        mAdapter.notifyDataSetChanged();
                                                    }
                                                });
                                            } else {
                                                mAdapter.notifyDataSetChanged();
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCompleted() {
                                        LogUtils.d("onCompleted() -> " + Thread.currentThread().getName());
                                        mAdapter.notifyDataSetChanged();
                                        Toast.makeText(BaseUsageActivity.this, "加载完成：" + mItems.size() + "条", Toast.LENGTH_SHORT).show();
                                        if (!mButton.isEnabled()) {
                                            mButton.setEnabled(true);
                                        }
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        LogUtils.d("onError() -> " + Thread.currentThread());
                                        Toast.makeText(BaseUsageActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
                                        if (!mButton.isEnabled()) {
                                            mButton.setEnabled(true);
                                        }
                                    }

                                    @Override
                                    public void onNext(String title) {
                                        LogUtils.d("onNext() -> " + Thread.currentThread().getName());
                                        mItems.add(title);
                                    }
                                }
                        );
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mSubscriber.isUnsubscribed()) {
            LogUtils.d("unsubscribe()");
            mSubscriber.unsubscribe();
        }
    }

    @Override
    protected boolean isSubActivity() {
        return true;
    }
}
