package com.mikepenz.fastadapter.commons.adapters;

import androidx.recyclerview.widget.RecyclerView;
import android.view.ViewGroup;

import com.mikepenz.fastadapter.IItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mikepenz on 03.03.16.
 */
public abstract class AbstractWrapAdapter<Item extends IItem> extends RecyclerView.Adapter {
    //the items handled and managed by this item
    private List<Item> mItems = new ArrayList<>();

    //private AbstractAdapter mParentAdapter;
    //keep a reference to the FastAdapter which contains the base logic
    private RecyclerView.Adapter mAdapter;

    public AbstractWrapAdapter(List<Item> items) {
        this.mItems = items;
    }

    public List<Item> getItems() {
        return mItems;
    }

    public void setItems(List<Item> items) {
        this.mItems = items;
    }

    /**
     * Wrap the FastAdapter with this AbstractAdapter and keep its reference to forward all events correctly
     *
     * @param adapter the FastAdapter which contains the base logic
     * @return this
     */
    public AbstractWrapAdapter wrap(RecyclerView.Adapter adapter) {
        //this.mParentAdapter = abstractAdapter;
        this.mAdapter = adapter;
        return this;
    }

    /**
     * this method states if we should insert a custom element at the vien position
     *
     * @param position
     * @return
     */
    public abstract boolean shouldInsertItemAtPosition(int position);

    /**
     * this method calculates how many elements were already inserted before this position;
     *
     * @param position
     * @return
     */
    public abstract int itemInsertedBeforeCount(int position);


    /**
     * overwrite the registerAdapterDataObserver to correctly forward all events to the FastAdapter
     *
     * @param observer
     */
    @Override
    public void registerAdapterDataObserver(RecyclerView.AdapterDataObserver observer) {
        super.registerAdapterDataObserver(observer);
        if (mAdapter != null) {
            mAdapter.registerAdapterDataObserver(observer);
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
        if (mAdapter != null) {
            mAdapter.unregisterAdapterDataObserver(observer);
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
        if (shouldInsertItemAtPosition(position)) {
            return getItem(position).getType();
        } else {
            return mAdapter.getItemViewType(position - itemInsertedBeforeCount(position));
        }
    }

    /**
     * overwrite the getItemId to correctly return the value from the FastAdapter
     *
     * @param position
     * @return
     */
    @Override
    public long getItemId(int position) {
        if (shouldInsertItemAtPosition(position)) {
            return getItem(position).getIdentifier();
        } else {
            return mAdapter.getItemId(position - itemInsertedBeforeCount(position));
        }
    }

    /**
     * @return the reference to the FastAdapter
     */
    public RecyclerView.Adapter getAdapter() {
        return mAdapter;
    }

    /**
     * make sure we return the Item from the FastAdapter so we retrieve the item from all adapters
     *
     * @param position
     * @return
     */
    public Item getItem(int position) {
        if (shouldInsertItemAtPosition(position)) {
            return mItems.get(itemInsertedBeforeCount(position - 1));
        }
        return null;
    }

    /**
     * make sure we return the count from the FastAdapter so we retrieve the count from all adapters
     *
     * @return
     */
    @Override
    public int getItemCount() {
        int itemCount = mAdapter.getItemCount();
        return itemCount + itemInsertedBeforeCount(itemCount);
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
        //TODO OPTIMIZE
        for (Item item : mItems) {
            if (item.getType() == viewType) {
                return item.getViewHolder(parent);
            }
        }
        return mAdapter.onCreateViewHolder(parent, viewType);
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        //empty implementation as the one with the List payloads is already called
    }

    /**
     * the onBindViewHolder is managed by the FastAdapter so forward this correctly
     *
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position, List payloads) {
        if (shouldInsertItemAtPosition(position)) {
            getItem(position).bindView(holder, payloads);
        } else {
            mAdapter.onBindViewHolder(holder, position - itemInsertedBeforeCount(position), payloads);
        }
    }

    /**
     * the setHasStableIds is managed by the FastAdapter so forward this correctly
     *
     * @param hasStableIds
     */
    @Override
    public void setHasStableIds(boolean hasStableIds) {
        mAdapter.setHasStableIds(hasStableIds);
    }

    /**
     * the onViewRecycled is managed by the FastAdapter so forward this correctly
     *
     * @param holder
     */
    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder) {
        mAdapter.onViewRecycled(holder);
    }

    /**
     * the onFailedToRecycleView is managed by the FastAdapter so forward this correctly
     *
     * @param holder
     * @return
     */
    @Override
    public boolean onFailedToRecycleView(RecyclerView.ViewHolder holder) {
        return mAdapter.onFailedToRecycleView(holder);
    }

    /**
     * the onViewDetachedFromWindow is managed by the FastAdapter so forward this correctly
     *
     * @param holder
     */
    @Override
    public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
        mAdapter.onViewDetachedFromWindow(holder);
    }

    /**
     * the onViewAttachedToWindow is managed by the FastAdapter so forward this correctly
     *
     * @param holder
     */
    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        mAdapter.onViewAttachedToWindow(holder);
    }

    /**
     * the onAttachedToRecyclerView is managed by the FastAdapter so forward this correctly
     *
     * @param recyclerView
     */
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        mAdapter.onAttachedToRecyclerView(recyclerView);
    }

    /**
     * the onDetachedFromRecyclerView is managed by the FastAdapter so forward this correctly
     *
     * @param recyclerView
     */
    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        mAdapter.onDetachedFromRecyclerView(recyclerView);
    }
}
