package com.mikepenz.fastadapter.adapters;

/**
 * Created by mikepenz on 27.12.15.
 * Based on the ItemAdapter, with a different order to show it's items after the ItemAdapter
 */
public class FooterAdapter extends ItemAdapter {

    /**
     * @return
     */
    @Override
    public int getOrder() {
        return 1000;
    }

}
