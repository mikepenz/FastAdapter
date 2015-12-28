package com.mikepenz.fastadapter;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

/**
 * Created by mikepenz on 27.12.15.
 */
public abstract class AbstractAdapter extends RecyclerView.Adapter implements IAdapter {
    //private AbstractAdapter mParentAdapter;
    private FastAdapter mBaseAdapter;

    public AbstractAdapter wrap(FastAdapter baseAdapter) {
        //this.mParentAdapter = abstractAdapter;
        this.mBaseAdapter = baseAdapter;
        this.mBaseAdapter.registerAdapter(this);
        return this;
    }

    public AbstractAdapter wrap(AbstractAdapter abstractAdapter) {
        //this.mParentAdapter = abstractAdapter;
        this.mBaseAdapter = abstractAdapter.getBaseAdapter();
        this.mBaseAdapter.registerAdapter(this);
        return this;
    }

    @Override
    public void registerAdapterDataObserver(RecyclerView.AdapterDataObserver observer) {
        super.registerAdapterDataObserver(observer);
        if (mBaseAdapter != null) {
            mBaseAdapter.registerAdapterDataObserver(observer);
        }
    }

    @Override
    public void unregisterAdapterDataObserver(RecyclerView.AdapterDataObserver observer) {
        super.unregisterAdapterDataObserver(observer);
        if (mBaseAdapter != null) {
            mBaseAdapter.unregisterAdapterDataObserver(observer);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return mBaseAdapter.getItemViewType(position);
    }

    @Override
    public long getItemId(int position) {
        return mBaseAdapter.getItemId(position);
    }

    /*
    @Override
    public AbstractAdapter getParentAdapter() {
        //return mParentAdapter;
        return null;
    }
    */

    @Override
    public FastAdapter getBaseAdapter() {
        return mBaseAdapter;
    }

    @Override
    public void setBaseAdapter(FastAdapter baseAdapter) {
        this.mBaseAdapter = baseAdapter;
    }

    @Override
    public IItem getItem(int position) {
        return mBaseAdapter.getItem(position);
    }

    @Override
    public int getItemCount() {
        return mBaseAdapter.getItemCount();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return mBaseAdapter.onCreateViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        mBaseAdapter.onBindViewHolder(holder, position);
    }


    /**
     * internal mapper to remember and add possible types for the RecyclerView
     */
    public void mapPossibleTypes(Iterable<IItem> items) {
        if (items != null) {
            for (IItem item : items) {
                mapPossibleType(item);
            }
        }
    }

    /**
     * internal mapper to remember and add possible types for the RecyclerView
     */
    public void mapPossibleType(IItem item) {
        mBaseAdapter.registerTypeInstance(item);
    }
}
