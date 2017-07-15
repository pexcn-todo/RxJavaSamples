package me.pexcn.rxjava.samples.rx1.base;

import android.support.annotation.IntDef;
import android.support.annotation.StringDef;
import android.view.View;

import me.pexcn.android.utils.io.LogUtils;
import me.pexcn.rxjava.samples.BaseActivity;
import me.pexcn.rxjava.samples.R;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by pexcn on 2017-05-16.
 */
public class TestActivity extends BaseActivity {
    private static final String TYPE_1 = "type_1";
    private static final String TYPE_2 = "type_2";
    private static final String TYPE_3 = "type_3";

    @StringDef({TYPE_1, TYPE_2, TYPE_3})
    private @interface Type {
    }

    private static final int INT_1 = 1;
    private static final int INT_2 = INT_1 << 1;
    private static final int INT_3 = INT_2 << 1;

    @IntDef(flag = true, value = {INT_1, INT_2, INT_3})
    private @interface IntType {
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_test;
    }

    @Override
    protected void init() {
        super.init();

        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Observable.just("Hello", "world", "!")
                        .flatMap(new Func1<String, Observable<Character>>() {
                            @Override
                            public Observable<Character> call(String s) {
                                Character[] chars = new Character[s.length()];
                                for (int i = 0; i < chars.length; i++) {
                                    chars[i] = s.charAt(i);
                                }
                                return Observable.from(chars);
                            }
                        })
                        .subscribe(new Action1<Character>() {
                            @Override
                            public void call(Character character) {
                                LogUtils.d(character.toString());
                            }
                        });
            }
        });

        setType(TYPE_2);
        setIntType(INT_1 | INT_3);
        setIntType(INT_2);
        setIntType(INT_1 & INT_2);

//        FutureTask<String> task = new FutureTask<>(new Callable<String>() {
//            @Override
//            public String call() throws Exception {
//                return null;
//            }
//        });
    }

    @Override
    protected boolean isSubActivity() {
        return true;
    }

    private void setType(@Type String type) {
        LogUtils.d(type);
    }

    private void setIntType(@IntType int type) {
        LogUtils.d(String.valueOf(type));
    }
}
