package com.mikepenz.fastadapter.app.items

import android.graphics.Color
import android.view.View
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.fastadapter.app.R
import com.mikepenz.fastadapter.app.adapters.IDraggableViewHolder
import com.mikepenz.fastadapter.drag.IDraggable
import com.mikepenz.fastadapter.items.AbstractItem
import com.mikepenz.fastadapter.swipe.ISwipeable
import com.mikepenz.materialdrawer.holder.StringHolder

/**
 * Created by Mattias on 2016-02-15.
 */
class SwipeableItem : AbstractItem<SwipeableItem.ViewHolder>(), ISwipeable, IDraggable {

    var name: StringHolder? = null
    var description: StringHolder? = null

    var undoTextSwipeFromRight: StringHolder? = null
    var undoTextSwipeFromLeft: StringHolder? = null
    var undoTextSwipeFromTop: StringHolder? = null
    var undoTextSwipeFromBottom: StringHolder? = null

    var swipedDirection: Int = 0
    var swipedAction: Runnable? = null
    override var isSwipeable = true
    override var isDraggable = true

    /**
     * defines the type defining this item. must be unique. preferably an id
     *
     * @return the type
     */
    override val type: Int
        get() = R.id.fastadapter_swipable_item_id

    /**
     * defines the layout which will be used for this item in the list
     *
     * @return the layout for this item
     */
    override val layoutRes: Int
        get() = R.layout.swipeable_item

    fun withName(Name: String): SwipeableItem {
        this.name = StringHolder(Name)
        return this
    }

    fun withName(@StringRes NameRes: Int): SwipeableItem {
        this.name = StringHolder(NameRes)
        return this
    }

    fun withDescription(description: String): SwipeableItem {
        this.description = StringHolder(description)
        return this
    }

    fun withDescription(@StringRes descriptionRes: Int): SwipeableItem {
        this.description = StringHolder(descriptionRes)
        return this
    }

    fun withIsSwipeable(swipeable: Boolean): SwipeableItem {
        this.isSwipeable = swipeable
        return this
    }

    fun withIsDraggable(draggable: Boolean): SwipeableItem {
        this.isDraggable = draggable
        return this
    }

    /**
     * binds the data of this item onto the viewHolder
     *
     * @param holder the viewHolder of this item
     */
    override fun bindView(holder: ViewHolder, payloads: List<Any>) {
        super.bindView(holder, payloads)

        //set the text for the name
        StringHolder.applyTo(name, holder.name)
        //set the text for the description or hide
        StringHolder.applyToOrHide(description, holder.description)

        holder.swipeResultContent.visibility = if (swipedDirection != 0) View.VISIBLE else View.GONE
        holder.itemContent.visibility = if (swipedDirection != 0) View.GONE else View.VISIBLE

        var swipedAction: CharSequence? = null
        var swipedText: CharSequence? = null
        if (swipedDirection != 0) {
            swipedAction = holder.itemView.context.getString(R.string.action_undo)
            swipedText = if (swipedDirection == ItemTouchHelper.LEFT) "Removed" else "Archived"
            holder.swipeResultContent.setBackgroundColor(if (swipedDirection == ItemTouchHelper.LEFT) Color.RED else Color.BLUE)
        }
        holder.swipedAction.text = swipedAction ?: ""
        holder.swipedText.text = swipedText ?: ""
        holder.swipedActionRunnable = this.swipedAction
    }

    override fun unbindView(holder: ViewHolder) {
        super.unbindView(holder)
        holder.name.text = null
        holder.description.text = null
        holder.swipedAction.text = null
        holder.swipedText.text = null
        holder.swipedActionRunnable = null
    }

    override fun getViewHolder(v: View): ViewHolder {
        return ViewHolder(v)
    }

    /**
     * our ViewHolder
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view), IDraggableViewHolder {
        var name: TextView = view.findViewById(R.id.material_drawer_name)
        var description: TextView = view.findViewById(R.id.material_drawer_description)
        var swipeResultContent: View = view.findViewById(R.id.swipe_result_content)
        var itemContent: View = view.findViewById(R.id.item_content)
        var swipedText: TextView = view.findViewById(R.id.swiped_text)
        var swipedAction: TextView = view.findViewById(R.id.swiped_action)

        var swipedActionRunnable: Runnable? = null

        init {
            swipedAction.setOnClickListener {
                swipedActionRunnable?.run()
            }
        }

        override fun onDropped() {
            itemContent.setBackgroundColor(Color.TRANSPARENT)
        }

        override fun onDragged() {
            itemContent.setBackgroundColor(Color.LTGRAY)
        }
    }
}
