package com.mikepenz.fastadapter.listeners

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.fastadapter.GenericItem
import com.mikepenz.fastadapter.IFastAdapter
import com.mikepenz.fastadapter.IHookable
import com.mikepenz.fastadapter.utils.bind

/**
 * default implementation of the OnCreateViewHolderListener
 */
open class OnCreateViewHolderListenerImpl<Item : GenericItem> : OnCreateViewHolderListener<Item> {
    /**
     * is called inside the onCreateViewHolder method and creates the viewHolder based on the provided viewTyp
     *
     * @param parent   the parent which will host the View
     * @param viewType the type of the ViewHolder we want to create
     * @return the generated ViewHolder based on the given viewType
     */
    override fun onPreCreateViewHolder(fastAdapter: IFastAdapter<Item>, parent: ViewGroup, viewType: Int, typeInstance: Item): RecyclerView.ViewHolder {
        return typeInstance.getViewHolder(parent)
    }

    /**
     * is called after the viewHolder was created and the default listeners were added
     *
     * @param viewHolder the created viewHolder after all listeners were set
     * @return the viewHolder given as param
     */
    override fun onPostCreateViewHolder(fastAdapter: IFastAdapter<Item>, viewHolder: RecyclerView.ViewHolder, typeInstance: Item): RecyclerView.ViewHolder {
        fastAdapter.eventHooks.bind(viewHolder)
        //check if the item implements hookable and contains event hooks
        (typeInstance as? IHookable<*>)?.eventHooks?.bind(viewHolder)
        return viewHolder
    }
}
