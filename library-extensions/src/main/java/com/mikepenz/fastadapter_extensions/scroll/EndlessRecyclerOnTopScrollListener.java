package com.mikepenz.fastadapter_extensions.scroll;

import android.support.v7.widget.RecyclerView;

/**
 * Created by fabianterhorst on 21.09.16.
 */

public abstract class EndlessRecyclerOnTopScrollListener extends RecyclerView.OnScrollListener {

    private int mDistanceScrolledVertical = 0;

    private int mCurrentPage = 1;

    private boolean mLoading = false;

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        mDistanceScrolledVertical += dy;
        int scrollDistance = mDistanceScrolledVertical;
        if (mCurrentPage > 1) {
            scrollDistance = (mDistanceScrolledVertical / (mCurrentPage - 1))
                    + (recyclerView.computeVerticalScrollRange() / mCurrentPage);
        }
        if (scrollDistance == 0 && !isLoading()) {
            mCurrentPage++;
            onLoadMore(mCurrentPage);
        }
    }

    public void setLoading(boolean loading) {
        this.mLoading = loading;
    }

    public boolean isLoading() {
        return mLoading;
    }

    public int getCurrentPage() {
        return mCurrentPage;
    }

    public abstract void onLoadMore(int currentPage);
}
