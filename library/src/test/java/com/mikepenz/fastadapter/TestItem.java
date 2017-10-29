package com.mikepenz.fastadapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.mikepenz.fastadapter.items.AbstractItem;

/**
 * Created by fabianterhorst on 29.03.16.
 */
public class TestItem extends AbstractItem<TestItem, TestItem.ViewHolder> {

    @Override
    public int getLayoutRes() {
        return -1;
    }

    @Override
    public int getType() {
        return -1;
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
