package com.mikepenz.fastadapter.app.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.mopub.nativeads.MoPubRecyclerAdapter;

/**
 * Created by mikepenz on 28.06.16.
 */

public class MopubFastItemAdapter<Item extends IItem> extends FastItemAdapter<Item> {
    private MoPubRecyclerAdapter mMoPubAdAdapter;

    public MopubFastItemAdapter() {
        withLegacyBindViewMode(true);
    }

    public MopubFastItemAdapter withMoPubAdAdapter(MoPubRecyclerAdapter moPubAdAdapter) {
        this.mMoPubAdAdapter = moPubAdAdapter;
        return this;
    }

    @Override
    public int getHolderAdapterPosition(@NonNull RecyclerView.ViewHolder holder) {
        return mMoPubAdAdapter.getOriginalPosition(super.getHolderAdapterPosition(holder));
    }
}
