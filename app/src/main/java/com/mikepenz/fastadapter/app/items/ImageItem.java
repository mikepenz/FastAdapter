package com.mikepenz.fastadapter.app.items;

import android.content.Context;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mikepenz.fastadapter.app.R;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.fastadapter.utils.ViewHolderFactory;
import com.mikepenz.iconics.view.IconicsImageView;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by mikepenz on 28.12.15.
 */
public class ImageItem extends AbstractItem<ImageItem, ImageItem.ViewHolder> {

    public String mImageUrl;
    public String mName;
    public String mDescription;
    public boolean mStarred = false;

    public OnItemClickListener mOnItemClickListener;

    public ImageItem withImage(String imageUrl) {
        this.mImageUrl = imageUrl;
        return this;
    }

    public ImageItem withName(String name) {
        this.mName = name;
        return this;
    }

    public ImageItem withDescription(String description) {
        this.mDescription = description;
        return this;
    }

    public ImageItem withStarred(boolean starred) {
        this.mStarred = starred;
        return this;
    }

    public ImageItem withOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
        return this;
    }

    @Override
    public int getType() {
        return R.id.fastadapter_image_item_id;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.image_item;
    }

    @Override
    public void bindView(ViewHolder viewHolder) {
        Context ctx = viewHolder.itemView.getContext();

        //define our data for the view
        viewHolder.imageName.setText(mName);
        viewHolder.imageDescription.setText(mDescription);
        viewHolder.imageView.setImageBitmap(null);

        //we pre-style our heart :D
        style(viewHolder.imageLovedOn, mStarred ? 1 : 0);
        style(viewHolder.imageLovedOff, mStarred ? 0 : 1);

        //load glide
        Glide.clear(viewHolder.imageView);
        Glide.with(ctx).load(mImageUrl).animate(R.anim.alpha_on).into(viewHolder.imageView);

        viewHolder.imageLovedContainer.setOnClickListener(onClickListener);
    }

    /**
     * the onClickListener used to animate from one heart to the other
     */
    public View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mStarred = !mStarred;
            animateHeart(((ViewGroup) v).getChildAt(0), ((ViewGroup) v).getChildAt(1), mStarred);
            mOnItemClickListener.onLovedClick(mImageUrl, mStarred);
        }
    };

    /**
     * our ItemFactory implementation which creates the ViewHolder for our adapter.
     * It is highly recommended to implement a ViewHolderFactory as it is 0-1ms faster for ViewHolder creation,
     * and it is also many many times more efficient if you define custom listeners on views within your item.
     */
    public class ItemFactory implements ViewHolderFactory<ViewHolder> {
        public ViewHolder create(View v) {
            return new ViewHolder(v);
        }
    }

    /**
     * helper method to style the heart view
     *
     * @param view
     * @param value
     */
    private void style(View view, int value) {
        view.setScaleX(value);
        view.setScaleY(value);
        view.setAlpha(value);
    }

    /**
     * helper method to animate the heart view
     *
     * @param imageLovedOn
     * @param imageLovedOff
     * @param on
     */
    private void animateHeart(View imageLovedOn, View imageLovedOff, boolean on) {
        imageLovedOn.setVisibility(View.VISIBLE);
        imageLovedOff.setVisibility(View.VISIBLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            viewPropertyStartCompat(imageLovedOff.animate().scaleX(on ? 0 : 1).scaleY(on ? 0 : 1).alpha(on ? 0 : 1));
            viewPropertyStartCompat(imageLovedOn.animate().scaleX(on ? 1 : 0).scaleY(on ? 1 : 0).alpha(on ? 1 : 0));
        }
    }

    /**
     * helper method for the animator on APIs < 14
     *
     * @param animator
     */
    public static void viewPropertyStartCompat(ViewPropertyAnimator animator) {
        if (Build.VERSION.SDK_INT >= 14) {
            animator.start();
        }
    }

    /**
     * return our ViewHolderFactory implementation here
     *
     * @return
     */
    @Override
    public ViewHolderFactory getFactory() {
        return new ItemFactory();
    }

    /**
     * our ViewHolder
     */
    protected static class ViewHolder extends RecyclerView.ViewHolder {
        protected View view;
        @Bind(R.id.item_image_img)
        protected ImageView imageView;
        @Bind(R.id.item_image_name)
        protected TextView imageName;
        @Bind(R.id.item_image_description)
        protected TextView imageDescription;
        @Bind(R.id.item_image_loved_container)
        protected RelativeLayout imageLovedContainer;
        @Bind(R.id.item_image_loved_yes)
        protected IconicsImageView imageLovedOn;
        @Bind(R.id.item_image_loved_no)
        protected IconicsImageView imageLovedOff;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            this.view = view;

            //optimization to preset the correct height for our device
            int columns = view.getContext().getResources().getInteger(R.integer.wall_splash_columns);
            int screenWidth = view.getContext().getResources().getDisplayMetrics().widthPixels;
            int finalHeight = (int) (screenWidth / 1.5);
            imageView.setMinimumHeight(finalHeight / columns);
            imageView.setMaxHeight(finalHeight / columns);
            imageView.setAdjustViewBounds(false);
            //set height as layoutParameter too
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) imageView.getLayoutParams();
            lp.height = finalHeight / columns;
            imageView.setLayoutParams(lp);
        }
    }

    /**
     * our listener which is triggered when the heart is clicked
     */
    public interface OnItemClickListener {
        void onLovedClick(String image, boolean starred);
    }
}
