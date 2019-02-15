package com.mikepenz.fastadapter.listeners

import android.support.v7.widget.RecyclerView
import android.view.MotionEvent
import android.view.View
import com.mikepenz.fastadapter.IAdapter
import com.mikepenz.fastadapter.IItem

interface OnTouchListener<Item : IItem<out RecyclerView.ViewHolder>> {
    /**
     * the onTouch event of a specific item inside the RecyclerView
     *
     * @param v        the view we clicked
     * @param event    the touch event
     * @param adapter  the adapter which is responsible for the given item
     * @param item     the IItem which was clicked
     * @param position the global position
     * @return return true if the event was consumed, otherwise false
     */
    fun onTouch(
            v: View,
            event: MotionEvent,
            adapter: IAdapter<Item>,
            item: Item,
            position: Int
    ): Boolean
}
