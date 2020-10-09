package com.mikepenz.fastadapter.app.items

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.fastadapter.app.R
import com.mikepenz.fastadapter.items.AbstractItem

class LetterItem(var letter: String) : AbstractItem<LetterItem.ViewHolder>() {

    override val type: Int
        get() = R.id.fastadapter_letter_item_id

    override val layoutRes: Int
        get() = R.layout.letter_item

    override fun bindView(holder: ViewHolder, payloads: List<Any>) {
        super.bindView(holder, payloads)
        holder.text.text = letter
    }

    override fun unbindView(holder: ViewHolder) {
        super.unbindView(holder)
        holder.text.text = null
    }

    override fun getViewHolder(v: View): ViewHolder {
        return ViewHolder(v)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var text: TextView = view.findViewById(R.id.text)
    }
}
