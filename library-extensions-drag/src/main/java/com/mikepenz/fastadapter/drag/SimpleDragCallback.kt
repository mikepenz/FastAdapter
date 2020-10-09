package com.mikepenz.fastadapter.drag

import androidx.annotation.IntDef
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.IItem
import com.mikepenz.fastadapter.adapters.ItemAdapter

/**
 * Based on the [sample](https://github.com/AleBarreto/DragRecyclerView)
 */
open class SimpleDragCallback : ItemTouchHelper.SimpleCallback {

    //our callback
    private var callbackItemTouch: ItemTouchCallback? = null // interface

    /** enable to drag around via long press */
    var isDragEnabled = true

    private var from = RecyclerView.NO_POSITION
    private var to = RecyclerView.NO_POSITION

    private var directions = UP_DOWN

    /** enable notification for all drops, even if location did not change */
    var notifyAllDrops = false // Default behaviour of v5.0.1-

    @Suppress("EmptyDefaultConstructor")
    @IntDef(ALL, UP_DOWN, LEFT_RIGHT)
    @Retention(AnnotationRetention.SOURCE)
    annotation class Directions {
        // empty on purpose
    }

    constructor(@Directions directions: Int = UP_DOWN) : super(directions, 0) {
        this.directions = directions
    }

    constructor(@Directions directions: Int, itemTouchCallback: ItemTouchCallback) : super(directions, 0) {
        this.directions = directions
        this.callbackItemTouch = itemTouchCallback
    }

    constructor(itemTouchCallback: ItemTouchCallback) : super(UP_DOWN, 0) {
        this.callbackItemTouch = itemTouchCallback
    }

    fun setIsDragEnabled(mIsDragEnabled: Boolean) {
        this.isDragEnabled = mIsDragEnabled
    }

    override fun isLongPressDragEnabled(): Boolean {
        return isDragEnabled
    }

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        // remember the from/to positions
        val item = FastAdapter.getHolderAdapterItem<IItem<*>>(viewHolder)
        if (item is IDraggable) {
            if ((item as IDraggable).isDraggable) {
                if (from == RecyclerView.NO_POSITION) {
                    from = viewHolder.adapterPosition
                }
                to = target.adapterPosition
            }
        }
        if (callbackItemTouch == null) {
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
        return callbackItemTouch?.itemTouchOnMove(viewHolder.adapterPosition, target.adapterPosition)
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
            directions
        }
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        // swiped disabled
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)
        if (notifyAllDrops || (from != RecyclerView.NO_POSITION && to != RecyclerView.NO_POSITION)) {
            // If 'to' is not set, then we can assume the item hasn't moved at all
            if (from != RecyclerView.NO_POSITION && to == RecyclerView.NO_POSITION) to = from
            callbackItemTouch?.itemTouchDropped(from, to)
        }
        // reset the from/to positions
        to = RecyclerView.NO_POSITION
        from = to
    }

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        super.onSelectedChanged(viewHolder, actionState)
        if (ItemTouchHelper.ACTION_STATE_DRAG == actionState && viewHolder != null) {
            from = viewHolder.adapterPosition
            callbackItemTouch?.itemTouchStartDrag(viewHolder)
        }
    }

    companion object {

        const val ALL = ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        const val UP_DOWN = ItemTouchHelper.UP or ItemTouchHelper.DOWN
        const val LEFT_RIGHT = ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
    }
}