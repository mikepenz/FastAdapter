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

class CustomHeaderViewCache(adapter: StickyRecyclerHeadersAdapter<*>, private var moPubRecyclerAdapter: MoPubRecyclerAdapter, orientationProvider: OrientationProvider) : HeaderViewCache(adapter, orientationProvider) {

    override fun getHeader(parent: RecyclerView, position: Int): View {
        var originalPosition = moPubRecyclerAdapter.getOriginalPosition(position)
        if (originalPosition < 0) {
            originalPosition = if (position == 0) {
                0
            } else {
                moPubRecyclerAdapter.getOriginalPosition(position - 1)
            }
        }
        return super.getHeader(parent, originalPosition)
    }
}
