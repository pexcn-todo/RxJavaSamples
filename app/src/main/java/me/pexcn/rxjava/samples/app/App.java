package me.pexcn.rxjava.samples.app;

import android.app.Application;

import me.pexcn.android.utils.Utils;

/**
 * Created by pexcn on 2017-05-02.
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Utils.init(this);
    }
}
