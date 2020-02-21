package com.mikepenz.fastadapter.app.items

import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import butterknife.ButterKnife
import com.bumptech.glide.Glide
import com.mikepenz.aboutlibraries.util.getThemeColor
import com.mikepenz.fastadapter.IItemVHFactory
import com.mikepenz.fastadapter.app.R
import com.mikepenz.fastadapter.items.BaseItem
import com.mikepenz.fastadapter.items.BaseItemFactory
import com.mikepenz.fastadapter.ui.utils.FastAdapterUIUtils

/**
 * Created by mikepenz on 28.12.15.
 */
class SimpleImageItem : BaseItem<SimpleImageItem.ViewHolder>() {

    private var mImageUrl: String? = null
    private var mName: String? = null
    private var mDescription: String? = null
    private var isPlaceholder: Boolean = false // True when used as placeholderInterceptor by PagedModelAdapter

    /**
     * defines the type defining this item. must be unique. preferably an id
     *
     * @return the type
     */
    override val type: Int
        get() = R.id.fastadapter_simple_image_item_id

    /**
     * defines the factory to create this items VH
     */
    override val factory: IItemVHFactory<ViewHolder> = SimpleImageItemFactory

    fun withImage(imageUrl: String): SimpleImageItem {
        this.mImageUrl = imageUrl
        return this
    }

    fun withName(name: String): SimpleImageItem {
        this.mName = name
        return this
    }

    fun withDescription(description: String): SimpleImageItem {
        this.mDescription = description
        return this
    }

    fun withIdentifier(identifier: Long): SimpleImageItem {
        this.identifier = identifier
        return this
    }

    fun setPlaceholder(): SimpleImageItem {
        this.isPlaceholder = true
        return this
    }

    /**
     * binds the data of this item onto the viewHolder
     *
     * @param holder the viewHolder of this item
     */
    override fun bindView(holder: ViewHolder, payloads: List<Any>) {
        super.bindView(holder, payloads)

        //get the context
        val ctx = holder.itemView.context

        if (isPlaceholder) { // Nothing to display at all; may also display placeholder resources
            holder.view.visibility = View.GONE
            return
        } else holder.view.visibility = View.VISIBLE

        //define our data for the view
        holder.imageName.text = mName
        holder.imageDescription.text = mDescription
        holder.imageView.setImageBitmap(null)

        //set the background for the item
        val color = ctx.getThemeColor(R.attr.colorPrimary, R.color.colorPrimary)

        holder.view.clearAnimation()
        holder.view.foreground = FastAdapterUIUtils.getSelectablePressedBackground(ctx, FastAdapterUIUtils.adjustAlpha(color, 100), 50, true)

        //load glide
        Glide.clear(holder.imageView)
        Glide.with(ctx).load(mImageUrl).animate(R.anim.alpha_on).into(holder.imageView)
    }

    override fun unbindView(holder: ViewHolder) {
        super.unbindView(holder)
        Glide.clear(holder.imageView)
        holder.imageView.setImageDrawable(null)
        holder.imageDescription.text = null
    }


    /**
     * our ViewHolder
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var view: FrameLayout
        var imageView: ImageView = view.findViewById(R.id.item_image_img)
        var imageName: TextView = view.findViewById(R.id.item_image_name)
        var imageDescription: TextView = view.findViewById(R.id.item_image_description)

        init {
            ButterKnife.bind(this, view)
            this.view = view as FrameLayout

            //optimization to preset the correct height for our device
            val screenWidth = view.context.resources.displayMetrics.widthPixels
            val finalHeight = (screenWidth / 1.5).toInt() / 2
            imageView.minimumHeight = finalHeight
            imageView.maxHeight = finalHeight
            imageView.adjustViewBounds = false
            //set height as layoutParameter too
            val lp = imageView.layoutParams as FrameLayout.LayoutParams
            lp.height = finalHeight
            imageView.layoutParams = lp
        }
    }
}

/**
 * Created by mikepenz on 28.12.15.
 */
object SimpleImageItemFactory : BaseItemFactory<SimpleImageItem.ViewHolder>() {
    /**
     * defines the layout which will be used for this item in the list
     *
     * @return the layout for this item
     */
    override val layoutRes: Int
        get() = R.layout.simple_image_item

    override fun getViewHolder(v: View): SimpleImageItem.ViewHolder {
        return SimpleImageItem.ViewHolder(v)
    }
}