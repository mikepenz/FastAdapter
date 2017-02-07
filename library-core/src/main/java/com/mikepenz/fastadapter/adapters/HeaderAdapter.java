package com.mikepenz.fastadapter.adapters;

import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.adapters.ItemAdapter;

/**
 * Created by mikepenz on 27.12.15.
 * Based on the ItemAdapter, with a different order to show its items before the ItemAdapter
 */
public class HeaderAdapter<Item extends IItem> extends ItemAdapter<Item> {
    /**
     * @return the order
     */
    @Override
    public int getOrder() {
        return 100;
    }

}
