package com.mikepenz.fastadapter;

import androidx.recyclerview.widget.RecyclerView;

import android.view.View;

import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.List;

/**
 * Created by fabianterhorst on 29.03.16.
 */
public class TestItem extends AbstractItem<TestItem.ViewHolder> implements IExpandable<TestItem, TestItem, TestItem.ViewHolder>, ISubItem<TestItem, TestItem.ViewHolder> {

    private List<TestItem> mSubItems;
    private TestItem mParent;
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
    public List<TestItem> getSubItems() {
        return mSubItems;
    }

    @Override
    public boolean isAutoExpanding() {
        return true;
    }

    @Override
    public void setSubItems(List<TestItem> subItems) {
        this.mSubItems = subItems;
    }

    @Override
    public TestItem getParent() {
        return mParent;
    }

    @Override
    public void setParent(TestItem parent) {
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
