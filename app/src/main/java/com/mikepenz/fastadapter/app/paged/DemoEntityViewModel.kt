package com.mikepenz.fastadapter.app.paged

import android.app.Application

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList

/**
 * https://www.zoftino.com/pagination-in-android-using-paging-library
 */
class DemoEntityViewModel(application: Application) : ViewModel() {
    //PagedList controls data loading using data source
    var demoEntitiesList: LiveData<PagedList<DemoEntity>>

    init {
        //instantiate DemoEntitiesDataSourceFactory
        val factory = DemoEntitiesDataSourceFactory(application)

        //create PagedList Config
        val config = PagedList.Config.Builder()
                .setEnablePlaceholders(true)
                .setInitialLoadSizeHint(10)
                .setPageSize(5)
                .build()

        //create LiveData object using LivePagedListBuilder which takes
        //data source factory and page config as params
        demoEntitiesList = LivePagedListBuilder(factory, config).build()
    }

    //factory for creating view model,
    // required because we need to pass Application to view model object
    class DemoEntityViewModelFactory(private val mApplication: Application) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(viewModel: Class<T>): T {
            return DemoEntityViewModel(mApplication) as T
        }
    }
}