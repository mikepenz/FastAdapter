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
class SingleLineItem : AbstractItem<SingleLineItem.ViewHolder>() {

    var name: StringHolder? = null
        private set

    var avatar: ImageHolder? = null
        private set

    var icon: ImageHolder? = null
        private set

    override val type: Int
        get() = R.id.single_line_item_id

    override val layoutRes: Int
        get() = R.layout.single_line_item

    fun withName(name: String): SingleLineItem {
        this.name = StringHolder(name)
        return this
    }

    fun withAvatar(avatar: Drawable): SingleLineItem {
        this.avatar = ImageHolder(avatar)
        return this
    }

    fun withAvatar(@DrawableRes avatarRes: Int): SingleLineItem {
        this.avatar = ImageHolder(avatarRes)
        return this
    }

    fun withAvatar(uri: Uri): SingleLineItem {
        this.avatar = ImageHolder(uri)
        return this
    }

    fun withAvatar(bitmap: Bitmap): SingleLineItem {
        this.avatar = ImageHolder(bitmap)
        return this
    }

    fun withAvatar(url: String): SingleLineItem {
        this.avatar = ImageHolder(Uri.parse(url))
        return this
    }

    fun withIcon(icon: Drawable): SingleLineItem {
        this.icon = ImageHolder(icon)
        return this
    }

    fun withIcon(@DrawableRes iconRes: Int): SingleLineItem {
        this.icon = ImageHolder(iconRes)
        return this
    }

    fun withIcon(uri: Uri): SingleLineItem {
        this.icon = ImageHolder(uri)
        return this
    }

    fun withIcon(bitmap: Bitmap): SingleLineItem {
        this.icon = ImageHolder(bitmap)
        return this
    }

    fun withIcon(url: String): SingleLineItem {
        this.icon = ImageHolder(Uri.parse(url))
        return this
    }

    override fun bindView(holder: ViewHolder, payloads: List<Any>) {
        super.bindView(holder, payloads)
        if (isEnabled) {
            holder.itemView.setBackgroundResource(FastAdapterUIUtils.getSelectableBackground(holder.itemView.context))
        }
        name?.applyTo(holder.name)
        ImageHolder.applyToOrSetGone(avatar, holder.avatar)
        ImageHolder.applyToOrSetGone(icon, holder.icon)
    }

    override fun unbindView(holder: ViewHolder) {
        holder.name.text = null
        holder.avatar.setImageDrawable(null)
        holder.avatar.visibility = View.VISIBLE
        holder.icon.setImageDrawable(null)
        holder.icon.visibility = View.VISIBLE
    }

    override fun getViewHolder(v: View): ViewHolder = ViewHolder(v)

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var name: TextView = view.findViewById<View>(R.id.name) as TextView
        var avatar: ImageView = view.findViewById<View>(R.id.avatar) as ImageView
        var icon: ImageView = view.findViewById<View>(R.id.icon) as ImageView
    }
}
