package com.mikepenz.fastadapter_extensions.items;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.DrawableRes;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.fastadapter.utils.ViewHolderFactory;
import com.mikepenz.library_extensions.R;
import com.mikepenz.materialize.holder.ImageHolder;
import com.mikepenz.materialize.holder.StringHolder;

/**
 * Created by fabianterhorst on 30.03.16.
 */
public class SingleLineItem extends AbstractItem<SingleLineItem, SingleLineItem.ViewHolder> {

    private static final ViewHolderFactory<? extends ViewHolder> FACTORY = new ItemFactory();

    private StringHolder mName;

    private ImageHolder mAvatar, mIcon;

    public SingleLineItem withName(String name) {
        this.mName = new StringHolder(name);
        return this;
    }

    public SingleLineItem withAvatar(Drawable avatar) {
        this.mAvatar = new ImageHolder(avatar);
        return this;
    }

    public SingleLineItem withAvatar(@DrawableRes int avatarRes) {
        this.mAvatar = new ImageHolder(avatarRes);
        return this;
    }

    public SingleLineItem withAvatar(Uri uri) {
        this.mAvatar = new ImageHolder(uri);
        return this;
    }

    public SingleLineItem withAvatar(Bitmap bitmap) {
        this.mAvatar = new ImageHolder(bitmap);
        return this;
    }

    public SingleLineItem withAvatar(String url) {
        this.mAvatar = new ImageHolder(Uri.parse(url));
        return this;
    }

    public SingleLineItem withIcon(Drawable icon) {
        this.mIcon = new ImageHolder(icon);
        return this;
    }

    public SingleLineItem withIcon(@DrawableRes int iconRes) {
        this.mIcon = new ImageHolder(iconRes);
        return this;
    }

    public SingleLineItem withIcon(Uri uri) {
        this.mIcon = new ImageHolder(uri);
        return this;
    }

    public SingleLineItem withIcon(Bitmap bitmap) {
        this.mIcon = new ImageHolder(bitmap);
        return this;
    }

    public SingleLineItem withIcon(String url) {
        this.mIcon = new ImageHolder(Uri.parse(url));
        return this;
    }

    public StringHolder getName() {
        return mName;
    }

    public ImageHolder getAvatar() {
        return mAvatar;
    }

    public ImageHolder getIcon() {
        return mIcon;
    }

    @Override
    public int getType() {
        return R.id.single_line_item_id;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.single_line_item;
    }

    @Override
    public void bindView(ViewHolder holder) {
        super.bindView(holder);
        mName.applyTo(holder.name);
        if (mAvatar != null) {
            if (holder.avatar.getVisibility() == View.GONE) {
                holder.avatar.setVisibility(View.VISIBLE);
            }
            mAvatar.applyTo(holder.avatar);
        } else if (holder.avatar.getVisibility() == View.VISIBLE) {
            holder.avatar.setVisibility(View.GONE);
        }
        if (mIcon != null) {
            if (holder.icon.getVisibility() == View.GONE) {
                holder.icon.setVisibility(View.VISIBLE);
            }
            mIcon.applyTo(holder.icon);
        } else if (holder.icon.getVisibility() == View.VISIBLE) {
            holder.icon.setVisibility(View.GONE);
        }
    }

    protected static class ItemFactory implements ViewHolderFactory<ViewHolder> {
        public ViewHolder create(View v) {
            return new ViewHolder(v);
        }
    }

    @Override
    public ViewHolderFactory<? extends ViewHolder> getFactory() {
        return FACTORY;
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {

        protected TextView name;
        protected ImageView avatar, icon;

        public ViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.name);
            avatar = (ImageView) view.findViewById(R.id.avatar);
            icon = (ImageView) view.findViewById(R.id.icon);
        }
    }
}
