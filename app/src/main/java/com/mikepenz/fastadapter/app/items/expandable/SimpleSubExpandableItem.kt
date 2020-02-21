package com.mikepenz.fastadapter.app.items.expandable

import android.graphics.Color
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.fastadapter.ClickListener
import com.mikepenz.fastadapter.IAdapter
import com.mikepenz.fastadapter.IClickable
import com.mikepenz.fastadapter.ISubItem
import com.mikepenz.fastadapter.app.R
import com.mikepenz.fastadapter.expandable.items.AbstractExpandableItem
import com.mikepenz.fastadapter.ui.utils.FastAdapterUIUtils
import com.mikepenz.materialdrawer.holder.StringHolder

/**
 * Created by mikepenz on 28.12.15.
 */
open class SimpleSubExpandableItem : AbstractExpandableItem<SimpleSubExpandableItem.ViewHolder>(), IClickable<SimpleSubExpandableItem>, ISubItem<SimpleSubExpandableItem.ViewHolder> {

    var header: String? = null
    var name: StringHolder? = null
    var description: StringHolder? = null

    private var mOnClickListener: ClickListener<SimpleSubExpandableItem>? = null

    //we define a clickListener in here so we can directly animate
    /**
     * we overwrite the item specific click listener so we can automatically animate within the item
     *
     * @return
     */
    @Suppress("SetterBackingFieldAssignment")
    override var onItemClickListener: ClickListener<SimpleSubExpandableItem>? = { v: View?, adapter: IAdapter<SimpleSubExpandableItem>, item: SimpleSubExpandableItem, position: Int ->
        if (item.subItems.isNotEmpty()) {
            v?.findViewById<View>(R.id.material_drawer_icon)?.let {
                if (!item.isExpanded) {
                    ViewCompat.animate(it).rotation(180f).start()
                } else {
                    ViewCompat.animate(it).rotation(0f).start()
                }
            }
        }
        mOnClickListener?.invoke(v, adapter, item, position) ?: true
    }
        set(onClickListener) {
            this.mOnClickListener = onClickListener // on purpose
        }

    override var onPreItemClickListener: ClickListener<SimpleSubExpandableItem>?
        get() = null
        set(_) {}

    //this might not be true for your application
    override var isSelectable: Boolean
        get() = subItems.isEmpty()
        set(value) {
            super.isSelectable = value
        }

    /**
     * defines the type defining this item. must be unique. preferably an id
     *
     * @return the type
     */
    override val type: Int
        get() = R.id.fastadapter_expandable_item_id

    /**
     * defines the layout which will be used for this item in the list
     *
     * @return the layout for this item
     */
    override val layoutRes: Int
        get() = R.layout.expandable_item

    fun withHeader(header: String): SimpleSubExpandableItem {
        this.header = header
        return this
    }

    fun withName(Name: String): SimpleSubExpandableItem {
        this.name = StringHolder(Name)
        return this
    }

    fun withName(@StringRes NameRes: Int): SimpleSubExpandableItem {
        this.name = StringHolder(NameRes)
        return this
    }

    fun withDescription(description: String): SimpleSubExpandableItem {
        this.description = StringHolder(description)
        return this
    }

    fun withDescription(@StringRes descriptionRes: Int): SimpleSubExpandableItem {
        this.description = StringHolder(descriptionRes)
        return this
    }

    /**
     * binds the data of this item onto the viewHolder
     *
     * @param holder the viewHolder of this item
     */
    override fun bindView(holder: ViewHolder, payloads: List<Any>) {
        super.bindView(holder, payloads)

        //get the context
        val ctx = holder.itemView.context

        //set the background for the item
        holder.view.clearAnimation()
        ViewCompat.setBackground(holder.view, FastAdapterUIUtils.getSelectableBackground(ctx, Color.RED, true))
        //set the text for the name
        StringHolder.applyTo(name, holder.name)
        //set the text for the description or hide
        StringHolder.applyToOrHide(description, holder.description)

        if (subItems.isEmpty()) {
            holder.icon.visibility = View.GONE
        } else {
            holder.icon.visibility = View.VISIBLE
        }

        if (isExpanded) {
            holder.icon.rotation = 0f
        } else {
            holder.icon.rotation = 180f
        }
    }

    override fun unbindView(holder: ViewHolder) {
        super.unbindView(holder)
        holder.name.text = null
        holder.description.text = null
        //make sure all animations are stopped
        holder.icon.clearAnimation()
    }

    override fun getViewHolder(v: View): ViewHolder {
        return ViewHolder(v)
    }

    /**
     * our ViewHolder
     */
    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        var name: TextView = view.findViewById(R.id.material_drawer_name)
        var description: TextView = view.findViewById(R.id.material_drawer_description)
        var icon: ImageView = view.findViewById(R.id.material_drawer_icon)
    }
}
