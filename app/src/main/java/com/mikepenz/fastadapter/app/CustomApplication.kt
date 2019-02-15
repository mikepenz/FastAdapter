package com.mikepenz.fastadapter.app

import androidx.multidex.MultiDexApplication
import io.realm.Realm
import io.realm.RealmConfiguration

/**
 * Created by mikepenz on 04.07.16.
 */

class CustomApplication : MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()
        // The Realm file will be located in Context.getFilesDir() with name "default.realm"
        Realm.init(this)
        val config = RealmConfiguration.Builder().build()
        Realm.setDefaultConfiguration(config)
    }
}
