package com.mikepenz.fastadapter_extensions.items;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.fastadapter.utils.ViewHolderFactory;
import com.mikepenz.library_extensions.R;


public class ProgressItem extends AbstractItem<ProgressItem, ProgressItem.ViewHolder> {

    private static final ViewHolderFactory<? extends ViewHolder> FACTORY = new ItemFactory();

    @Override
    public int getType() {
        return R.id.progress_item_id;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.progress_item;
    }

    @Override
    public void bindView(ViewHolder holder) {
        super.bindView(holder);
    }

    protected static class ItemFactory implements ViewHolderFactory<ViewHolder> {
        public ViewHolder create(View v) {
            return new ViewHolder(v);
        }
    }

    @Override
    public ViewHolderFactory<? extends ViewHolder> getFactory() {
        return FACTORY;
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {

        protected ProgressBar progressBar;

        public ViewHolder(View view) {
            super(view);
            progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
        }
    }
}

