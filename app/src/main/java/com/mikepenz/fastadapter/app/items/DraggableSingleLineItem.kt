package com.mikepenz.fastadapter.app.items

import android.graphics.drawable.Drawable
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.app.R
import com.mikepenz.fastadapter.drag.IDraggable
import com.mikepenz.fastadapter.items.AbstractItem
import com.mikepenz.fastadapter.listeners.TouchEventHook
import com.mikepenz.fastadapter.ui.utils.ImageHolder
import com.mikepenz.fastadapter.ui.utils.StringHolder

class DraggableSingleLineItem(icon: Drawable, name: String) : AbstractItem<DraggableSingleLineItem.ViewHolder>(), IDraggable {
    val icon: ImageHolder = ImageHolder(icon)
    val name: StringHolder = StringHolder(name)

    override val isDraggable: Boolean = true

    override val type: Int
        get() = R.id.fastadapter_draggable_single_line_item

    override val layoutRes: Int
        get() = R.layout.draggable_single_line_item

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
        var dragHandle: View = view.findViewById(R.id.drag_handle)
    }
}

class DragHandleTouchEvent(val action: (position: Int) -> Unit) : TouchEventHook<DraggableSingleLineItem>() {
    override fun onBind(viewHolder: RecyclerView.ViewHolder): View? {
        return if (viewHolder is DraggableSingleLineItem.ViewHolder) viewHolder.dragHandle else null
    }

    override fun onTouch(
        v: View,
        event: MotionEvent,
        position: Int,
        fastAdapter: FastAdapter<DraggableSingleLineItem>,
        item: DraggableSingleLineItem
    ): Boolean {
        return if (event.action == MotionEvent.ACTION_DOWN) {
            action(position)
            true
        } else {
            false
        }
    }
}

