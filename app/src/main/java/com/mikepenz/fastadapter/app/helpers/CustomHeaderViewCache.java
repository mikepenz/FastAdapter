package com.mikepenz.fastadapter.app.helpers;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.mopub.nativeads.MoPubRecyclerAdapter;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;
import com.timehop.stickyheadersrecyclerview.caching.HeaderViewCache;
import com.timehop.stickyheadersrecyclerview.util.OrientationProvider;

/**
 * Created by Gagan on 5/3/2017.
 */

public class CustomHeaderViewCache extends HeaderViewCache
{
    private MoPubRecyclerAdapter moPubRecyclerAdapter;

    public CustomHeaderViewCache(StickyRecyclerHeadersAdapter adapter, OrientationProvider orientationProvider) {
        super(adapter, orientationProvider);
    }

    public CustomHeaderViewCache(StickyRecyclerHeadersAdapter adapter, MoPubRecyclerAdapter moPubRecyclerAdapter,  OrientationProvider orientationProvider) {
        super(adapter, orientationProvider);
        this.moPubRecyclerAdapter = moPubRecyclerAdapter;
    }

    @Override
    public View getHeader(RecyclerView parent, int position) {
        int originalPosition = moPubRecyclerAdapter.getOriginalPosition(position);
        if (originalPosition < 0) {
            if (position == 0) {
                originalPosition = 0;
            } else {
                originalPosition = moPubRecyclerAdapter.getOriginalPosition(position - 1);
            }
        }
        return super.getHeader(parent, originalPosition);
    }
}
