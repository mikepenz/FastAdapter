package com.mikepenz.fastadapter.scroll

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.OrientationHelper
import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.fastadapter.FastAdapter

abstract class EndlessRecyclerOnTopScrollListener : RecyclerView.OnScrollListener {
    private var mPreviousTotal = 0
    private var mLoading = true
    var currentPage = 0
        private set
    private var mAdapter: FastAdapter<*>? = null
    private lateinit var mLayoutManager: LinearLayoutManager
    var visibleThreshold = -1
    private var mOrientationHelper: OrientationHelper? = null
    private var mIsOrientationHelperVertical: Boolean = false
    var firstVisibleItem: Int = 0
        private set
    var visibleItemCount: Int = 0
        private set
    var totalItemCount: Int = 0
        private set
    private var mAlreadyCalledOnNoMore: Boolean = false
    // how many items your adapter must have at the end?
    // leave it -1 as its by default to disable onNothingToLoad() feature if you have only local data
    var totalLoadedItems = -1

    val isNothingToLoadFeatureEnabled: Boolean
        get() = totalLoadedItems != -1

    private val isNothingToLoadNeeded: Boolean
        get() = mAdapter?.itemCount == totalLoadedItems && !mAlreadyCalledOnNoMore

    val layoutManager: RecyclerView.LayoutManager?
        get() = mLayoutManager

    constructor(adapter: FastAdapter<*>, totalItems: Int) {
        this.mAdapter = adapter
        totalLoadedItems = totalItems
    }

    constructor(adapter: FastAdapter<*>) {
        this.mAdapter = adapter
    }

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        if (!::mLayoutManager.isInitialized) {
            this.mLayoutManager = recyclerView.layoutManager as LinearLayoutManager?
                    ?: throw RuntimeException("A layoutmanager is required")
        }

        if (visibleThreshold == -1) {
            visibleThreshold = findLastVisibleItemPosition(recyclerView) - findFirstVisibleItemPosition(recyclerView)
        }

        visibleItemCount = recyclerView.childCount
        totalItemCount = mLayoutManager.itemCount
        firstVisibleItem = findFirstVisibleItemPosition(recyclerView)

        totalItemCount = mAdapter?.itemCount ?: 0

        if (mLoading) {
            if (totalItemCount > mPreviousTotal) {
                mLoading = false
                mPreviousTotal = totalItemCount
            }
        }

        if (!mLoading && mLayoutManager.findFirstVisibleItemPosition() - visibleThreshold <= 0) {
            currentPage++

            onLoadMore(currentPage)

            mLoading = true
        } else {
            if (isNothingToLoadFeatureEnabled && isNothingToLoadNeeded) {
                onNothingToLoad()
                mAlreadyCalledOnNoMore = true
            }
        }
    }

    private fun findLastVisibleItemPosition(recyclerView: RecyclerView): Int {
        val child = findOneVisibleChild(recyclerView.childCount - 1, -1, false, true)
        return if (child == null) RecyclerView.NO_POSITION else recyclerView.getChildAdapterPosition(child)
    }

    private fun findFirstVisibleItemPosition(recyclerView: RecyclerView): Int {
        val child = findOneVisibleChild(0, mLayoutManager.childCount, false, true)
        return if (child == null) RecyclerView.NO_POSITION else recyclerView.getChildAdapterPosition(child)
    }

    /**
     * load more data
     *
     * @param page page number, starts from 0
     */
    abstract fun onLoadMore(page: Int)

    /**
     * there's no more data to be loaded, you may want
     * to send a request to server for asking more data
     */
    abstract fun onNothingToLoad()

    private fun findOneVisibleChild(fromIndex: Int, toIndex: Int, completelyVisible: Boolean,
                                    acceptPartiallyVisible: Boolean): View? {
        if (mLayoutManager.canScrollVertically() != mIsOrientationHelperVertical || mOrientationHelper == null) {
            mIsOrientationHelperVertical = mLayoutManager.canScrollVertically()
            mOrientationHelper = if (mIsOrientationHelperVertical)
                OrientationHelper.createVerticalHelper(mLayoutManager)
            else
                OrientationHelper.createHorizontalHelper(mLayoutManager)
        }

        val mOrientationHelper = this.mOrientationHelper ?: return null
        val start = mOrientationHelper.startAfterPadding
        val end = mOrientationHelper.endAfterPadding
        val next = if (toIndex > fromIndex) 1 else -1
        var partiallyVisible: View? = null
        var i = fromIndex
        while (i != toIndex) {
            val child = mLayoutManager.getChildAt(i)
            if (child != null) {
                val childStart = mOrientationHelper.getDecoratedStart(child)
                val childEnd = mOrientationHelper.getDecoratedEnd(child)
                if (childStart < end && childEnd > start) {
                    if (completelyVisible) {
                        if (childStart >= start && childEnd <= end) {
                            return child
                        } else if (acceptPartiallyVisible && partiallyVisible == null) {
                            partiallyVisible = child
                        }
                    } else {
                        return child
                    }
                }
            }
            i += next
        }
        return partiallyVisible
    }

    /**
     * reset page count
     */
    fun resetPageCount() {
        this.resetPageCount(0)
    }

    /**
     * reset page count to specified page
     *
     * @param page page number, starts from 0
     */
    fun resetPageCount(page: Int) {
        this.mPreviousTotal = 0
        this.mLoading = true
        this.currentPage = page
        this.onLoadMore(this.currentPage)
    }
}
