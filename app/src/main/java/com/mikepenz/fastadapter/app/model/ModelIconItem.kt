package com.mikepenz.fastadapter.app.model

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.aboutlibraries.util.getThemeColor
import com.mikepenz.fastadapter.app.R
import com.mikepenz.fastadapter.items.ModelAbstractItem
import com.mikepenz.iconics.IconicsDrawable
import com.mikepenz.iconics.utils.colorInt
import com.mikepenz.iconics.view.IconicsImageView

/**
 * Created by mikepenz on 28.12.15.
 */
open class ModelIconItem(icon: IconModel) : ModelAbstractItem<IconModel, ModelIconItem.ViewHolder>(icon) {

    /**
     * defines the type defining this item. must be unique. preferably an id
     *
     * @return the type
     */
    override val type: Int
        get() = R.id.fastadapter_model_icon_item_id

    /**
     * defines the layout which will be used for this item in the list
     *
     * @return the layout for this item
     */
    override val layoutRes: Int
        get() = R.layout.icon_item

    /**
     * binds the data of this item onto the viewHolder
     *
     * @param holder the viewHolder of this item
     */
    override fun bindView(holder: ViewHolder, payloads: List<Any>) {
        super.bindView(holder, payloads)

        //define our data for the view
        holder.image.icon = IconicsDrawable(holder.image.context, model.icon).apply {
            colorInt = holder.view.context.getThemeColor(R.attr.colorOnSurface)
        }
        holder.name.text = model.icon.name
    }

    override fun unbindView(holder: ViewHolder) {
        super.unbindView(holder)
        holder.image.setImageDrawable(null)
        holder.name.text = null
    }

    override fun getViewHolder(v: View): ViewHolder {
        return ViewHolder(v)
    }

    /**
     * our ViewHolder
     */
    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        var name: TextView = view.findViewById(R.id.name)
        var image: IconicsImageView = view.findViewById(R.id.icon)
    }
}
