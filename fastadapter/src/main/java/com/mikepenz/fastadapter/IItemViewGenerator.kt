package com.mikepenz.fastadapter

import android.content.Context
import android.view.View
import android.view.ViewGroup

/**
 * Defines an additional interface to more conveniently generate the view of an item if not used in a RV
 */
interface IItemViewGenerator {
    /** Generates a view by the defined LayoutRes */
    fun generateView(ctx: Context): View

    /** Generates a view by the defined LayoutRes and pass the LayoutParams from the parent */
    fun generateView(ctx: Context, parent: ViewGroup): View
}

/**
NOTE This may be implemented as the following code showcases:

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
 */