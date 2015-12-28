package com.mikepenz.fastadapter;

/**
 * Created by mikepenz on 27.12.15.
 */
public interface IAdapter {
    void setBaseAdapter(FastAdapter baseAdapter);

    FastAdapter getBaseAdapter();

    /*
    AbstractAdapter getParentAdapter();
    */

    int getOrder();

    int getAdapterItemCount();

    IItem getAdapterItem(int position);

    int getItemCount();

    IItem getItem(int position);
}
