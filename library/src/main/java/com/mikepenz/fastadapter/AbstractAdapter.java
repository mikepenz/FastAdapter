package com.mikepenz.fastadapter;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

/**
 * Created by mikepenz on 27.12.15.
 */
public abstract class AbstractAdapter extends RecyclerView.Adapter implements IAdapter {
    //private AbstractAdapter mParentAdapter;
    //keep a reference to the BaseAdapter which contains the base logic
    private FastAdapter mBaseAdapter;

    /**
     * Wrap the FastAdapter with this AbstractAdapter and keep it's reference to forward all events correctly
     *
     * @param baseAdapter the FastAdapter which contains the base logic
     * @return this
     */
    public AbstractAdapter wrap(FastAdapter baseAdapter) {
        //this.mParentAdapter = abstractAdapter;
        this.mBaseAdapter = baseAdapter;
        this.mBaseAdapter.registerAdapter(this);
        return this;
    }

    /**
     * Wrap the AbstractAdapter with this AbstractAdapter and keep the reference to it's FastAdapter to which we forward all events correctly
     *
     * @param abstractAdapter an AbstractWrapper which wraps another AbstractAdapter or FastAdapter
     * @return this
     */
    public AbstractAdapter wrap(AbstractAdapter abstractAdapter) {
        //this.mParentAdapter = abstractAdapter;
        this.mBaseAdapter = abstractAdapter.getBaseAdapter();
        this.mBaseAdapter.registerAdapter(this);
        return this;
    }

    /**
     * overwrite the registerAdapterDataObserver to correctly forward all events to the FastAdapter
     *
     * @param observer
     */
    @Override
    public void registerAdapterDataObserver(RecyclerView.AdapterDataObserver observer) {
        super.registerAdapterDataObserver(observer);
        if (mBaseAdapter != null) {
            mBaseAdapter.registerAdapterDataObserver(observer);
        }
    }

    /**
     * overwrite the unregisterAdapterDataObserver to correctly forward all events to the FastAdapter
     *
     * @param observer
     */
    @Override
    public void unregisterAdapterDataObserver(RecyclerView.AdapterDataObserver observer) {
        super.unregisterAdapterDataObserver(observer);
        if (mBaseAdapter != null) {
            mBaseAdapter.unregisterAdapterDataObserver(observer);
        }
    }

    /**
     * overwrite the getItemViewType to correctly return the value from the FastAdapter
     *
     * @param position
     * @return
     */
    @Override
    public int getItemViewType(int position) {
        return mBaseAdapter.getItemViewType(position);
    }

    /**
     * overwrite the getItemId to correctly return the value from the FastAdapter
     *
     * @param position
     * @return
     */
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

    /**
     * @return the reference to the FastAdapter
     */
    @Override
    public FastAdapter getBaseAdapter() {
        return mBaseAdapter;
    }

    /**
     * make sure we return the Item from the FastAdapter so we retrieve the item from all adapters
     *
     * @param position
     * @return
     */
    @Override
    public IItem getItem(int position) {
        return mBaseAdapter.getItem(position);
    }

    /**
     * make sure we return the count from the FastAdapter so we retrieve the count from all adapters
     *
     * @return
     */
    @Override
    public int getItemCount() {
        return mBaseAdapter.getItemCount();
    }

    /**
     * the onCreateViewHolder is managed by the FastAdapter so forward this correctly
     *
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return mBaseAdapter.onCreateViewHolder(parent, viewType);
    }

    /**
     * the onBindViewHolder is managed by the FastAdapter so forward this correctly
     *
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        mBaseAdapter.onBindViewHolder(holder, position);
    }

    /**
     * internal mapper to remember and add possible types for the RecyclerView
     *
     * @param items
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
     *
     * @param item
     */
    public void mapPossibleType(IItem item) {
        mBaseAdapter.registerTypeInstance(item);
    }
}
