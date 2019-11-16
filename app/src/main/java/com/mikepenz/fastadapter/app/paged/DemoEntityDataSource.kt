package com.mikepenz.fastadapter.app.paged

import android.content.Context
import androidx.paging.PageKeyedDataSource

/**
 * https://www.zoftino.com/pagination-in-android-using-paging-library
 */
//data source for PagedList, it is used for loading data for each page
class DemoEntityDataSource(ctx: Context) : PageKeyedDataSource<Int, DemoEntity>() {
    private val demoEntityDAO: DemoEntityLocalDAO = LocalRepository.getDemoEntityDB(ctx).demoEntityDAO()

    //is called too load initial data
    override fun loadInitial(params: LoadInitialParams<Int>,
                             callback: LoadInitialCallback<Int, DemoEntity>) {

        var cpns = demoEntityDAO.getDemoEntitiesBySize(0, params.requestedLoadSize)

        //this is required to handle first request after db is created or app is installed
        var noOfTryies = 0
        while (cpns.isEmpty()) {
            cpns = demoEntityDAO.getDemoEntitiesBySize(0, params.requestedLoadSize)
            noOfTryies++
            if (noOfTryies == 6) {
                break
            }
            try {
                Thread.sleep(500)
            } catch (e: InterruptedException) {
                // no action
            }
        }

        callback.onResult(cpns, null, cpns.size + 1)
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, DemoEntity>) {
    }

    //is called to load pages of data using key passed in params
    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, DemoEntity>) {
        val cpns = demoEntityDAO.getDemoEntitiesBySize(params.key, params.requestedLoadSize)

        Thread.sleep(500)

        val nextKey = params.key + cpns.size
        callback.onResult(cpns, nextKey)
    }
}
