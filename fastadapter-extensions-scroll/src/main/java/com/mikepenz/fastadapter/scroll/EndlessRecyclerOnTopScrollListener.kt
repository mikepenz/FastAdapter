package com.mikepenz.fastadapter.scroll

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.OrientationHelper
import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.fastadapter.FastAdapter

abstract class EndlessRecyclerOnTopScrollListener : RecyclerView.OnScrollListener {
    private var previousTotal = 0
    private var isLoading = true
    private var adapter: FastAdapter<*>? = null
    private var orientationHelper: OrientationHelper? = null
    private var isOrientationHelperVertical: Boolean = false
    private var alreadyCalledOnNoMore: Boolean = false

    private lateinit var _layoutManager: LinearLayoutManager

    var currentPage = 0
        private set
    var firstVisibleItem: Int = 0
        private set
    var visibleItemCount: Int = 0
        private set
    var totalItemCount: Int = 0
        private set
    var visibleThreshold = RecyclerView.NO_POSITION
    // how many items your adapter must have at the end?
    // leave it -1 as its by default to disable onNothingToLoad() feature if you have only local data
    var totalLoadedItems = -1

    val isNothingToLoadFeatureEnabled: Boolean
        get() = totalLoadedItems != -1

    private val isNothingToLoadNeeded: Boolean
        get() = adapter?.itemCount == totalLoadedItems && !alreadyCalledOnNoMore

    val layoutManager: RecyclerView.LayoutManager?
        get() = _layoutManager

    constructor(adapter: FastAdapter<*>, totalItems: Int) {
        this.adapter = adapter
        totalLoadedItems = totalItems
    }

    constructor(adapter: FastAdapter<*>) {
        this.adapter = adapter
    }

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        if (!::_layoutManager.isInitialized) {
            this._layoutManager = recyclerView.layoutManager as LinearLayoutManager?
                    ?: throw RuntimeException("A layoutmanager is required")
        }

        if (visibleThreshold == RecyclerView.NO_POSITION) {
            visibleThreshold = findLastVisibleItemPosition(recyclerView) - findFirstVisibleItemPosition(recyclerView)
        }

        visibleItemCount = recyclerView.childCount
        totalItemCount = _layoutManager.itemCount
        firstVisibleItem = findFirstVisibleItemPosition(recyclerView)

        totalItemCount = adapter?.itemCount ?: 0

        if (isLoading) {
            if (totalItemCount > previousTotal) {
                isLoading = false
                previousTotal = totalItemCount
            }
        }

        if (!isLoading && _layoutManager.findFirstVisibleItemPosition() - visibleThreshold <= 0) {
            currentPage++

            onLoadMore(currentPage)

            isLoading = true
        } else {
            if (isNothingToLoadFeatureEnabled && isNothingToLoadNeeded) {
                onNothingToLoad()
                alreadyCalledOnNoMore = true
            }
        }
    }

    private fun findLastVisibleItemPosition(recyclerView: RecyclerView): Int {
        val child = findOneVisibleChild(recyclerView.childCount - 1, -1, false, true)
        return if (child == null) RecyclerView.NO_POSITION else recyclerView.getChildAdapterPosition(child)
    }

    private fun findFirstVisibleItemPosition(recyclerView: RecyclerView): Int {
        val child = findOneVisibleChild(0, _layoutManager.childCount, false, true)
        return if (child == null) RecyclerView.NO_POSITION else recyclerView.getChildAdapterPosition(child)
    }

    /**
     * Load more data
     *
     * @param page page number, starts from 0
     */
    abstract fun onLoadMore(page: Int)

    /**
     * There's no more data to be loaded, you may want
     * to send a request to server for asking more data
     */
    abstract fun onNothingToLoad()

    private fun findOneVisibleChild(fromIndex: Int, toIndex: Int, completelyVisible: Boolean,
                                    acceptPartiallyVisible: Boolean): View? {
        if (_layoutManager.canScrollVertically() != isOrientationHelperVertical || orientationHelper == null) {
            isOrientationHelperVertical = _layoutManager.canScrollVertically()
            orientationHelper = if (isOrientationHelperVertical)
                OrientationHelper.createVerticalHelper(_layoutManager)
            else
                OrientationHelper.createHorizontalHelper(_layoutManager)
        }

        val mOrientationHelper = this.orientationHelper ?: return null
        val start = mOrientationHelper.startAfterPadding
        val end = mOrientationHelper.endAfterPadding
        val next = if (toIndex > fromIndex) 1 else -1
        var partiallyVisible: View? = null
        var i = fromIndex
        while (i != toIndex) {
            val child = _layoutManager.getChildAt(i)
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
     * Reset page count
     */
    fun resetPageCount() {
        this.resetPageCount(0)
    }

    /**
     * Reset page count to specified page
     *
     * @param page page number, starts from 0
     */
    fun resetPageCount(page: Int) {
        this.previousTotal = 0
        this.isLoading = true
        this.currentPage = page
        this.onLoadMore(this.currentPage)
    }
}
