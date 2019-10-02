package com.mikepenz.fastadapter.app.paged;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Coupon.class}, version = 1)
public abstract class CouponsDB extends RoomDatabase {
    public abstract CouponLocalDAO couponDAO();
}