package com.mikepenz.fastadapter.app.items

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.fastadapter.app.R
import com.mikepenz.fastadapter.items.AbstractItem
import com.mikepenz.fastadapter.ui.utils.StringHolder

class SectionHeaderItem(text: String) : AbstractItem<SectionHeaderItem.ViewHolder>() {
    val text: StringHolder = StringHolder(text)

    override val type: Int
        get() = R.id.fastadapter_section_header_item

    override val layoutRes: Int
        get() = R.layout.section_header_item

    override fun bindView(holder: ViewHolder, payloads: List<Any>) {
        super.bindView(holder, payloads)
        text.applyTo(holder.text)
    }

    override fun unbindView(holder: ViewHolder) {
        holder.text.text = null
    }

    override fun getViewHolder(v: View): ViewHolder = ViewHolder(v)

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var text: TextView = view.findViewById(R.id.text)
    }
}
