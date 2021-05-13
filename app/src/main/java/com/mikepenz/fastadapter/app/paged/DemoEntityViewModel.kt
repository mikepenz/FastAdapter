package com.mikepenz.fastadapter.app.paged

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


/**
 * https://www.zoftino.com/pagination-in-android-using-paging-library
 */
class DemoEntityViewModel(val application: Application) : ViewModel() {
    //PagedList controls data loading using data source
    private val dao: DemoEntityLocalDAO =
        LocalRepository.getDemoEntityDB(application).demoEntityDAO()
    var demoEntitiesList: LiveData<PagedList<DemoEntity>>

    init {
        val pagedListConfig = PagedList.Config.Builder()
            .setPageSize(20)
            .setPrefetchDistance(20)
            .setEnablePlaceholders(true)
            .build()

        demoEntitiesList = LivePagedListBuilder(dao.getAll(), pagedListConfig).build()
    }

    fun updateEntities() {
        Completable.fromRunnable { dao.updateDemoEntities() }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()
    }

    //factory for creating view model,
    // required because we need to pass Application to view model object
    class DemoEntityViewModelFactory(private val mApplication: Application) :
        ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(viewModel: Class<T>): T {
            return DemoEntityViewModel(mApplication) as T
        }
    }
}