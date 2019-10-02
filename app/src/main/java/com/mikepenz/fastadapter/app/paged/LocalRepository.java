package com.mikepenz.fastadapter.app.paged;

import android.content.Context;

import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class LocalRepository {
    private static CouponsDB couponDB;
    private static final Object LOCK = new Object();
    private static Context ctx;

    public synchronized static CouponsDB getCouponDB(Context context) {
        if (couponDB == null) {
            ctx = context;
            synchronized (LOCK) {
                if (couponDB == null) {
                    couponDB = Room.databaseBuilder(context,
                            CouponsDB.class, "Coupons Database")
                            .fallbackToDestructiveMigration()
                            .addCallback(dbCallback).build();

                }
            }
        }
        return couponDB;
    }

    private static RoomDatabase.Callback dbCallback = new RoomDatabase.Callback() {
        public void onCreate(SupportSQLiteDatabase db) {

            Executors.newSingleThreadScheduledExecutor().execute(new Runnable() {
                @Override
                public void run() {
                    addCoupons(ctx);
                }
            });
        }
    };

    private static void addCoupons(Context ctx) {
        List<Coupon> couponList = new ArrayList<Coupon>();

        for (String s : initCoupons) {
            String[] ss = s.split("\\|");
            couponList.add(new Coupon(ss[0], ss[1]));
        }
        getCouponDB(ctx).couponDAO().insertCoupons(couponList);
    }

    private static String[] initCoupons = {"amazon|falt 20% off on fashion",
            "amazon|upto 30% off on electronics",
            "ebay|falt 20% off on fashion", "ebay|upto 40% off on electronics",
            "nordstorm|falt 30% off on fashion", "bestbuy|upto 80% off on electronics",
            "sears|falt 60% off on fashion", "ee|upto 40% off on electronics",
            "macys|falt 30% off on fashion", "alibaba|upto 90% off on electronics",
            "nordstorm|falt 90% off on fashion", "ebay|upto 40% off on electronics",
            "nordstorm|falt 30% off on fashion", "ebay|upto 70% off on electronics",
            "jcpenny|falt 50% off on fashion", "ebay|upto 50% off on electronics",
            "khols|falt 70% off on fashion", "ebay|upto 40% off on electronics",
            "target|falt 30% off on fashion", "ebay|upto 20% off on electronics",
            "costco|falt 80% off on fashion", "ebay|upto 40% off on electronics",
            "walmart|falt 10% off on fashion", "ebay|upto 10% off on electronics",
            "nordstorm|falt 30% off on fashion", "ebay|upto 70% off on electronics",
            "ebay|falt 40% off on fashion", "ebay|upto 40% off on electronics",
            "nordstorm|falt 70% off on fashion", "ebay|upto 80% off on electronics",
            "nordstorm|falt 30% off on fashion", "ebay|upto 40% off on electronics",
            "nordstorm|falt 60% off on fashion", "ebay|upto 50% off on electronics",
            "ebay|falt 30% off on fashion", "ebay|upto 70% off on electronics",
            "ebay|falt 30% off on fashion", "ebay|upto 40% off on electronics",
            "uuuu|falt 30% off on fashion", "ebay|upto 40% off on electronics",
            "tttt|falt 30% off on fashion", "ebay|upto 40% off on electronics",
            "ssss|falt 30% off on fashion", "ebay|upto 40% off on electronics",
            "eee|falt 30% off on fashion", "ebay|upto 40% off on electronics",
            "www|falt 30% off on fashion", "ebay|upto 40% off on electronics",
            "rrrr|falt 30% off on fashion", "tyyyy|upto 40% off on electronics",
            "vvvv|falt 30% off on fashion", "wwwwe|upto 40% off on electronics",
            "bbbb|falt 30% off on fashion", "ssssssssssssa|upto 40% off on electronics",
            "mmmm|falt 30% off on fashion", "rrtttt|upto 40% off on electronics",

    };
}