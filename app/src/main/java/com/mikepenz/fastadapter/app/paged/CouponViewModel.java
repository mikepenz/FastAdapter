package com.mikepenz.fastadapter.app.paged;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

public class CouponViewModel extends ViewModel {
    //PagedList controls data loading using data source
    public LiveData<PagedList<Coupon>> couponList;

    public CouponViewModel(Application application) {

        //instantiate CouponsDataSourceFactory
        CouponsDataSourceFactory factory = new CouponsDataSourceFactory(application);

        //create PagedList Config
        PagedList.Config config = (new PagedList.Config.Builder()).setEnablePlaceholders(true)
                .setInitialLoadSizeHint(5)
                .setPageSize(5).build();

        //create LiveData object using LivePagedListBuilder which takes
        //data source factory and page config as params
        couponList = new LivePagedListBuilder<>(factory, config).build();
    }

    //factory for creating view model,
    // required because we need to pass Application to view model object
    public static class CouponViewModelFactory extends ViewModelProvider.NewInstanceFactory {
        private Application mApplication;

        public CouponViewModelFactory(Application application) {
            mApplication = application;
        }

        @Override
        public <T extends ViewModel> T create(Class<T> viewModel) {
            return (T) new CouponViewModel(mApplication);
        }
    }
}