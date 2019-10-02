package com.mikepenz.fastadapter.app.paged;

import android.content.Context;

import androidx.paging.DataSource;


public class CouponsDataSourceFactory extends DataSource.Factory<Integer, Coupon> {
    private Context ctx;
    private CouponsDataSource couponsDataSource;

    public CouponsDataSourceFactory(Context ctx) {
        this.ctx = ctx;
    }

    @Override
    public DataSource<Integer, Coupon> create() {
        if (couponsDataSource == null) {
            couponsDataSource = new CouponsDataSource(ctx);
        }
        return couponsDataSource;
    }
}