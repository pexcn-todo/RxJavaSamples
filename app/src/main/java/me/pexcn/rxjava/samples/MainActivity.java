package me.pexcn.rxjava.samples;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import me.pexcn.rxjava.samples.rx1.base.BaseUsageActivity;
import me.pexcn.rxjava.samples.rx1.base.TestActivity;

@SuppressWarnings("FieldCanBeLocal")
public class MainActivity extends BaseActivity implements AdapterView.OnItemClickListener {
    private String[] mItems;
    private ListView mListView;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void init() {
        super.init();
        mItems = getResources().getStringArray(R.array.sub_activities);
        mListView = (ListView) findViewById(R.id.list_view);
        mListView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mItems));
        mListView.setOnItemClickListener(this);
    }

    private void setTargetActivity(int position, Class<?> clazz) {
        Intent intent = new Intent();
        intent.setClass(this, clazz);
        intent.putExtra(KEY_ACTIVITY_TITLE, mItems[position]);
        startActivity(intent);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
                setTargetActivity(position, BaseUsageActivity.class);
                break;
            case 1:
                setTargetActivity(position, TestActivity.class);
                break;
        }
    }
}
