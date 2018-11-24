package com.mikepenz.fastadapter.ui.items;

import android.view.View;
import android.widget.ProgressBar;

import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.fastadapter.ui.R;
import com.mikepenz.fastadapter.ui.utils.FastAdapterUIUtils;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;


public class ProgressItem extends AbstractItem<ProgressItem.ViewHolder> {

    @Override
    public int getType() {
        return R.id.progress_item_id;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.progress_item;
    }

    @Override
    public void bindView(ViewHolder holder, List<Object> payloads) {
        super.bindView(holder, payloads);
        if (isEnabled()) {
            holder.itemView.setBackgroundResource(FastAdapterUIUtils.getSelectableBackground(holder.itemView.getContext()));
        }
    }

    @Override
    public void unbindView(ViewHolder holder) {

    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {

        protected ProgressBar progressBar;

        public ViewHolder(View view) {
            super(view);
            progressBar = view.findViewById(R.id.progress_bar);
        }
    }
}

