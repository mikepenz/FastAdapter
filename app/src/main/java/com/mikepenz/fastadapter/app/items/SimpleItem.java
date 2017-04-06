package com.mikepenz.fastadapter.app.items;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.StringRes;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.mikepenz.fastadapter.IDraggable;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.app.R;
import com.mikepenz.fastadapter.commons.utils.FastAdapterUIUtils;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.materialdrawer.holder.StringHolder;
import com.mikepenz.materialize.util.UIUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mikepenz on 28.12.15.
 */
public class SimpleItem extends AbstractItem<SimpleItem, SimpleItem.ViewHolder> implements IDraggable<SimpleItem, IItem> {

    public String header;
    public StringHolder name;
    public StringHolder description;

    private boolean mIsDraggable = true;

    public SimpleItem withHeader(String header) {
        this.header = header;
        return this;
    }

    public SimpleItem withName(String Name) {
        this.name = new StringHolder(Name);
        return this;
    }

    public SimpleItem withName(@StringRes int NameRes) {
        this.name = new StringHolder(NameRes);
        return this;
    }

    public SimpleItem withDescription(String description) {
        this.description = new StringHolder(description);
        return this;
    }

    public SimpleItem withDescription(@StringRes int descriptionRes) {
        this.description = new StringHolder(descriptionRes);
        return this;
    }

    @Override
    public boolean isDraggable() {
        return mIsDraggable;
    }

    @Override
    public SimpleItem withIsDraggable(boolean draggable) {
        this.mIsDraggable = draggable;
        return this;
    }

    /**
     * defines the type defining this item. must be unique. preferably an id
     *
     * @return the type
     */
    @Override
    public int getType() {
        return R.id.fastadapter_sample_item_id;
    }

    /**
     * defines the layout which will be used for this item in the list
     *
     * @return the layout for this item
     */
    @Override
    public int getLayoutRes() {
        return R.layout.sample_item;
    }

    /**
     * binds the data of this item onto the viewHolder
     *
     * @param viewHolder the viewHolder of this item
     */
    @Override
    public void bindView(ViewHolder viewHolder, List<Object> payloads) {
        super.bindView(viewHolder, payloads);

        //get the context
        Context ctx = viewHolder.itemView.getContext();

        //set the background for the item
        UIUtils.setBackground(viewHolder.view, FastAdapterUIUtils.getSelectableBackground(ctx, Color.RED, true));
        //set the text for the name
        StringHolder.applyTo(name, viewHolder.name);
        //set the text for the description or hide
        StringHolder.applyToOrHide(description, viewHolder.description);
    }

    @Override
    public void unbindView(ViewHolder holder) {
        super.unbindView(holder);
        holder.name.setText(null);
        holder.description.setText(null);
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
        @BindView(R.id.material_drawer_name)
        TextView name;
        @BindView(R.id.material_drawer_description)
        TextView description;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            this.view = view;
        }
    }
}
