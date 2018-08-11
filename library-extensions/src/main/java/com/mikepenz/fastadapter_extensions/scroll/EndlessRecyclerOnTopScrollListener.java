package com.mikepenz.fastadapter_extensions.scroll;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import com.mikepenz.fastadapter.FastAdapter;

public abstract class EndlessRecyclerOnTopScrollListener extends RecyclerView.OnScrollListener {
    private int mPreviousTotal = 0;
    private boolean mLoading = true;
    private int mCurrentPage = 0;
    private FastAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private int mVisibleThreshold = -1;
    private OrientationHelper mOrientationHelper;
    private boolean mIsOrientationHelperVertical;
    private int mFirstVisibleItem, mVisibleItemCount, mTotalItemCount;
    private boolean mAlreadyCalledOnNoMore;
    // how many items your adapter must have at the end?
    // leave it -1 as its by default to disable onNothingToLoad() feature if you have only local data
    private int mTotalItems = -1;

    public EndlessRecyclerOnTopScrollListener(FastAdapter adapter, int totalItems) {
        this.mAdapter = adapter;
        mTotalItems = totalItems;
    }

    public EndlessRecyclerOnTopScrollListener(FastAdapter adapter) {
        this.mAdapter = adapter;
    }

    public int getVisibleThreshold() {
        return mVisibleThreshold;
    }

    public void setVisibleThreshold(int visibleThreshold) {
        this.mVisibleThreshold = visibleThreshold;
    }

    public int getTotalLoadedItems() {
        return mTotalItems;
    }

    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        if (this.mLayoutManager == null) {
            this.mLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        }

        if (mVisibleThreshold == -1) {
            mVisibleThreshold = findLastVisibleItemPosition(recyclerView) - findFirstVisibleItemPosition(recyclerView);
        }

        mVisibleItemCount = recyclerView.getChildCount();
        mTotalItemCount = mLayoutManager.getItemCount();
        mFirstVisibleItem = findFirstVisibleItemPosition(recyclerView);

        mTotalItemCount = mAdapter.getItemCount();

        if (mLoading) {
            if (mTotalItemCount > mPreviousTotal) {
                mLoading = false;
                mPreviousTotal = mTotalItemCount;
            }
        }

        if (!mLoading && mLayoutManager.findFirstVisibleItemPosition() - mVisibleThreshold <= 0) {
            mCurrentPage++;

            onLoadMore(mCurrentPage);

            mLoading = true;
        } else {
            if (isNothingToLoadFeatureEnabled() && isNothingToLoadNeeded()) {
                onNothingToLoad();
                mAlreadyCalledOnNoMore = true;
            }
        }
    }

    private int findLastVisibleItemPosition(RecyclerView recyclerView) {
        final View child = findOneVisibleChild(recyclerView.getChildCount() - 1, -1, false, true);
        return child == null ? RecyclerView.NO_POSITION : recyclerView.getChildAdapterPosition(child);
    }

    private int findFirstVisibleItemPosition(RecyclerView recyclerView) {
        final View child = findOneVisibleChild(0, mLayoutManager.getChildCount(), false, true);
        return child == null ? RecyclerView.NO_POSITION : recyclerView.getChildAdapterPosition(child);
    }

    /**
     * load more data
     *
     * @param page page number, starts from 0
     */
    public abstract void onLoadMore(int page);

    public boolean isNothingToLoadFeatureEnabled() {
        return mTotalItems != -1;
    }

    private boolean isNothingToLoadNeeded() {
        return mAdapter.getItemCount() == mTotalItems && !mAlreadyCalledOnNoMore;
    }

    /**
     * there's no more data to be loaded, you may want
     * to send a request to server for asking more data
     **/
    public abstract void onNothingToLoad();

    private View findOneVisibleChild(int fromIndex, int toIndex, boolean completelyVisible,
                                     boolean acceptPartiallyVisible) {
        if (mLayoutManager.canScrollVertically() != mIsOrientationHelperVertical
                || mOrientationHelper == null) {
            mIsOrientationHelperVertical = mLayoutManager.canScrollVertically();
            mOrientationHelper = mIsOrientationHelperVertical
                    ? OrientationHelper.createVerticalHelper(mLayoutManager)
                    : OrientationHelper.createHorizontalHelper(mLayoutManager);
        }

        final int start = mOrientationHelper.getStartAfterPadding();
        final int end = mOrientationHelper.getEndAfterPadding();
        final int next = toIndex > fromIndex ? 1 : -1;
        View partiallyVisible = null;
        for (int i = fromIndex; i != toIndex; i += next) {
            final View child = mLayoutManager.getChildAt(i);
            if (child != null) {
                final int childStart = mOrientationHelper.getDecoratedStart(child);
                final int childEnd = mOrientationHelper.getDecoratedEnd(child);
                if (childStart < end && childEnd > start) {
                    if (completelyVisible) {
                        if (childStart >= start && childEnd <= end) {
                            return child;
                        } else if (acceptPartiallyVisible && partiallyVisible == null) {
                            partiallyVisible = child;
                        }
                    } else {
                        return child;
                    }
                }
            }
        }
        return partiallyVisible;
    }

    /**
     * reset page count
     */
    public void resetPageCount() {
        this.resetPageCount(0);
    }

    /**
     * reset page count to specified page
     *
     * @param page page number, starts from 0
     */
    public void resetPageCount(int page) {
        this.mPreviousTotal = 0;
        this.mLoading = true;
        this.mCurrentPage = page;
        this.onLoadMore(this.mCurrentPage);
    }

    public RecyclerView.LayoutManager getLayoutManager() {
        return mLayoutManager;
    }

    public int getTotalItemCount() {
        return mTotalItemCount;
    }

    public int getFirstVisibleItem() {
        return mFirstVisibleItem;
    }

    public int getVisibleItemCount() {
        return mVisibleItemCount;
    }

    public int getCurrentPage() {
        return mCurrentPage;
    }
}
