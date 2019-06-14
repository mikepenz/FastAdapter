package com.mikepenz.fastadapter;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by fabianterhorst on 29.03.16.
 */
public class ExpandableTestDataGenerator {
    public static List<ExpandableTestItem> genTestItemList(int size) {
        List<ExpandableTestItem> list = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            ExpandableTestItem expandableTestItem = genTestItem(i);
            list.add(expandableTestItem);
        }
        return list;
    }

    public static List<ExpandableTestItem> genTestItemWithSubItemsList(int size, int levels) {
        return genTestItemWithSubItemsList(size, levels, new AtomicInteger(0));
    }

    private static List<ExpandableTestItem> genTestItemWithSubItemsList(int size, int levels, AtomicInteger atomicInteger) {
        List<ExpandableTestItem> list = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            ExpandableTestItem expandableTestItem = genTestItem(atomicInteger.incrementAndGet());
            if (levels > 0) {
                expandableTestItem.getSubItems().addAll(genTestItemWithSubItemsList(size, levels - 1, atomicInteger));
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

    public int count(List<ExpandableTestItem> items) {
        AtomicInteger atomicInteger = new AtomicInteger(0);
        count(items, atomicInteger);
        return atomicInteger.get();
    }

    private void count(List<ExpandableTestItem> items, AtomicInteger atomicInteger) {
        for (ExpandableTestItem expandableTestItem : items) {
            count(expandableTestItem, atomicInteger);
        }
    }

    private void count(ExpandableTestItem item, AtomicInteger atomicInteger) {
        atomicInteger.incrementAndGet();
        if (item.getSubItems() == null) {
            return;
        }
        for (ISubItem<?> expandableTestItem : item.getSubItems()) {
            if (expandableTestItem instanceof ExpandableTestItem) {
                atomicInteger.incrementAndGet();
                count((ExpandableTestItem) expandableTestItem, atomicInteger);
            }
        }
    }
}
