package com.mikepenz.fastadapter.app.paged;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.paging.PageKeyedDataSource;

import java.util.List;

//data source for PagedList, it is used for loading data for each page
public class CouponsDataSource extends PageKeyedDataSource<Integer, Coupon> {
    private CouponLocalDAO couponDAO;

    public CouponsDataSource(Context ctx) {
        couponDAO = LocalRepository.getCouponDB(ctx).couponDAO();
    }

    //is called too load initial data
    @Override
    public void loadInitial(@NonNull LoadInitialParams<Integer> params,
                            @NonNull LoadInitialCallback<Integer, Coupon> callback) {

        List<Coupon> cpns = couponDAO.getCouponsBySize(0, params.requestedLoadSize);

        //this is required to handle first request after db is created or app is installed
        int noOfTryies = 0;
        while (cpns.size() == 0) {
            cpns = couponDAO.getCouponsBySize(0, params.requestedLoadSize);
            noOfTryies++;
            if (noOfTryies == 6) {
                break;
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
            }
        }

        callback.onResult(cpns, null,
                cpns.size() + 1);

    }

    @Override
    public void loadBefore(@NonNull LoadParams<Integer> params,
                           @NonNull LoadCallback<Integer, Coupon> callback) {

    }

    //is called to load pages of data using key passed in params
    @Override
    public void loadAfter(@NonNull LoadParams<Integer> params,
                          @NonNull LoadCallback<Integer, Coupon> callback) {
        List<Coupon> cpns = couponDAO.getCouponsBySize(params.key, params.requestedLoadSize);
        int nextKey = params.key + cpns.size();
        callback.onResult(cpns, nextKey);
    }
}