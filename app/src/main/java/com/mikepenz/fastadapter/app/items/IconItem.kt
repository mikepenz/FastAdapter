package com.mikepenz.fastadapter.app.items

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.aboutlibraries.util.getThemeColor
import com.mikepenz.fastadapter.IExpandable
import com.mikepenz.fastadapter.IParentItem
import com.mikepenz.fastadapter.ISubItem
import com.mikepenz.fastadapter.app.R
import com.mikepenz.fastadapter.items.AbstractItem
import com.mikepenz.iconics.IconicsDrawable
import com.mikepenz.iconics.typeface.IIcon
import com.mikepenz.iconics.utils.colorInt
import com.mikepenz.iconics.view.IconicsImageView

/**
 * Created by mikepenz on 28.12.15.
 */
class IconItem : AbstractItem<IconItem.ViewHolder>(), IExpandable<IconItem.ViewHolder> {

    var mIcon: IIcon? = null
    override var parent: IParentItem<*>? = null
    override var isExpanded: Boolean = false

    override var subItems: MutableList<ISubItem<*>>
        get() = mutableListOf()
        set(_) {
        }

    override val isAutoExpanding: Boolean
        get() = true

    /**
     * defines the type defining this item. must be unique. preferably an id
     *
     * @return the type
     */
    override val type: Int
        get() = R.id.fastadapter_icon_item_id

    /**
     * defines the layout which will be used for this item in the list
     *
     * @return the layout for this item
     */
    override val layoutRes: Int
        get() = R.layout.icon_item

    /**
     * setter method for the Icon
     *
     * @param icon the icon
     * @return this
     */
    fun withIcon(icon: IIcon): IconItem {
        this.mIcon = icon
        return this
    }

    /**
     * binds the data of this item onto the viewHolder
     *
     * @param holder the viewHolder of this item
     */
    override fun bindView(holder: ViewHolder, payloads: List<Any>) {
        super.bindView(holder, payloads)

        //define our data for the view
        mIcon?.let {
            holder.image.icon = IconicsDrawable(holder.image.context, it).apply {
                colorInt = holder.view.context.getThemeColor(R.attr.colorOnSurface)
            }
        }
        holder.name.text = mIcon?.name
    }

    override fun unbindView(holder: ViewHolder) {
        super.unbindView(holder)
        holder.image.setImageDrawable(null)
    }

    override fun getViewHolder(v: View): ViewHolder {
        return ViewHolder(v)
    }

    /**
     * our ViewHolder
     */
    class ViewHolder(var view: View) : RecyclerView.ViewHolder(view) {
        var name: TextView = view.findViewById(R.id.name)
        var image: IconicsImageView = view.findViewById(R.id.icon)
    }
}
