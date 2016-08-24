package com.mikepenz.fastadapter.app.items;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mikepenz.fastadapter.app.R;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.fastadapter.utils.FastAdapterUIUtils;
import com.mikepenz.fastadapter.utils.ViewHolderFactory;
import com.mikepenz.materialize.util.UIUtils;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by mikepenz on 28.12.15.
 */
public class SimpleImageItem extends AbstractItem<SimpleImageItem, SimpleImageItem.ViewHolder> {
    //the static ViewHolderFactory which will be used to generate the ViewHolder for this Item
    private static final ViewHolderFactory<? extends ViewHolder> FACTORY = new ItemFactory();

    public String mImageUrl;
    public String mName;
    public String mDescription;

    public SimpleImageItem withImage(String imageUrl) {
        this.mImageUrl = imageUrl;
        return this;
    }

    public SimpleImageItem withName(String name) {
        this.mName = name;
        return this;
    }

    public SimpleImageItem withDescription(String description) {
        this.mDescription = description;
        return this;
    }

    /**
     * defines the type defining this item. must be unique. preferably an id
     *
     * @return the type
     */
    @Override
    public int getType() {
        return R.id.fastadapter_simple_image_item_id;
    }

    /**
     * defines the layout which will be used for this item in the list
     *
     * @return the layout for this item
     */
    @Override
    public int getLayoutRes() {
        return R.layout.simple_image_item;
    }

    /**
     * binds the data of this item onto the viewHolder
     *
     * @param viewHolder the viewHolder of this item
     */
    @Override
    public void bindView(SimpleImageItem.ViewHolder viewHolder, List<Object> payloads) {
        super.bindView(viewHolder, payloads);

        if (payloads != null && payloads.size() > 0) {
            viewHolder.imageName.setText(payloads.get(0).toString());
        } else {
            //get the context
            Context ctx = viewHolder.itemView.getContext();

            //define our data for the view
            viewHolder.imageName.setText(mName);
            viewHolder.imageDescription.setText(mDescription);
            viewHolder.imageView.setImageBitmap(null);

            //set the background for the item
            int color = UIUtils.getThemeColor(ctx, R.attr.colorPrimary);
            viewHolder.view.setForeground(FastAdapterUIUtils.getSelectablePressedBackground(ctx, FastAdapterUIUtils.adjustAlpha(color, 100), 50, true));

            //load glide
            Glide.clear(viewHolder.imageView);
            Glide.with(ctx).load(mImageUrl).animate(R.anim.alpha_on).into(viewHolder.imageView);
        }
    }

    /**
     * our ItemFactory implementation which creates the ViewHolder for our adapter.
     * It is highly recommended to implement a ViewHolderFactory as it is 0-1ms faster for ViewHolder creation,
     * and it is also many many times more efficient if you define custom listeners on views within your item.
     */
    protected static class ItemFactory implements ViewHolderFactory<ViewHolder> {
        public ViewHolder create(View v) {
            return new ViewHolder(v);
        }
    }

    /**
     * return our ViewHolderFactory implementation here
     *
     * @return
     */
    @Override
    public ViewHolderFactory<? extends ViewHolder> getFactory() {
        return FACTORY;
    }

    /**
     * our ViewHolder
     */
    protected static class ViewHolder extends RecyclerView.ViewHolder {
        protected FrameLayout view;
        @Bind(R.id.item_image_img)
        protected ImageView imageView;
        @Bind(R.id.item_image_name)
        protected TextView imageName;
        @Bind(R.id.item_image_description)
        protected TextView imageDescription;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            this.view = (FrameLayout) view;

            //optimization to preset the correct height for our device
            int screenWidth = view.getContext().getResources().getDisplayMetrics().widthPixels;
            int finalHeight = (int) (screenWidth / 1.5) / 2;
            imageView.setMinimumHeight(finalHeight);
            imageView.setMaxHeight(finalHeight);
            imageView.setAdjustViewBounds(false);
            //set height as layoutParameter too
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) imageView.getLayoutParams();
            lp.height = finalHeight;
            imageView.setLayoutParams(lp);
        }
    }
}
