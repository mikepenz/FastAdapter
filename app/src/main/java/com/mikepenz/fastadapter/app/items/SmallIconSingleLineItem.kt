package com.mikepenz.fastadapter.app.items

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.fastadapter.app.R
import com.mikepenz.fastadapter.items.AbstractItem
import com.mikepenz.fastadapter.ui.utils.ImageHolder
import com.mikepenz.fastadapter.ui.utils.StringHolder

class SmallIconSingleLineItem(icon: Drawable, name: String) : AbstractItem<SmallIconSingleLineItem.ViewHolder>() {
    val icon: ImageHolder = ImageHolder(icon)
    val name: StringHolder = StringHolder(name)

    override val type: Int
        get() = R.id.fastadapter_small_icon_single_line_item

    override val layoutRes: Int
        get() = R.layout.small_single_line_item

    override fun bindView(holder: ViewHolder, payloads: List<Any>) {
        super.bindView(holder, payloads)
        ImageHolder.applyToOrSetGone(icon, holder.icon)
        name.applyTo(holder.name)
    }

    override fun unbindView(holder: ViewHolder) {
        holder.icon.setImageDrawable(null)
        holder.name.text = null
    }

    override fun getViewHolder(v: View): ViewHolder = ViewHolder(v)

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var icon: ImageView = view.findViewById(R.id.icon)
        var name: TextView = view.findViewById(R.id.name)
    }
}
