package com.mikepenz.fastadapter_extensions.items;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.DrawableRes;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mikepenz.fastadapter.commons.utils.FastAdapterUIUtils;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.library_extensions.R;
import com.mikepenz.materialize.holder.ImageHolder;
import com.mikepenz.materialize.holder.StringHolder;

import java.util.List;

/**
 * Created by fabianterhorst on 30.03.16.
 */
public class TwoLineItem extends AbstractItem<TwoLineItem, TwoLineItem.ViewHolder> {

    private StringHolder mName, mDescription;

    private ImageHolder mAvatar, mIcon;

    public TwoLineItem withName(String name) {
        this.mName = new StringHolder(name);
        return this;
    }

    public TwoLineItem withDescription(String description) {
        this.mDescription = new StringHolder(description);
        return this;
    }

    public TwoLineItem withAvatar(Drawable avatar) {
        this.mAvatar = new ImageHolder(avatar);
        return this;
    }

    public TwoLineItem withAvatar(@DrawableRes int avatarRes) {
        this.mAvatar = new ImageHolder(avatarRes);
        return this;
    }

    public TwoLineItem withAvatar(Uri uri) {
        this.mAvatar = new ImageHolder(uri);
        return this;
    }

    public TwoLineItem withAvatar(Bitmap bitmap) {
        this.mAvatar = new ImageHolder(bitmap);
        return this;
    }

    public TwoLineItem withAvatar(String url) {
        this.mAvatar = new ImageHolder(Uri.parse(url));
        return this;
    }

    public TwoLineItem withIcon(Drawable icon) {
        this.mIcon = new ImageHolder(icon);
        return this;
    }

    public TwoLineItem withIcon(@DrawableRes int iconRes) {
        this.mIcon = new ImageHolder(iconRes);
        return this;
    }

    public TwoLineItem withIcon(Uri uri) {
        this.mIcon = new ImageHolder(uri);
        return this;
    }

    public TwoLineItem withIcon(Bitmap bitmap) {
        this.mIcon = new ImageHolder(bitmap);
        return this;
    }

    public TwoLineItem withIcon(String url) {
        this.mIcon = new ImageHolder(Uri.parse(url));
        return this;
    }

    public StringHolder getName() {
        return mName;
    }

    public StringHolder getDescription() {
        return mDescription;
    }

    public ImageHolder getAvatar() {
        return mAvatar;
    }

    public ImageHolder getIcon() {
        return mIcon;
    }

    @Override
    public int getType() {
        return R.id.two_line_item_id;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.two_line_item;
    }

    @Override
    public void bindView(ViewHolder holder, List<Object> payloads) {
        super.bindView(holder, payloads);
        if (isEnabled()) {
            holder.itemView.setBackgroundResource(FastAdapterUIUtils.getSelectableBackground(holder.itemView.getContext()));
        }
        mName.applyTo(holder.name);
        mDescription.applyTo(holder.description);
        ImageHolder.applyToOrSetGone(mAvatar, holder.avatar);
        ImageHolder.applyToOrSetGone(mIcon, holder.icon);
    }

    @Override
    public void unbindView(ViewHolder holder) {
        holder.name.setText(null);
        holder.description.setText(null);
        holder.avatar.setImageDrawable(null);
        holder.avatar.setVisibility(View.VISIBLE);
        holder.icon.setImageDrawable(null);
        holder.icon.setVisibility(View.VISIBLE);
    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {

        protected TextView name, description;
        protected ImageView avatar, icon;

        public ViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.name);
            description = (TextView) view.findViewById(R.id.description);
            avatar = (ImageView) view.findViewById(R.id.avatar);
            icon = (ImageView) view.findViewById(R.id.icon);
        }
    }
}
