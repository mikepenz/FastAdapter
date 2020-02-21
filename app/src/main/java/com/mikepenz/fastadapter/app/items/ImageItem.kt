package com.mikepenz.fastadapter.app.items

import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.view.ViewPropertyAnimator
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.app.R
import com.mikepenz.fastadapter.items.AbstractItem
import com.mikepenz.fastadapter.listeners.ClickEventHook
import com.mikepenz.iconics.view.IconicsImageView

/**
 * Created by mikepenz on 28.12.15.
 */
class ImageItem : AbstractItem<ImageItem.ViewHolder>() {

    var mImageUrl: String? = null
    var mName: String? = null
    var mDescription: String? = null
    var mStarred = false

    /**
     * defines the type defining this item. must be unique. preferably an id
     *
     * @return the type
     */
    override val type: Int
        get() = R.id.fastadapter_image_item_id

    /**
     * defines the layout which will be used for this item in the list
     *
     * @return the layout for this item
     */
    override val layoutRes: Int
        get() = R.layout.image_item

    fun withImage(imageUrl: String): ImageItem {
        this.mImageUrl = imageUrl
        return this
    }

    fun withName(name: String): ImageItem {
        this.mName = name
        return this
    }

    fun withDescription(description: String): ImageItem {
        this.mDescription = description
        return this
    }

    fun withStarred(starred: Boolean): ImageItem {
        this.mStarred = starred
        return this
    }

    fun withIdentifier(identifier: Long): ImageItem {
        this.identifier = identifier
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

        //define our data for the view
        holder.imageName.text = mName
        holder.imageDescription.text = mDescription
        holder.imageView.setImageBitmap(null)

        //we pre-style our heart :D
        style(holder.imageLovedOn, if (mStarred) 1 else 0)
        style(holder.imageLovedOff, if (mStarred) 0 else 1)

        //load glide
        Glide.with(ctx).load(mImageUrl).animate(R.anim.alpha_on).into(holder.imageView)
    }

    override fun unbindView(holder: ViewHolder) {
        super.unbindView(holder)
        Glide.clear(holder.imageView)
        holder.imageView.setImageDrawable(null)
    }

    override fun getViewHolder(v: View): ViewHolder {
        return ViewHolder(v)
    }

    /**
     * helper method to style the heart view
     *
     * @param view
     * @param value
     */
    private fun style(view: View, value: Int) {
        view.scaleX = value.toFloat()
        view.scaleY = value.toFloat()
        view.alpha = value.toFloat()
    }

    /**
     * helper method to animate the heart view
     *
     * @param imageLovedOn
     * @param imageLovedOff
     * @param on
     */
    fun animateHeart(imageLovedOn: View, imageLovedOff: View, on: Boolean) {
        imageLovedOn.visibility = View.VISIBLE
        imageLovedOff.visibility = View.VISIBLE

        viewPropertyStartCompat(imageLovedOff.animate().scaleX((if (on) 0 else 1).toFloat()).scaleY((if (on) 0 else 1).toFloat()).alpha((if (on) 0 else 1).toFloat()))
        viewPropertyStartCompat(imageLovedOn.animate().scaleX((if (on) 1 else 0).toFloat()).scaleY((if (on) 1 else 0).toFloat()).alpha((if (on) 1 else 0).toFloat()))
    }

    /**
     * our ViewHolder
     */
    class ViewHolder(var view: View) : RecyclerView.ViewHolder(view) {
        var imageView: ImageView = view.findViewById(R.id.item_image_img)
        var imageName: TextView = view.findViewById(R.id.item_image_name)
        var imageDescription: TextView = view.findViewById(R.id.item_image_description)
        var imageLovedContainer: RelativeLayout = view.findViewById(R.id.item_image_loved_container)
        var imageLovedOn: IconicsImageView = view.findViewById(R.id.item_image_loved_yes)
        var imageLovedOff: IconicsImageView = view.findViewById(R.id.item_image_loved_no)

        init {
            //optimization to preset the correct height for our device
            val columns = view.context.resources.getInteger(R.integer.wall_splash_columns)
            val screenWidth = view.context.resources.displayMetrics.widthPixels
            val finalHeight = (screenWidth / 1.5).toInt()
            imageView.minimumHeight = finalHeight / columns
            imageView.maxHeight = finalHeight / columns
            imageView.adjustViewBounds = false
            //set height as layoutParameter too
            val lp = imageView.layoutParams as FrameLayout.LayoutParams
            lp.height = finalHeight / columns
            imageView.layoutParams = lp
        }
    }

    class ImageItemHeartClickEvent : ClickEventHook<ImageItem>() {
        override fun onBindMany(viewHolder: RecyclerView.ViewHolder): List<View>? {
            if (viewHolder is ViewHolder) {
                return listOf(viewHolder.imageLovedContainer)
            }
            return super.onBindMany(viewHolder)
        }

        override fun onClick(v: View, position: Int, fastAdapter: FastAdapter<ImageItem>, item: ImageItem) {
            item.withStarred(!item.mStarred)
            //we animate the heart
            item.animateHeart((v as ViewGroup).getChildAt(0), v.getChildAt(1), item.mStarred)

            //we display the info about the click
            Toast.makeText(v.context, item.mImageUrl + " - " + item.mStarred, Toast.LENGTH_SHORT).show()
        }
    }

    companion object {

        /**
         * helper method for the animator on APIs < 14
         *
         * @param animator
         */
        fun viewPropertyStartCompat(animator: ViewPropertyAnimator) {
            if (Build.VERSION.SDK_INT >= 14) {
                animator.start()
            }
        }
    }
}
