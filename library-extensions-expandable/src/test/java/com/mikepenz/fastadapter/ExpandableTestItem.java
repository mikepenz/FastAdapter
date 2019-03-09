package com.mikepenz.fastadapter;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.mikepenz.fastadapter.items.AbstractItem;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fabianterhorst on 29.03.16.
 */
public class ExpandableTestItem extends AbstractItem<ExpandableTestItem.ViewHolder> implements IExpandable<ExpandableTestItem.ViewHolder> {

    private List<ISubItem<?>> mSubItems = new ArrayList<>();
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

    @NotNull
    @Override
    public List<ISubItem<?>> getSubItems() {
        return mSubItems;
    }

    @Override
    public void setSubItems(@NotNull List<ISubItem<?>> list) {
        this.mSubItems = list;
    }

    @Override
    public boolean isAutoExpanding() {
        return true;
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
