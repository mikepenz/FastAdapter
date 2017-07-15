package com.mikepenz.fastadapter.adapters;

import com.mikepenz.fastadapter.IInterceptor;
import com.mikepenz.fastadapter.IItem;

/**
 * Created by mikepenz on 27.12.15.
 * A general ItemAdapter implementation based on the AbstractAdapter to speed up development for general items
 */
public class ItemAdapter<Item extends IItem> extends ModelAdapter<Item, Item> {

    public ItemAdapter() {
        super(new IInterceptor<Item, Item>() {
            @Override
            public Item intercept(Item item) {
                return item;
            }
        });
    }

    /**
     * static method to retrieve a new `ItemAdapter`
     *
     * @return a new ItemAdapter
     */
    public static <Item extends IItem> ItemAdapter<Item> items() {
        return new ItemAdapter<>();
    }
}
