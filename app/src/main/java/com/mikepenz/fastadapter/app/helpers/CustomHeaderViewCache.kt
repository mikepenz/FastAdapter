package com.mikepenz.fastadapter.app.helpers

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.mopub.nativeads.MoPubRecyclerAdapter
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter
import com.timehop.stickyheadersrecyclerview.caching.HeaderViewCache
import com.timehop.stickyheadersrecyclerview.util.OrientationProvider

/**
 * Created by Gagan on 5/3/2017.
 */

class CustomHeaderViewCache : HeaderViewCache {
    private var moPubRecyclerAdapter: MoPubRecyclerAdapter

    constructor(adapter: StickyRecyclerHeadersAdapter<*>, moPubRecyclerAdapter: MoPubRecyclerAdapter, orientationProvider: OrientationProvider) : super(adapter, orientationProvider) {
        this.moPubRecyclerAdapter = moPubRecyclerAdapter
    }

    override fun getHeader(parent: RecyclerView, position: Int): View {
        var originalPosition = moPubRecyclerAdapter.getOriginalPosition(position)
        if (originalPosition < 0) {
            if (position == 0) {
                originalPosition = 0
            } else {
                originalPosition = moPubRecyclerAdapter.getOriginalPosition(position - 1)
            }
        }
        return super.getHeader(parent, originalPosition)
    }
}
