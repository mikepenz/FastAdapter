package com.mikepenz.fastadapter.ui.items

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.fastadapter.items.AbstractItem
import com.mikepenz.fastadapter.ui.R
import com.mikepenz.fastadapter.ui.utils.FastAdapterUIUtils
import com.mikepenz.fastadapter.ui.utils.ImageHolder
import com.mikepenz.fastadapter.ui.utils.StringHolder

/**
 * Created by fabianterhorst on 30.03.16.
 */
class TwoLineItem : AbstractItem<TwoLineItem.ViewHolder>() {

    var name: StringHolder? = null
        private set

    var description: StringHolder? = null
        private set

    var avatar: ImageHolder? = null
        private set

    var icon: ImageHolder? = null
        private set

    override val type: Int
        get() = R.id.two_line_item_id

    override val layoutRes: Int
        get() = R.layout.two_line_item

    fun withName(name: String): TwoLineItem {
        this.name = StringHolder(name)
        return this
    }

    fun withDescription(description: String): TwoLineItem {
        this.description = StringHolder(description)
        return this
    }

    fun withAvatar(avatar: Drawable): TwoLineItem {
        this.avatar = ImageHolder(avatar)
        return this
    }

    fun withAvatar(@DrawableRes avatarRes: Int): TwoLineItem {
        this.avatar = ImageHolder(avatarRes)
        return this
    }

    fun withAvatar(uri: Uri): TwoLineItem {
        this.avatar = ImageHolder(uri)
        return this
    }

    fun withAvatar(bitmap: Bitmap): TwoLineItem {
        this.avatar = ImageHolder(bitmap)
        return this
    }

    fun withAvatar(url: String): TwoLineItem {
        this.avatar = ImageHolder(Uri.parse(url))
        return this
    }

    fun withIcon(icon: Drawable): TwoLineItem {
        this.icon = ImageHolder(icon)
        return this
    }

    fun withIcon(@DrawableRes iconRes: Int): TwoLineItem {
        this.icon = ImageHolder(iconRes)
        return this
    }

    fun withIcon(uri: Uri): TwoLineItem {
        this.icon = ImageHolder(uri)
        return this
    }

    fun withIcon(bitmap: Bitmap): TwoLineItem {
        this.icon = ImageHolder(bitmap)
        return this
    }

    fun withIcon(url: String): TwoLineItem {
        this.icon = ImageHolder(Uri.parse(url))
        return this
    }

    override fun bindView(holder: ViewHolder, payloads: List<Any>) {
        super.bindView(holder, payloads)
        if (isEnabled) {
            holder.itemView.setBackgroundResource(FastAdapterUIUtils.getSelectableBackground(holder.itemView.context))
        }
        name?.applyTo(holder.name)
        description?.applyTo(holder.description)
        ImageHolder.applyToOrSetGone(avatar, holder.avatar)
        ImageHolder.applyToOrSetGone(icon, holder.icon)
    }

    override fun unbindView(holder: ViewHolder) {
        holder.name.text = null
        holder.description.text = null
        holder.avatar.setImageDrawable(null)
        holder.avatar.visibility = View.VISIBLE
        holder.icon.setImageDrawable(null)
        holder.icon.visibility = View.VISIBLE
    }

    override fun getViewHolder(v: View): ViewHolder = ViewHolder(v)

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var name: TextView = view.findViewById<View>(R.id.name) as TextView
        var description: TextView = view.findViewById<View>(R.id.description) as TextView
        var avatar: ImageView = view.findViewById<View>(R.id.avatar) as ImageView
        var icon: ImageView = view.findViewById<View>(R.id.icon) as ImageView
    }
}
