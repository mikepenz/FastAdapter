package com.mikepenz.fastadapter.app.helpers

import android.graphics.Canvas
import android.graphics.Rect
import android.util.SparseArray
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mopub.nativeads.MoPubRecyclerAdapter
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter
import com.timehop.stickyheadersrecyclerview.caching.HeaderProvider
import com.timehop.stickyheadersrecyclerview.calculation.DimensionCalculator
import com.timehop.stickyheadersrecyclerview.rendering.HeaderRenderer
import com.timehop.stickyheadersrecyclerview.util.LinearLayoutOrientationProvider
import com.timehop.stickyheadersrecyclerview.util.OrientationProvider

/**
 * Created by Gagan on 5/3/2017.
 */
class CustomStickyRecyclerHeadersDecoration private constructor(private val mAdapter: StickyRecyclerHeadersAdapter<*>, private val mRenderer: HeaderRenderer,
                                                                private val mOrientationProvider: OrientationProvider, private val mDimensionCalculator: DimensionCalculator, private val mHeaderProvider: HeaderProvider,
                                                                private val mHeaderPositionCalculator: HeaderPositionCalculator) : RecyclerView.ItemDecoration() {
    private val mHeaderRects = SparseArray<Rect>()

    /**
     * The following field is used as a buffer for internal calculations. Its sole purpose is to avoid
     * allocating new Rect every time we need one.
     */
    private val mTempRect = Rect()

    // TODO Consider passing in orientation to simplify orientation accounting within calculation
    constructor(adapter: StickyRecyclerHeadersAdapter<*>, moPubRecyclerAdapter: MoPubRecyclerAdapter) : this(adapter, moPubRecyclerAdapter, LinearLayoutOrientationProvider(), DimensionCalculator())

    private constructor(adapter: StickyRecyclerHeadersAdapter<*>, moPubRecyclerAdapter: MoPubRecyclerAdapter, orientationProvider: OrientationProvider,
                        dimensionCalculator: DimensionCalculator, headerRenderer: HeaderRenderer = HeaderRenderer(orientationProvider), headerProvider: HeaderProvider = CustomHeaderViewCache(adapter, moPubRecyclerAdapter, orientationProvider)) : this(adapter, headerRenderer, orientationProvider, dimensionCalculator, headerProvider,
            HeaderPositionCalculator(adapter, moPubRecyclerAdapter, headerProvider, orientationProvider,
                    dimensionCalculator))

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        val itemPosition = parent.getChildAdapterPosition(view)
        if (itemPosition == RecyclerView.NO_POSITION) {
            return
        }
        if (mHeaderPositionCalculator.hasNewHeader(itemPosition, mOrientationProvider.isReverseLayout(parent))) {
            val header = getHeaderView(parent, itemPosition)
            setItemOffsetsForHeader(outRect, header, mOrientationProvider.getOrientation(parent))
        }
    }

    /**
     * Sets the offsets for the first item in a section to make room for the header view
     *
     * @param itemOffsets rectangle to define offsets for the item
     * @param header      view used to calculate offset for the item
     * @param orientation used to calculate offset for the item
     */
    private fun setItemOffsetsForHeader(itemOffsets: Rect, header: View, orientation: Int) {
        mDimensionCalculator.initMargins(mTempRect, header)
        if (orientation == LinearLayoutManager.VERTICAL) {
            itemOffsets.top = header.height + mTempRect.top + mTempRect.bottom
        } else {
            itemOffsets.left = header.width + mTempRect.left + mTempRect.right
        }
    }

    override fun onDrawOver(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(canvas, parent, state)

        val childCount = parent.childCount
        if (childCount <= 0 || mAdapter.itemCount <= 0) {
            return
        }

        for (i in 0 until childCount) {
            val itemView = parent.getChildAt(i)
            val position = parent.getChildAdapterPosition(itemView)
            if (position == RecyclerView.NO_POSITION) {
                continue
            }

            val hasStickyHeader = mHeaderPositionCalculator.hasStickyHeader(itemView, mOrientationProvider.getOrientation(parent), position)
            if (hasStickyHeader || mHeaderPositionCalculator.hasNewHeader(position, mOrientationProvider.isReverseLayout(parent))) {
                val header = mHeaderProvider.getHeader(parent, position)
                //re-use existing Rect, if any.
                var headerOffset: Rect? = mHeaderRects.get(position)
                if (headerOffset == null) {
                    headerOffset = Rect()
                    mHeaderRects.put(position, headerOffset)
                }
                mHeaderPositionCalculator.initHeaderBounds(headerOffset, parent, header, itemView, hasStickyHeader)
                mRenderer.drawHeader(parent, canvas, header, headerOffset)
            }
        }
    }

    /**
     * Gets the position of the header under the specified (x, y) coordinates.
     *
     * @param x x-coordinate
     * @param y y-coordinate
     * @return position of header, or [androidx.recyclerview.widget.RecyclerView.NO_POSITION] (-1) if not found
     */
    fun findHeaderPositionUnder(x: Int, y: Int): Int {
        for (i in 0 until mHeaderRects.size()) {
            val rect = mHeaderRects.get(mHeaderRects.keyAt(i))
            if (rect.contains(x, y)) {
                return mHeaderRects.keyAt(i)
            }
        }
        return RecyclerView.NO_POSITION
    }

    /**
     * Gets the header view for the associated position.  If it doesn't exist yet, it will be
     * created, measured, and laid out.
     *
     * @param parent
     * @param position
     * @return Header view
     */
    fun getHeaderView(parent: RecyclerView, position: Int): View {
        return mHeaderProvider.getHeader(parent, position)
    }

    /**
     * Invalidates cached headers.  This does not invalidate the recyclerview, you should do that manually after
     * calling this method.
     */
    fun invalidateHeaders() {
        mHeaderProvider.invalidate()
    }
}
