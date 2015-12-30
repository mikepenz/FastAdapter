package com.mikepenz.fastadapter.app.items;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.StringRes;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.mikepenz.fastadapter.ICollapsible;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.app.R;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.fastadapter.utils.FastAdapterUIUtils;
import com.mikepenz.fastadapter.utils.ViewHolderFactory;
import com.mikepenz.materialdrawer.holder.StringHolder;
import com.mikepenz.materialize.util.UIUtils;

import java.util.List;

/**
 * Created by mikepenz on 28.12.15.
 */
public class SampleItem extends AbstractItem<SampleItem> implements ICollapsible<SampleItem> {

    public String header;
    public StringHolder name;
    public StringHolder description;

    private List<IItem> mSubItems;
    private boolean mCollapsed = true;

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
    public boolean isCollapsed() {
        return mCollapsed;
    }

    @Override
    public SampleItem withCollapsed(boolean collapsed) {
        mCollapsed = collapsed;
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
        return R.id.fastadapter_sampleitem_id;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.sample_item;
    }

    @Override
    public void bindView(RecyclerView.ViewHolder holder) {
        Context ctx = holder.itemView.getContext();
        //get our viewHolder
        ViewHolder viewHolder = (ViewHolder) holder;

        //set the item selected if it is
        viewHolder.itemView.setSelected(isSelected());
        //set itself as tag. (not required)
        viewHolder.itemView.setTag(this);

        //set the background for the item
        UIUtils.setBackground(viewHolder.view, FastAdapterUIUtils.getSelectableBackground(ctx, Color.RED));
        //set the text for the name
        StringHolder.applyTo(name, viewHolder.name);
        //set the text for the description or hide
        StringHolder.applyToOrHide(description, viewHolder.description);
    }

    @Override
    public ViewHolderFactory getFactory() {
        return new ItemFactory();
    }


    public static class ItemFactory implements ViewHolderFactory<ViewHolder> {
        public ViewHolder factory(View v) {
            return new ViewHolder(v);
        }
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        protected View view;
        protected TextView name;
        protected TextView description;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            this.name = (TextView) view.findViewById(com.mikepenz.materialdrawer.R.id.material_drawer_name);
            this.description = (TextView) view.findViewById(com.mikepenz.materialdrawer.R.id.material_drawer_description);
        }
    }
}
