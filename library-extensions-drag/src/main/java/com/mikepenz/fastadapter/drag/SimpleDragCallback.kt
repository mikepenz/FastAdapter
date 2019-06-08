package com.mikepenz.fastadapter.drag

import androidx.annotation.IntDef
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.IItem
import com.mikepenz.fastadapter.adapters.ItemAdapter

/**
 * based on the sample from
 * https://github.com/AleBarreto/DragRecyclerView
 */
open class SimpleDragCallback : ItemTouchHelper.SimpleCallback {

    //our callback
    private var mCallbackItemTouch: ItemTouchCallback? = null // interface
    private var mIsDragEnabled = true

    private var mFrom = RecyclerView.NO_POSITION
    private var mTo = RecyclerView.NO_POSITION

    private var mDirections = UP_DOWN

    @IntDef(ALL, UP_DOWN, LEFT_RIGHT)
    @Retention(AnnotationRetention.SOURCE)
    annotation class Directions()

    constructor() : super(UP_DOWN, 0) {}

    constructor(@Directions directions: Int) : super(directions, 0) {
        this.mDirections = directions
    }

    constructor(@Directions directions: Int, itemTouchCallback: ItemTouchCallback) : super(directions, 0) {
        this.mDirections = directions
        this.mCallbackItemTouch = itemTouchCallback
    }

    constructor(itemTouchCallback: ItemTouchCallback) : super(UP_DOWN, 0) {
        this.mCallbackItemTouch = itemTouchCallback
    }

    fun setIsDragEnabled(mIsDragEnabled: Boolean) {
        this.mIsDragEnabled = mIsDragEnabled
    }

    override fun isLongPressDragEnabled(): Boolean {
        return mIsDragEnabled
    }

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        // remember the from/to positions
        val item = FastAdapter.getHolderAdapterItem<IItem<*>>(viewHolder)
        if (item is IDraggable) {
            if ((item as IDraggable).isDraggable) {
                if (mFrom == RecyclerView.NO_POSITION) {
                    mFrom = viewHolder.adapterPosition
                }
                mTo = target.adapterPosition
            }
        }
        if (mCallbackItemTouch == null) {
            val adapter = recyclerView.adapter
            var itemAdapter: ItemAdapter<*>? = null
            if (adapter is FastAdapter<*>) {
                //TODO this logic only works for the adapter at position 0 --> in the past it also only worked for the outer adapter
                itemAdapter = adapter.adapter(0) as ItemAdapter<*>?
            }
            if (itemAdapter != null) {
                itemAdapter.fastAdapter?.let {
                    itemAdapter.move(it.getHolderAdapterPosition(viewHolder), it.getHolderAdapterPosition(target))
                }
                return true
            }
            throw RuntimeException("SimpleDragCallback without an callback is only allowed when using the ItemAdapter or the FastItemAdapter")
        }
        return mCallbackItemTouch?.itemTouchOnMove(viewHolder.adapterPosition, target.adapterPosition)
                ?: false // information to the interface
    }

    override fun getDragDirs(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        val item = FastAdapter.getHolderAdapterItem<IItem<*>>(viewHolder)
        return if (item is IDraggable) {
            if ((item as IDraggable).isDraggable) {
                super.getDragDirs(recyclerView, viewHolder)
            } else {
                0
            }
        } else {
            mDirections
        }
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        // swiped disabled
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)
        if (mFrom != RecyclerView.NO_POSITION && mTo != RecyclerView.NO_POSITION) {
            mCallbackItemTouch?.itemTouchDropped(mFrom, mTo)
        }
        // reset the from/to positions
        mTo = RecyclerView.NO_POSITION
        mFrom = mTo
    }

    companion object {

        const val ALL = ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        const val UP_DOWN = ItemTouchHelper.UP or ItemTouchHelper.DOWN
        const val LEFT_RIGHT = ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
    }
}