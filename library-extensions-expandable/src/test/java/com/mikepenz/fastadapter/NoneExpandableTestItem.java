package com.mikepenz.fastadapter;

import android.view.View;

import com.mikepenz.fastadapter.items.AbstractItem;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by fabianterhorst on 29.03.16.
 */
public class NoneExpandableTestItem extends AbstractItem<NoneExpandableTestItem.ViewHolder> {

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
