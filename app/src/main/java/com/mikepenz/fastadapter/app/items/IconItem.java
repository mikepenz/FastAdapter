package com.mikepenz.fastadapter.app.items;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.mikepenz.fastadapter.IExpandable;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.ISubItem;
import com.mikepenz.fastadapter.app.R;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.typeface.IIcon;
import com.mikepenz.iconics.view.IconicsImageView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mikepenz on 28.12.15.
 */
public class IconItem<T extends IItem & IExpandable> extends AbstractItem<IconItem<T>, IconItem.ViewHolder> implements ISubItem<IconItem, T> {

    public IIcon mIcon;
    private T mParent;

    /**
     * setter method for the Icon
     *
     * @param icon the icon
     * @return this
     */
    public IconItem withIcon(IIcon icon) {
        this.mIcon = icon;
        return this;
    }

    @Override
    public T getParent() {
        return mParent;
    }

    @Override
    public IconItem withParent(T parent) {
        mParent = parent;
        return this;
    }

    /**
     * defines the type defining this item. must be unique. preferably an id
     *
     * @return the type
     */
    @Override
    public int getType() {
        return R.id.fastadapter_icon_item_id;
    }

    /**
     * defines the layout which will be used for this item in the list
     *
     * @return the layout for this item
     */
    @Override
    public int getLayoutRes() {
        return R.layout.icon_item;
    }

    /**
     * binds the data of this item onto the viewHolder
     *
     * @param viewHolder the viewHolder of this item
     */
    @Override
    public void bindView(ViewHolder viewHolder, List<Object> payloads) {
        super.bindView(viewHolder, payloads);

        //define our data for the view
        viewHolder.image.setIcon(new IconicsDrawable(viewHolder.image.getContext(), mIcon));
        viewHolder.name.setText(mIcon.getName());
    }

    @Override
    public void unbindView(ViewHolder holder) {
        super.unbindView(holder);
        holder.image.setImageDrawable(null);
    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    /**
     * our ViewHolder
     */
    protected static class ViewHolder extends RecyclerView.ViewHolder {
        protected View view;
        @BindView(R.id.name)
        public TextView name;
        @BindView(R.id.icon)
        public IconicsImageView image;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            this.view = view;
        }
    }
}
