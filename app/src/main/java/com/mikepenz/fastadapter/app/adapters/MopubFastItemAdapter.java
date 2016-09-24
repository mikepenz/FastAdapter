package com.mikepenz.fastadapter.app.adapters;

import android.support.v7.widget.RecyclerView;

import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.adapters.FastItemAdapter;
import com.mopub.nativeads.MoPubRecyclerAdapter;

import java.util.Collections;

/**
 * Created by mikepenz on 28.06.16.
 */

public class MopubFastItemAdapter<Item extends IItem> extends FastItemAdapter<Item> {
    private MoPubRecyclerAdapter mMoPubAdAdapter;

    public MopubFastItemAdapter withMoPubAdAdapter(MoPubRecyclerAdapter moPubAdAdapter) {
        this.mMoPubAdAdapter = moPubAdAdapter;
        return this;
    }

    @Override
    public int getHolderAdapterPosition(RecyclerView.ViewHolder holder) {
        return mMoPubAdAdapter.getOriginalPosition(super.getHolderAdapterPosition(holder));
    }

    /**
     * fix the MopubAdapter not correctly overwriting the proper onBindViewHolder methods
     *
     * @param holder   the viewHolder we bind the data on
     * @param position the global position
     */
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position, Collections.EMPTY_LIST);
    }
}
