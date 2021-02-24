package com.mikepenz.fastadapter.ui.items

import android.view.View
import android.widget.ProgressBar
import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.fastadapter.items.AbstractItem
import com.mikepenz.fastadapter.ui.R
import com.mikepenz.fastadapter.ui.utils.FastAdapterUIUtils

class ProgressItem : AbstractItem<ProgressItem.ViewHolder>() {

    override val type: Int
        get() = R.id.progress_item_id

    override val layoutRes: Int
        get() = R.layout.progress_item

    override fun bindView(holder: ViewHolder, payloads: List<Any>) {
        super.bindView(holder, payloads)
        if (isEnabled) {
            holder.itemView.setBackgroundResource(FastAdapterUIUtils.getSelectableBackground(holder.itemView.context))
        }
    }

    override fun getViewHolder(v: View): ViewHolder = ViewHolder(v)

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private var progressBar: ProgressBar = view.findViewById(R.id.progress_bar)
    }
}

