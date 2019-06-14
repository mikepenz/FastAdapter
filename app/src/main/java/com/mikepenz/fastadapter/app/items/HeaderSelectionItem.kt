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

    override fun bindView(viewHolder: ViewHolder, payloads: MutableList<Any>) {
        super.bindView(viewHolder, payloads)

        //get the context
        val ctx = viewHolder.itemView.context

        //set the background for the item
        viewHolder.view.clearAnimation()
        ViewCompat.setBackground(viewHolder.view, FastAdapterUIUtils.getSelectableBackground(ctx, Color.RED, true))
        //set the text for the name
        StringHolder.applyTo(name, viewHolder.name)
        //set the text for the description or hide

        var selectedSubItems = 0
        if (mSubSelectionProvider != null)
            selectedSubItems = mSubSelectionProvider?.invoke() ?: 0
        val descr = StringHolder(description?.text)
        if (selectedSubItems > 0)
            descr.setText("Selected children: " + selectedSubItems + "/" + subItems.size)
        StringHolder.applyToOrHide(descr, viewHolder.description)

        viewHolder.description.setTextColor(if (selectedSubItems == 0) Color.BLACK else Color.RED)
    }
}
