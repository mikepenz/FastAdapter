package com.mikepenz.fastadapter.app.paged

import android.content.Context

import androidx.paging.DataSource

/**
 * https://www.zoftino.com/pagination-in-android-using-paging-library
 */
class DemoEntitiesDataSourceFactory(private val ctx: Context) : DataSource.Factory<Int, DemoEntity>() {
    private var demoEntitiesDataSource: DemoEntityDataSource? = null

    override fun create(): DataSource<Int, DemoEntity> {
        if (demoEntitiesDataSource == null) {
            demoEntitiesDataSource = DemoEntityDataSource(ctx)
        }
        return demoEntitiesDataSource!!
    }
}