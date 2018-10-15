package com.mikepenz.fastadapter.app;

import com.mikepenz.fastadapter.expandable.ExpandableExtensionFactory;
import com.mikepenz.fastadapter.extensions.ExtensionsFactories;
import com.mikepenz.fastadapter.select.SelectExtensionFactory;

import androidx.multidex.MultiDexApplication;
import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by mikepenz on 04.07.16.
 */

public class CustomApplication extends MultiDexApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        // The Realm file will be located in Context.getFilesDir() with name "default.realm"
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder().build();
        Realm.setDefaultConfiguration(config);

        ExtensionsFactories.INSTANCE.register(new SelectExtensionFactory());
        ExtensionsFactories.INSTANCE.register(new ExpandableExtensionFactory());
    }
}
