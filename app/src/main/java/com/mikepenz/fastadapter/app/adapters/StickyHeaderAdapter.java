package com.mikepenz.fastadapter.app.adapters;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.app.R;
import com.mikepenz.fastadapter.app.items.SimpleItem;
import com.mikepenz.fastadapter.app.items.expandable.SimpleSubExpandableItem;
import com.mikepenz.fastadapter.app.items.expandable.SimpleSubItem;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

import java.security.SecureRandom;
import java.util.List;

/**
 * Created by mikepenz on 30.12.15.
 * This is a FastAdapter adapter implementation for the awesome Sticky-Headers lib by timehop
 * https://github.com/timehop/sticky-headers-recyclerview
 */
public class StickyHeaderAdapter<Item extends IItem> extends RecyclerView.Adapter implements StickyRecyclerHeadersAdapter {
    @Override
    public long getHeaderId(int position) {
        IItem item = getItem(position);

        //in our sample we want a separate header per first letter of our items
        //this if is not necessary for your code, we only use it as this sticky header is reused for different item implementations
        if (item instanceof SimpleItem && ((SimpleItem) item).header != null) {
            return ((SimpleItem) item).header.charAt(0);
        } else if (item instanceof SimpleSubItem && ((SimpleSubItem) item).header != null) {
            return ((SimpleSubItem) item).header.charAt(0);
        } else if (item instanceof SimpleSubExpandableItem && ((SimpleSubExpandableItem) item).header != null) {
            return ((SimpleSubExpandableItem) item).header.charAt(0);
        }
        return -1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        //we create the view for the header
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_header, parent, false);
        return new RecyclerView.ViewHolder(view) {
        };
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {
        TextView textView = (TextView) holder.itemView;

        IItem item = getItem(position);
        if (item instanceof SimpleItem && ((SimpleItem) item).header != null) {
            //based on the position we set the headers text
            textView.setText(String.valueOf(((SimpleItem) item).header.charAt(0)));
        } else if (item instanceof SimpleSubItem && ((SimpleSubItem) item).header != null) {
            //based on the position we set the headers text
            textView.setText(String.valueOf(((SimpleSubItem) item).header.charAt(0)));
        } else if (item instanceof SimpleSubExpandableItem && ((SimpleSubExpandableItem) item).header != null) {
            //based on the position we set the headers text
            textView.setText(String.valueOf(((SimpleSubExpandableItem) item).header.charAt(0)));
        }
        holder.itemView.setBackgroundColor(getRandomColor());
    }

    //just to prettify things a bit
    private int getRandomColor() {
        SecureRandom rgen = new SecureRandom();
        return Color.HSVToColor(150, new float[]{
                rgen.nextInt(359), 1, 1
        });
    }

    /*
    * GENERAL CODE NEEDED TO WRAP AN ADAPTER
     */

    //private AbstractAdapter mParentAdapter;
    //keep a reference to the FastAdapter which contains the base logic
    private FastAdapter<Item> mFastAdapter;

    /**
     * Wrap the FastAdapter with this AbstractAdapter and keep its reference to forward all events correctly
     *
     * @param fastAdapter the FastAdapter which contains the base logic
     * @return this
     */
    public StickyHeaderAdapter<Item> wrap(FastAdapter fastAdapter) {
        //this.mParentAdapter = abstractAdapter;
        this.mFastAdapter = fastAdapter;
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
        if (mFastAdapter != null) {
            mFastAdapter.registerAdapterDataObserver(observer);
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
        if (mFastAdapter != null) {
            mFastAdapter.unregisterAdapterDataObserver(observer);
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
        return mFastAdapter.getItemViewType(position);
    }

    /**
     * overwrite the getItemId to correctly return the value from the FastAdapter
     *
     * @param position
     * @return
     */
    @Override
    public long getItemId(int position) {
        return mFastAdapter.getItemId(position);
    }

    /**
     * @return the reference to the FastAdapter
     */
    public FastAdapter<Item> getFastAdapter() {
        return mFastAdapter;
    }

    /**
     * make sure we return the Item from the FastAdapter so we retrieve the item from all adapters
     *
     * @param position
     * @return
     */
    public Item getItem(int position) {
        return mFastAdapter.getItem(position);
    }

    /**
     * make sure we return the count from the FastAdapter so we retrieve the count from all adapters
     *
     * @return
     */
    @Override
    public int getItemCount() {
        return mFastAdapter.getItemCount();
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
        return mFastAdapter.onCreateViewHolder(parent, viewType);
    }

    /**
     * the onBindViewHolder is managed by the FastAdapter so forward this correctly
     *
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        mFastAdapter.onBindViewHolder(holder, position);
    }

    /**
     * the onBindViewHolder is managed by the FastAdapter so forward this correctly
     *
     * @param holder
     * @param position
     * @param payloads
     */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position, List payloads) {
        mFastAdapter.onBindViewHolder(holder, position, payloads);
    }

    /**
     * the setHasStableIds is managed by the FastAdapter so forward this correctly
     *
     * @param hasStableIds
     */
    @Override
    public void setHasStableIds(boolean hasStableIds) {
        mFastAdapter.setHasStableIds(hasStableIds);
    }

    /**
     * the onViewRecycled is managed by the FastAdapter so forward this correctly
     *
     * @param holder
     */
    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder) {
        mFastAdapter.onViewRecycled(holder);
    }

    /**
     * the onFailedToRecycleView is managed by the FastAdapter so forward this correctly
     *
     * @param holder
     * @return
     */
    @Override
    public boolean onFailedToRecycleView(RecyclerView.ViewHolder holder) {
        return mFastAdapter.onFailedToRecycleView(holder);
    }

    /**
     * the onViewDetachedFromWindow is managed by the FastAdapter so forward this correctly
     *
     * @param holder
     */
    @Override
    public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
        mFastAdapter.onViewDetachedFromWindow(holder);
    }

    /**
     * the onViewAttachedToWindow is managed by the FastAdapter so forward this correctly
     *
     * @param holder
     */
    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        mFastAdapter.onViewAttachedToWindow(holder);
    }

    /**
     * the onAttachedToRecyclerView is managed by the FastAdapter so forward this correctly
     *
     * @param recyclerView
     */
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        mFastAdapter.onAttachedToRecyclerView(recyclerView);
    }

    /**
     * the onDetachedFromRecyclerView is managed by the FastAdapter so forward this correctly
     *
     * @param recyclerView
     */
    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        mFastAdapter.onDetachedFromRecyclerView(recyclerView);
    }
}
