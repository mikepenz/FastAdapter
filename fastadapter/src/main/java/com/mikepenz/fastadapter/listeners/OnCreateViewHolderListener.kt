package com.mikepenz.fastadapter.listeners

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.GenericItem
import com.mikepenz.fastadapter.IItemVHFactory

interface OnCreateViewHolderListener<Item : GenericItem> {
    /**
     * Is called inside the onCreateViewHolder method and creates the viewHolder based on the provided viewTyp
     *
     * @param fastAdapter the fastAdapter which handles the creation of this viewHolder
     * @param parent   the parent which will host the View
     * @param viewType the type of the ViewHolder we want to create
     * @return the generated ViewHolder based on the given viewType
     */
    fun onPreCreateViewHolder(fastAdapter: FastAdapter<Item>, parent: ViewGroup, viewType: Int, itemVHFactory: IItemVHFactory<*>): RecyclerView.ViewHolder

    /**
     * Is called after the viewHolder was created and the default listeners were added
     *
     * @param fastAdapter the fastAdapter which handles the creation of this viewHolder
     * @param viewHolder the created viewHolder after all listeners were set
     * @return the viewHolder given as param
     */
    fun onPostCreateViewHolder(fastAdapter: FastAdapter<Item>, viewHolder: RecyclerView.ViewHolder, itemVHFactory: IItemVHFactory<*>): RecyclerView.ViewHolder
}
