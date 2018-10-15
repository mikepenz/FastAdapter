package com.mikepenz.fastadapter;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fabianterhorst on 29.03.16.
 */
public class TestDataGenerator {
    public static List<ExpandableTestItem> genTestItemList(int size) {
        List<ExpandableTestItem> list = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            ExpandableTestItem expandableTestItem = genTestItem(i);
            list.add(expandableTestItem);
        }
        return list;
    }

    public static List<ExpandableTestItem> genTestItemWithSubItemsList(int size, int levels) {
        List<ExpandableTestItem> list = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            ExpandableTestItem expandableTestItem = genTestItem(i);
            if (levels > 0) {
                expandableTestItem.setSubItems(genTestItemWithSubItemsList(size, levels - 1));
            }
            list.add(expandableTestItem);
        }
        return list;
    }

    @NonNull
    public static ExpandableTestItem genTestItem(int i) {
        ExpandableTestItem expandableTestItem = new ExpandableTestItem();
        expandableTestItem.setIdentifier(i);
        return expandableTestItem;
    }
}
