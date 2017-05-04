package me.pexcn.rxjava.samples;

import android.support.annotation.CallSuper;
import android.support.v7.app.ActionBar;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by pexcn on 2017-05-03.
 */
public abstract class BaseActivity extends me.pexcn.android.base.ui.BaseActivity {
    public static final String KEY_ACTIVITY_TITLE = "activity_title";

    private Unbinder mUnbinder;

    @CallSuper
    @Override
    protected void init() {
        final ActionBar actionBar = getSupportActionBar();
        final String title = getIntent().getStringExtra(KEY_ACTIVITY_TITLE);
        if (actionBar != null && title != null && isSubActivity()) {
            actionBar.setTitle(title);
        }
        mUnbinder = ButterKnife.bind(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mUnbinder != null) {
            mUnbinder.unbind();
            mUnbinder = null;
        }
    }
}
