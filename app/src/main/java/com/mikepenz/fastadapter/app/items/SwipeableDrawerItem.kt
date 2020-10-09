package com.mikepenz.fastadapter.app.items

import android.graphics.Color
import android.view.View
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.fastadapter.app.R
import com.mikepenz.fastadapter.app.adapters.IDraggableViewHolder
import com.mikepenz.fastadapter.drag.IDraggable
import com.mikepenz.fastadapter.items.AbstractItem
import com.mikepenz.fastadapter.swipe.IDrawerSwipeableViewHolder
import com.mikepenz.fastadapter.swipe.ISwipeable
import com.mikepenz.materialdrawer.holder.StringHolder
import io.reactivex.functions.Consumer


/**
 * Created by Robb on 2020-07-03
 */
class SwipeableDrawerItem : AbstractItem<SwipeableDrawerItem.ViewHolder>(), ISwipeable, IDraggable {

    var name: StringHolder? = null
    var description: StringHolder? = null

    var deleteAction: Consumer<SwipeableDrawerItem>? = null
    var archiveAction: Consumer<SwipeableDrawerItem>? = null
    var shareAction: Consumer<SwipeableDrawerItem>? = null
    override var isSwipeable = true
    override var isDraggable = true

    /**
     * defines the type defining this item. must be unique. preferably an id
     *
     * @return the type
     */
    override val type: Int
        get() = R.id.fastadapter_swipable_drawer_item_id

    /**
     * defines the layout which will be used for this item in the list
     *
     * @return the layout for this item
     */
    override val layoutRes: Int
        get() = R.layout.swipeable_drawer_item

    fun withName(Name: String): SwipeableDrawerItem {
        this.name = StringHolder(Name)
        return this
    }

    fun withName(@StringRes NameRes: Int): SwipeableDrawerItem {
        this.name = StringHolder(NameRes)
        return this
    }

    fun withDescription(description: String): SwipeableDrawerItem {
        this.description = StringHolder(description)
        return this
    }

    fun withDescription(@StringRes descriptionRes: Int): SwipeableDrawerItem {
        this.description = StringHolder(descriptionRes)
        return this
    }

    fun withIsSwipeable(swipeable: Boolean): SwipeableDrawerItem {
        this.isSwipeable = swipeable
        return this
    }

    fun withIsDraggable(draggable: Boolean): SwipeableDrawerItem {
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

        holder.deleteActionRunnable = Runnable { deleteAction?.accept(this) }
        holder.archiveActionRunnable = Runnable { archiveAction?.accept(this) }
        holder.shareActionRunnable = Runnable { shareAction?.accept(this) }
    }

    override fun unbindView(holder: ViewHolder) {
        super.unbindView(holder)
        holder.name.text = null
        holder.description.text = null
        holder.deleteActionRunnable = null
        holder.archiveActionRunnable = null
        holder.shareActionRunnable = null
        holder.itemContent.translationX = 0f
    }

    override fun getViewHolder(v: View): ViewHolder {
        return ViewHolder(v)
    }

    /**
     * our ViewHolder
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view), IDraggableViewHolder, IDrawerSwipeableViewHolder {
        var name: TextView = view.findViewById(R.id.material_drawer_name)
        var description: TextView = view.findViewById(R.id.material_drawer_description)
        var archiveBtn: View = view.findViewById(R.id.archive_btn)
        var deleteBtn: View = view.findViewById(R.id.delete_btn)
        var shareBtn: View = view.findViewById(R.id.share_btn)
        var itemContent: View = view.findViewById(R.id.item_content)

        var deleteActionRunnable: Runnable? = null
        var archiveActionRunnable: Runnable? = null
        var shareActionRunnable: Runnable? = null

        init {
            deleteBtn.setOnClickListener {
                deleteActionRunnable?.run()
            }
            archiveBtn.setOnClickListener {
                archiveActionRunnable?.run()
            }
            shareBtn.setOnClickListener {
                shareActionRunnable?.run()
            }
        }

        override fun onDropped() {
            itemContent.setBackgroundColor(Color.WHITE)
        }

        override fun onDragged() {
            itemContent.setBackgroundColor(Color.LTGRAY)
        }

        override val swipeableView: View
            get() = itemContent
    }
}
