package com.mikepenz.fastadapter.app.adapters

import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.fastadapter.GenericItem
import com.mikepenz.fastadapter.adapters.FastItemAdapter
import com.mopub.nativeads.MoPubRecyclerAdapter

/**
 * Created by mikepenz on 28.06.16.
 */

class MopubFastItemAdapter<Item : GenericItem> : FastItemAdapter<Item>() {
    private var mMoPubAdAdapter: MoPubRecyclerAdapter? = null

    init {
        legacyBindViewMode = true
    }

    fun withMoPubAdAdapter(moPubAdAdapter: MoPubRecyclerAdapter): MopubFastItemAdapter<*> {
        this.mMoPubAdAdapter = moPubAdAdapter
        return this
    }

    override fun getHolderAdapterPosition(holder: RecyclerView.ViewHolder): Int {
        return mMoPubAdAdapter!!.getOriginalPosition(super.getHolderAdapterPosition(holder))
    }
}
