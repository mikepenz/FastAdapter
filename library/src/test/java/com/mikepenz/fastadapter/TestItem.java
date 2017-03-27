package com.mikepenz.fastadapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.List;

/**
 * Created by fabianterhorst on 29.03.16.
 */
public class TestItem extends AbstractItem<TestItem, TestItem.ViewHolder> implements IExpandable<TestItem, TestItem>, ISubItem<TestItem, TestItem> {

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
    public TestItem withIsExpanded(boolean expanded) {
        mExpanded = expanded;
        return this;
    }

    @Override
    public List<TestItem> getSubItems() {
        return mSubItems;
    }

    @Override
    public boolean isAutoExpanding() {
        return true;
    }

    public TestItem withSubItems(List<TestItem> subItems) {
        this.mSubItems = subItems;
        return this;
    }

    @Override
    public TestItem getParent() {
        return mParent;
    }

    @Override
    public TestItem withParent(TestItem parent) {
        this.mParent = parent;
        return this;
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
