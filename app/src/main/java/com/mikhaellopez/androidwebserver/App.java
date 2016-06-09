package com.mikhaellopez.androidwebserver;

import android.app.Application;
import android.content.Context;

/**
 * Created by way on 16/6/9.
 */
public class App extends Application{
    private static Context mContext;

    public static Context getContext() {
        return mContext;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
    }
}
