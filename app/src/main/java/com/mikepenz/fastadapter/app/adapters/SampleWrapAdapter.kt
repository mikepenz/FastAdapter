package com.mikepenz.fastadapter.app.adapters

import androidx.recyclerview.widget.RecyclerView

import com.mikepenz.fastadapter.IItem
import com.mikepenz.fastadapter.adapters.AbstractWrapAdapter

/**
 * Created by mikepenz on 03.03.16.
 */
class SampleWrapAdapter<Item : IItem<VH>, VH : RecyclerView.ViewHolder>(list: List<Item>) : AbstractWrapAdapter<Item, VH>(list) {

    override fun shouldInsertItemAtPosition(position: Int): Boolean {
        return position == 10
    }

    override fun itemInsertedBeforeCount(position: Int): Int {
        return if (position > 10) {
            1
        } else 0
    }
}
