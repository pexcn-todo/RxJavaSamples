package me.pexcn.rxjava.samples;

import android.content.Intent;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import butterknife.BindView;
import butterknife.OnItemClick;
import me.pexcn.rxjava.samples.rx1.base.BaseUsageActivity;

public class MainActivity extends BaseActivity {
    private String[] mItems;

    @BindView(R.id.list_view)
    ListView mListView;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void init() {
        super.init();
        mItems = getResources().getStringArray(R.array.sub_activities);
        mListView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mItems));
    }

    @OnItemClick(R.id.list_view)
    void onItemClick(int position) {
        switch (position) {
            case 0:
                setTargetActivity(position, BaseUsageActivity.class);
                break;
            case 1:
                setTargetActivity(position, BaseUsageActivity.class);
                break;
        }
    }

    private void setTargetActivity(int position, Class<?> clazz) {
        Intent intent = new Intent();
        intent.setClass(this, clazz);
        intent.putExtra(KEY_ACTIVITY_TITLE, mItems[position]);
        startActivity(intent);
    }
}
