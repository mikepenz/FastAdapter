package com.mikepenz.fastadapter.app.items;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.view.View;
import android.widget.TextView;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.app.R;
import com.mikepenz.fastadapter.commons.utils.FastAdapterUIUtils;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.fastadapter_extensions.drag.IDraggable;
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

    @Override
    public ViewHolder getViewHolder(@NonNull View v) {
        return new ViewHolder(v);
    }

    /**
     * our ViewHolder
     */
    protected static class ViewHolder extends FastAdapter.ViewHolder<SimpleItem> {
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

        @Override
        public void bindView(@NonNull SimpleItem item, @NonNull List<Object> payloads) {
            //get the context
            Context ctx = itemView.getContext();

            //set the background for the item
            UIUtils.setBackground(view, FastAdapterUIUtils.getSelectableBackground(ctx, Color.RED, true));
            //set the text for the name
            StringHolder.applyTo(item.name, name);
            //set the text for the description or hide
            StringHolder.applyToOrHide(item.description, description);
        }

        @Override
        public void unbindView(@NonNull SimpleItem item) {
            name.setText(null);
            description.setText(null);
        }
    }
}
