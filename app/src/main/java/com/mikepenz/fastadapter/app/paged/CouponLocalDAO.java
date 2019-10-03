package com.mikepenz.fastadapter.app.paged;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface CouponLocalDAO {
    //to fetch data required to display in each page
    @Query("SELECT * FROM Coupon WHERE  _id >= :id ORDER BY _id LIMIT :size")
    public List<Coupon> getCouponsBySize(int id, int size);

    //this is used to populate db
    @Insert
    public void insertCoupons(List<Coupon> coupons);
}