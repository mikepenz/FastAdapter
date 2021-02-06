package com.mikepenz.fastadapter.listeners

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.fastadapter.GenericItem

interface EventHook<Item : GenericItem> {

    /**
     * Return the view for this hook that the listener should be bound to
     *
     * @return null, if the provided ViewHolder should not be bound to the event hook; return the view responsible for the event otherwise
     */
    fun onBind(viewHolder: RecyclerView.ViewHolder): View? = null

    /**
     * Return the views for this hook that the listener should be bound to
     *
     * @return null, if the provided ViewHolder should not be bound to the event hook; return the views responsible for the event otherwise
     */
    fun onBindMany(viewHolder: RecyclerView.ViewHolder): List<View>? = null
}
