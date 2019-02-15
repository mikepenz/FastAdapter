package com.mikepenz.fastadapter.app.items.expandable

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.mikepenz.fastadapter.IExpandable
import com.mikepenz.fastadapter.app.R
import com.mikepenz.fastadapter.expandable.items.AbstractExpandableItem

open class SimpleSubItem : AbstractExpandableItem<SimpleSubItem.ViewHolder>(), IExpandable<SimpleSubItem.ViewHolder> {

    var header: String? = null
    var name: String? = null
    var description: String? = null

    /**
     * defines the type defining this item. must be unique. preferably an id
     *
     * @return the type
     */
    override val type: Int
        get() = R.id.fastadapter_sub_item_id

    /**
     * defines the layout which will be used for this item in the list
     *
     * @return the layout for this item
     */
    override val layoutRes: Int
        get() = R.layout.sample_item

    fun withHeader(header: String): SimpleSubItem {
        this.header = header
        return this
    }

    fun withName(name: String): SimpleSubItem {
        this.name = name
        return this
    }

    fun withDescription(description: String): SimpleSubItem {
        this.description = description
        return this
    }

    /**
     * binds the data of this item onto the viewHolder
     *
     * @param holder the viewHolder of this item
     */
    override fun bindView(holder: ViewHolder, payloads: MutableList<Any>) {
        super.bindView(holder, payloads)

        //get the context
        val ctx = holder.itemView.context

        //set the background for the item
        holder.view.clearAnimation()
        //ViewCompat.setBackground(holder.view, FastAdapterUIUtils.getSelectableBackground(ctx, Color.RED, true))
        //set the text for the name
        holder.name.text = name
        //set the text for the description or hide
        holder.description.text = description
    }

    override fun unbindView(holder: ViewHolder) {
        super.unbindView(holder)
        holder.name.text = null
        holder.description.text = null
    }

    override fun getViewHolder(v: View): ViewHolder {
        return ViewHolder(v)
    }

    /**
     * our ViewHolder
     */
    class ViewHolder(var view: View) : RecyclerView.ViewHolder(view) {
        var name: TextView = view.findViewById(R.id.material_drawer_name)
        var description: TextView = view.findViewById(R.id.material_drawer_description)
    }
}
