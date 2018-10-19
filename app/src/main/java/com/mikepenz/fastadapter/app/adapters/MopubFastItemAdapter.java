package com.mikepenz.fastadapter.app.adapters;

import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.mopub.nativeads.MoPubRecyclerAdapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by mikepenz on 28.06.16.
 */

public class MopubFastItemAdapter<Item extends IItem<? extends RecyclerView.ViewHolder>> extends FastItemAdapter<Item> {
    private MoPubRecyclerAdapter mMoPubAdAdapter;

    public MopubFastItemAdapter() {
        setLegacyBindViewMode(true);
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
