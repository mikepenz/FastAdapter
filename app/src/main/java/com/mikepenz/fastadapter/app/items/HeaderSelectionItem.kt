package com.mikepenz.fastadapter.app.items

import android.graphics.Color
import androidx.core.view.ViewCompat
import com.mikepenz.fastadapter.app.items.expandable.SimpleSubExpandableItem
import com.mikepenz.fastadapter.ui.utils.FastAdapterUIUtils
import com.mikepenz.materialdrawer.holder.StringHolder

/**
 * Created by flisar on 21.09.2016.
 */

open class HeaderSelectionItem : SimpleSubExpandableItem() {

    var mSubSelectionProvider: (() -> Int)? = null

    fun withSubSelectionProvider(subSelectionProvider: () -> Int) {
        this.mSubSelectionProvider = subSelectionProvider
    }

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

        var selectedSubItems = 0
        if (mSubSelectionProvider != null)
            selectedSubItems = mSubSelectionProvider?.invoke() ?: 0
        val descr = StringHolder(if (selectedSubItems > 0) description?.textString else "Selected children: " + selectedSubItems + "/" + subItems.size)
        StringHolder.applyToOrHide(descr, holder.description)

        holder.description.setTextColor(if (selectedSubItems == 0) Color.BLACK else Color.RED)
    }
}
