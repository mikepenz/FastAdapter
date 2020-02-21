package com.mikepenz.fastadapter.items

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.fastadapter.IItemVHFactory
import com.mikepenz.fastadapter.IItemViewGenerator
import java.util.*

/**
 * Implements the general methods of the IItem interface to speed up development.
 */
abstract class AbstractItem<VH : RecyclerView.ViewHolder> : BaseItem<VH>(), IItemVHFactory<VH>, IItemViewGenerator {

    /** The layout for the given item */
    @get:LayoutRes
    abstract val layoutRes: Int

    /**
     * This method is called by generateView(Context ctx), generateView(Context ctx, ViewGroup parent) and getViewHolder(ViewGroup parent)
     * it will generate the View from the layout, overwrite this if you want to implement your view creation programatically
     */
    open fun createView(ctx: Context, parent: ViewGroup?): View {
        return LayoutInflater.from(ctx).inflate(layoutRes, parent, false)
    }

    /** Generates a view by the defined LayoutRes */
    override fun generateView(ctx: Context): View {
        val viewHolder = getViewHolder(createView(ctx, null))

        //as we already know the type of our ViewHolder cast it to our type
        bindView(viewHolder, Collections.emptyList())

        //return the bound view
        return viewHolder.itemView
    }

    /** Generates a view by the defined LayoutRes and pass the LayoutParams from the parent */
    override fun generateView(ctx: Context, parent: ViewGroup): View {
        val viewHolder = getViewHolder(createView(ctx, parent))

        //as we already know the type of our ViewHolder cast it to our type
        bindView(viewHolder, Collections.emptyList())
        //return the bound and generatedView
        return viewHolder.itemView
    }

    /** Generates a ViewHolder from this Item with the given parent */
    override fun getViewHolder(parent: ViewGroup): VH {
        return getViewHolder(createView(parent.context, parent))
    }

    /**
     * This method returns the ViewHolder for our item, using the provided View.
     *
     * @return the ViewHolder for this Item
     */
    abstract fun getViewHolder(v: View): VH
}