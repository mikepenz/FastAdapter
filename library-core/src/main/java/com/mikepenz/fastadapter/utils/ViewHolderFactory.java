package com.mikepenz.fastadapter.utils;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * the interfaces for the ViewHolderFactory
 *
 * @param <T>
 */
public interface ViewHolderFactory<T extends RecyclerView.ViewHolder> {
    T create(View v);
}