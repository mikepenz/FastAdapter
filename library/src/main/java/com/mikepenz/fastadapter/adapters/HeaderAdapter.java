package com.mikepenz.fastadapter.adapters;

import com.mikepenz.fastadapter.IItem;

/**
 * Created by mikepenz on 27.12.15.
 * Based on the ItemAdapter, with a different order to show it's items before the ItemAdapter
 */
public class HeaderAdapter<Item extends IItem> extends ItemAdapter<Item> {
    /**
     * @return
     */
    @Override
    public int getOrder() {
        return 100;
    }

}
