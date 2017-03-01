package com.mikepenz.fastadapter.app;

import android.app.Application;

import io.realm.Realm;

/**
 * Created by mikepenz on 04.07.16.
 */

public class CustomApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // The Realm file will be located in Context.getFilesDir() with name "default.realm"
        Realm.init(getApplicationContext());
    }
}
