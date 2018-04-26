package com.mikepenz.fastadapter;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fabianterhorst on 29.03.16.
 */
public class TestDataGenerator {
    public static List<TestItem> genTestItemList(int size) {
        List<TestItem> list = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            TestItem testItem = genTestItem(i);
            list.add(testItem);
        }
        return list;
    }

    @NonNull
    public static TestItem genTestItem(int i) {
        return new TestItem(i + "").withIdentifier(i);
    }
}
