package com.mikepenz.fastadapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

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
    public View createView(final Context ctx, @Nullable final ViewGroup parent) {
        return new FrameLayout(ctx);
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
