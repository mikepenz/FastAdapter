package com.mikepenz.fastadapter;

import androidx.recyclerview.widget.RecyclerView;

import android.view.View;

import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.List;

/**
 * Created by fabianterhorst on 29.03.16.
 */
public class ExpandableTestItem extends AbstractItem<ExpandableTestItem.ViewHolder> implements IExpandable<ExpandableTestItem, ExpandableTestItem, ExpandableTestItem.ViewHolder>, ISubItem<ExpandableTestItem, ExpandableTestItem.ViewHolder> {

    private List<ExpandableTestItem> mSubItems;
    private ExpandableTestItem mParent;
    private boolean mExpanded = false;

    @Override
    public int getLayoutRes() {
        return -1;
    }

    @Override
    public int getType() {
        return -1;
    }

    @Override
    public boolean isExpanded() {
        return mExpanded;
    }

    @Override
    public void setExpanded(boolean expanded) {
        mExpanded = expanded;
    }

    @Override
    public List<ExpandableTestItem> getSubItems() {
        return mSubItems;
    }

    @Override
    public boolean isAutoExpanding() {
        return true;
    }

    @Override
    public void setSubItems(List<ExpandableTestItem> subItems) {
        this.mSubItems = subItems;
    }

    @Override
    public ExpandableTestItem getParent() {
        return mParent;
    }

    @Override
    public void setParent(ExpandableTestItem parent) {
        this.mParent = parent;
    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View view) {
            super(view);
        }
    }
}
