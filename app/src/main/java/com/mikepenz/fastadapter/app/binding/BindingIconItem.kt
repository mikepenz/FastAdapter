package com.mikepenz.fastadapter.app.binding

import android.view.LayoutInflater
import android.view.ViewGroup
import com.mikepenz.aboutlibraries.util.getThemeColor
import com.mikepenz.fastadapter.IExpandable
import com.mikepenz.fastadapter.IParentItem
import com.mikepenz.fastadapter.ISubItem
import com.mikepenz.fastadapter.app.R
import com.mikepenz.fastadapter.app.databinding.IconItemBinding
import com.mikepenz.fastadapter.binding.AbstractBindingItem
import com.mikepenz.fastadapter.binding.BindingViewHolder
import com.mikepenz.iconics.IconicsDrawable
import com.mikepenz.iconics.typeface.IIcon
import com.mikepenz.iconics.utils.colorInt

/**
 * Created by mikepenz on 28.12.15.
 */
class BindingIconItem : AbstractBindingItem<IconItemBinding>(), IExpandable<BindingViewHolder<IconItemBinding>> {
    var icon: IIcon? = null

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
     * setter method for the Icon
     *
     * @param icon the icon
     * @return this
     */
    fun withIcon(icon: IIcon): BindingIconItem {
        this.icon = icon
        return this
    }

    /**
     * binds the data of this item onto the viewHolder
     */
    override fun bindView(binding: IconItemBinding, payloads: List<Any>) {
        //define our data for the view
        icon?.let {
            binding.icon.icon = IconicsDrawable(binding.icon.context, it).apply {
                colorInt = binding.root.context.getThemeColor(R.attr.colorOnSurface)
            }
        }
        binding.name.text = icon?.name
    }

    override fun unbindView(binding: IconItemBinding) {
        binding.icon.setImageDrawable(null)
    }

    override fun createBinding(inflater: LayoutInflater, parent: ViewGroup?): IconItemBinding {
        return IconItemBinding.inflate(inflater, parent, false)
    }
}