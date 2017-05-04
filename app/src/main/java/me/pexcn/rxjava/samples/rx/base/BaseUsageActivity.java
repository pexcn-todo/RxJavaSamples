package me.pexcn.rxjava.samples.rx.base;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import me.pexcn.rxjava.samples.BaseActivity;
import me.pexcn.rxjava.samples.R;
import me.pexcn.rxjava.samples.api.Api;
import me.pexcn.rxjava.samples.api.ZhihuNewsService;
import me.pexcn.rxjava.samples.entity.ZhihuNews;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by pexcn on 2017-05-02.
 */
public class BaseUsageActivity extends BaseActivity {
    private ZhihuNewsService mZhihuNewsService;
    private ArrayList<String> mTitles;
    private ArrayList<String> mUrls;
    private ArrayAdapter<String> mAdapter;

    @BindView(R.id.fetch_data)
    Button mFetchDataButton;
    @BindView(R.id.source_data)
    TextView mSourceDataTextView;
    @BindView(R.id.data_list)
    ListView mDataList;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_base_usage;
    }

    @Override
    protected void init() {
        super.init();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Api.BASE_URL_ZHIHU)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        mZhihuNewsService = retrofit.create(ZhihuNewsService.class);

        mTitles = new ArrayList<>();
        mUrls = new ArrayList<>();
    }

    @OnClick(R.id.fetch_data)
    public void onFetchData(View view) {
        mZhihuNewsService.fetchNews()
                .subscribeOn(Schedulers.io()) // 指定 fetchNews() 执行的线程发生在 io 线程
                .observeOn(AndroidSchedulers.mainThread()) // 在主线程处理得到的数据
                .subscribe(new Subscriber<List<ZhihuNews>>() {
                    @Override
                    public void onCompleted() {
                        mAdapter.notifyDataSetChanged();
                        Toast.makeText(BaseUsageActivity.this, "Load Finished.", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(BaseUsageActivity.this, "Load Error.", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(List<ZhihuNews> zhihuNewses) {

                    }
                });
    }

    @Override
    protected boolean isSubActivity() {
        return true;
    }
}
