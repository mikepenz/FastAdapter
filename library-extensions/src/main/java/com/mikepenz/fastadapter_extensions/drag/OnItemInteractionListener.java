package com.mikepenz.fastadapter_extensions.drag;

import android.support.v7.widget.RecyclerView;

import java.util.List;

public interface OnItemInteractionListener {
    void onLongItemClicked(RecyclerView recyclerView, RecyclerView.ViewHolder mViewHolderTouched, int position);

    void onItemClicked(RecyclerView recyclerView, RecyclerView.ViewHolder mViewHolderTouched, int position);

    void onViewHolderHovered(RecyclerView rv, RecyclerView.ViewHolder viewHolder);

    void onMultipleViewHoldersSelected(RecyclerView rv, List<RecyclerView.ViewHolder> selection);
}
