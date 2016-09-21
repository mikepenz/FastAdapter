package com.mikepenz.fastadapter_extensions.scroll;

import android.support.v7.widget.RecyclerView;

/**
 * Created by fabianterhorst on 21.09.16.
 */

public abstract class EndlessRecyclerOnTopScrollListener extends RecyclerView.OnScrollListener {

    private int mDistanceScrolledVertical = 0;

    private int mCurrentPage = 1;

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        mDistanceScrolledVertical += dy;
        if (mDistanceScrolledVertical == 0) {
            mCurrentPage++;
            onLoadMore(mCurrentPage);
        }
    }

    public int getCurrentPage() {
        return mCurrentPage;
    }

    public abstract void onLoadMore(int currentPage);
}
