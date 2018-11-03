package com.mikepenz.fastadapter;

import android.view.View;

import com.mikepenz.fastadapter.items.AbstractItem;

import org.jetbrains.annotations.Nullable;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by fabianterhorst on 29.03.16.
 */
public class ExpandableTestItem extends AbstractItem<ExpandableTestItem.ViewHolder> implements IExpandable<ExpandableTestItem.ViewHolder> {

    private List<? extends ISubItem<?>> mSubItems;
    private IParentItem<?> mParent;
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
    public List<? extends ISubItem<?>> getSubItems() {
        return mSubItems;
    }

    @Override
    public boolean isAutoExpanding() {
        return true;
    }


    @Override
    public void setSubItems(@Nullable List<? extends ISubItem<?>> subItems) {
        this.mSubItems = subItems;
    }

    @Override
    public void setParent(@Nullable IParentItem<?> parent) {
        this.mParent = parent;
    }

    @Nullable
    @Override
    public IParentItem<?> getParent() {
        return mParent;
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
