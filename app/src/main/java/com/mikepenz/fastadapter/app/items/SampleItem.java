package com.mikepenz.fastadapter.app.items;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.StringRes;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.mikepenz.fastadapter.IExpandable;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.app.R;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.fastadapter.utils.FastAdapterUIUtils;
import com.mikepenz.fastadapter.utils.ViewHolderFactory;
import com.mikepenz.materialdrawer.holder.StringHolder;
import com.mikepenz.materialize.util.UIUtils;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by mikepenz on 28.12.15.
 */
public class SampleItem extends AbstractItem<SampleItem, SampleItem.ViewHolder> implements IExpandable<SampleItem, IItem> {
    //the static ViewHolderFactory which will be used to generate the ViewHolder for this Item
    private static final ViewHolderFactory<? extends ViewHolder> FACTORY = new ItemFactory();

    public String header;
    public StringHolder name;
    public StringHolder description;

    private List<IItem> mSubItems;
    private boolean mExpanded = false;

    public SampleItem withHeader(String header) {
        this.header = header;
        return this;
    }

    public SampleItem withName(String Name) {
        this.name = new StringHolder(Name);
        return this;
    }

    public SampleItem withName(@StringRes int NameRes) {
        this.name = new StringHolder(NameRes);
        return this;
    }

    public SampleItem withDescription(String description) {
        this.description = new StringHolder(description);
        return this;
    }

    public SampleItem withDescription(@StringRes int descriptionRes) {
        this.description = new StringHolder(descriptionRes);
        return this;
    }

    @Override
    public boolean isExpanded() {
        return mExpanded;
    }

    @Override
    public SampleItem withIsExpanded(boolean expaned) {
        mExpanded = expaned;
        return this;
    }

    @Override
    public List<IItem> getSubItems() {
        return mSubItems;
    }

    public SampleItem withSubItems(List<IItem> subItems) {
        this.mSubItems = subItems;
        return this;
    }

    @Override
    public int getType() {
        return R.id.fastadapter_sample_item_id;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.sample_item;
    }

    @Override
    public void bindView(ViewHolder viewHolder) {
        super.bindView(viewHolder);

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
    public ViewHolderFactory<? extends ViewHolder> getFactory() {
        return FACTORY;
    }

    protected static class ItemFactory implements ViewHolderFactory<ViewHolder> {
        public ViewHolder create(View v) {
            return new ViewHolder(v);
        }
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        protected View view;
        @Bind(R.id.material_drawer_name)
        TextView name;
        @Bind(R.id.material_drawer_description)
        TextView description;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            this.view = view;
        }
    }
}
