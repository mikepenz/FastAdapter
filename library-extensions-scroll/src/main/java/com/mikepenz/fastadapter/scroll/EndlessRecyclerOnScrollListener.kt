package com.mikepenz.fastadapter.scroll

import android.view.View
import androidx.recyclerview.widget.OrientationHelper
import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.fastadapter.adapters.ItemAdapter

abstract class EndlessRecyclerOnScrollListener : RecyclerView.OnScrollListener {

    private var enabled = true
    private var previousTotal = 0
    private var isLoading = true
    private var visibleThreshold = RecyclerView.NO_POSITION
    var firstVisibleItem: Int = 0
        private set
    var visibleItemCount: Int = 0
        private set
    var totalItemCount: Int = 0
        private set

    private var isOrientationHelperVertical: Boolean = false
    private var orientationHelper: OrientationHelper? = null

    var currentPage = 0
        private set

    private var footerAdapter: ItemAdapter<*>? = null

    lateinit var layoutManager: RecyclerView.LayoutManager
        private set

    constructor()

    /**
     * @param adapter the ItemAdapter used to host footer items
     */
    constructor(adapter: ItemAdapter<*>) {
        this.footerAdapter = adapter
    }

    constructor(layoutManager: RecyclerView.LayoutManager) {
        this.layoutManager = layoutManager
    }

    constructor(visibleThreshold: Int) {
        this.visibleThreshold = visibleThreshold
    }

    constructor(layoutManager: RecyclerView.LayoutManager, visibleThreshold: Int) {
        this.layoutManager = layoutManager
        this.visibleThreshold = visibleThreshold
    }

    /**
     * @param layoutManager
     * @param visibleThreshold
     * @param footerAdapter    the ItemAdapter used to host footer items
     */
    constructor(layoutManager: RecyclerView.LayoutManager, visibleThreshold: Int, footerAdapter: ItemAdapter<*>) {
        this.layoutManager = layoutManager
        this.visibleThreshold = visibleThreshold
        this.footerAdapter = footerAdapter
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
        if (layoutManager.canScrollVertically() != isOrientationHelperVertical || orientationHelper == null) {
            isOrientationHelperVertical = layoutManager.canScrollVertically()
            orientationHelper = if (isOrientationHelperVertical)
                OrientationHelper.createVerticalHelper(layoutManager)
            else
                OrientationHelper.createHorizontalHelper(layoutManager)
        }

        val mOrientationHelper = this.orientationHelper ?: return null

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
            if (!::layoutManager.isInitialized) {
                layoutManager = recyclerView.layoutManager
                        ?: throw RuntimeException("A LayoutManager is required")
            }

            val footerItemCount = footerAdapter?.adapterItemCount ?: 0

            if (visibleThreshold == RecyclerView.NO_POSITION) {
                visibleThreshold = findLastVisibleItemPosition(recyclerView) - findFirstVisibleItemPosition(recyclerView) - footerItemCount
            }

            visibleItemCount = recyclerView.childCount - footerItemCount
            totalItemCount = layoutManager.itemCount - footerItemCount
            firstVisibleItem = findFirstVisibleItemPosition(recyclerView)

            if (isLoading) {
                if (totalItemCount > previousTotal) {
                    isLoading = false
                    previousTotal = totalItemCount
                }
            }
            if (!isLoading && totalItemCount - visibleItemCount <= firstVisibleItem + visibleThreshold) {

                currentPage++

                onLoadMore(currentPage)

                isLoading = true
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
        previousTotal = 0
        isLoading = true
        currentPage = page
        onLoadMore(currentPage)
    }

    abstract fun onLoadMore(currentPage: Int)
}