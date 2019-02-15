package com.mikepenz.fastadapter.scroll

import android.view.View
import androidx.recyclerview.widget.OrientationHelper
import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.fastadapter.adapters.ItemAdapter

abstract class EndlessRecyclerOnScrollListener : RecyclerView.OnScrollListener {

    private var enabled = true
    private var mPreviousTotal = 0
    private var mLoading = true
    private var mVisibleThreshold = -1
    var firstVisibleItem: Int = 0
        private set
    var visibleItemCount: Int = 0
        private set
    var totalItemCount: Int = 0
        private set

    private var mIsOrientationHelperVertical: Boolean = false
    private var mOrientationHelper: OrientationHelper? = null

    var currentPage = 0
        private set

    private var mFooterAdapter: ItemAdapter<*>? = null

    lateinit var layoutManager: RecyclerView.LayoutManager
        private set

    constructor() {}

    /**
     * @param adapter the ItemAdapter used to host footer items
     */
    constructor(adapter: ItemAdapter<*>) {
        this.mFooterAdapter = adapter
    }

    constructor(layoutManager: RecyclerView.LayoutManager) {
        this.layoutManager = layoutManager
    }

    constructor(visibleThreshold: Int) {
        this.mVisibleThreshold = visibleThreshold
    }

    constructor(layoutManager: RecyclerView.LayoutManager, visibleThreshold: Int) {
        this.layoutManager = layoutManager
        this.mVisibleThreshold = visibleThreshold
    }

    /**
     * @param layoutManager
     * @param visibleThreshold
     * @param footerAdapter    the ItemAdapter used to host footer items
     */
    constructor(layoutManager: RecyclerView.LayoutManager, visibleThreshold: Int, footerAdapter: ItemAdapter<*>) {
        this.layoutManager = layoutManager
        this.mVisibleThreshold = visibleThreshold
        this.mFooterAdapter = footerAdapter
    }

    private fun findFirstVisibleItemPosition(recyclerView: RecyclerView): Int {
        val child = findOneVisibleChild(0, layoutManager.childCount, false, true)
        return if (child == null) RecyclerView.NO_POSITION else recyclerView.getChildAdapterPosition(child)
    }

    private fun findLastVisibleItemPosition(recyclerView: RecyclerView): Int {
        val child = findOneVisibleChild(recyclerView.childCount - 1, -1, false, true)
        return if (child == null) RecyclerView.NO_POSITION else recyclerView.getChildAdapterPosition(child)
    }

    private fun findOneVisibleChild(fromIndex: Int, toIndex: Int, completelyVisible: Boolean,
                                    acceptPartiallyVisible: Boolean): View? {
        if (layoutManager.canScrollVertically() != mIsOrientationHelperVertical || mOrientationHelper == null) {
            mIsOrientationHelperVertical = layoutManager.canScrollVertically()
            mOrientationHelper = if (mIsOrientationHelperVertical)
                OrientationHelper.createVerticalHelper(layoutManager)
            else
                OrientationHelper.createHorizontalHelper(layoutManager)
        }

        val mOrientationHelper = this.mOrientationHelper ?: return null

        val start = mOrientationHelper.startAfterPadding
        val end = mOrientationHelper.endAfterPadding
        val next = if (toIndex > fromIndex) 1 else -1
        var partiallyVisible: View? = null
        var i = fromIndex
        while (i != toIndex) {
            val child = layoutManager.getChildAt(i)
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

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        if (enabled) {
            if (!::layoutManager.isInitialized)
                layoutManager = recyclerView.layoutManager
                        ?: throw RuntimeException("A LayoutManager is required")

            val footerItemCount = mFooterAdapter?.adapterItemCount ?: 0

            if (mVisibleThreshold == -1)
                mVisibleThreshold = findLastVisibleItemPosition(recyclerView) - findFirstVisibleItemPosition(recyclerView) - footerItemCount

            visibleItemCount = recyclerView.childCount - footerItemCount
            totalItemCount = layoutManager.itemCount - footerItemCount
            firstVisibleItem = findFirstVisibleItemPosition(recyclerView)

            if (mLoading) {
                if (totalItemCount > mPreviousTotal) {
                    mLoading = false
                    mPreviousTotal = totalItemCount
                }
            }
            if (!mLoading && totalItemCount - visibleItemCount <= firstVisibleItem + mVisibleThreshold) {

                currentPage++

                onLoadMore(currentPage)

                mLoading = true
            }
        }
    }

    fun enable(): EndlessRecyclerOnScrollListener {
        enabled = true
        return this
    }

    fun disable(): EndlessRecyclerOnScrollListener {
        enabled = false
        return this
    }

    @JvmOverloads
    fun resetPageCount(page: Int = 0) {
        mPreviousTotal = 0
        mLoading = true
        currentPage = page
        onLoadMore(currentPage)
    }

    abstract fun onLoadMore(currentPage: Int)
}