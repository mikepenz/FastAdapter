package com.mikepenz.fastadapter.listeners;

import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.R;

import java.util.List;

public class OnBindViewHolderListenerImpl implements OnBindViewHolderListener {
    /**
     * is called in onBindViewHolder to bind the data on the ViewHolder
     *
     * @param viewHolder the viewHolder for the type at this position
     * @param position   the position of this viewHolder
     * @param payloads   the payloads provided by the adapter
     */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position, List<Object> payloads) {
        Object tag = viewHolder.itemView.getTag(R.id.fastadapter_item_adapter);
        if (tag instanceof FastAdapter) {
            FastAdapter fastAdapter = ((FastAdapter) tag);
            IItem item = fastAdapter.getItem(position);
            if (item != null) {
                item.bindView(viewHolder, payloads);
                if (viewHolder instanceof FastAdapter.ViewHolder) {
                    ((FastAdapter.ViewHolder) viewHolder).bindView(item, payloads);
                }
                //set the R.id.fastadapter_item tag of this item to the item object (can be used when retrieving the view)
                viewHolder.itemView.setTag(R.id.fastadapter_item, item);
            }
        }
    }

    /**
     * is called in onViewRecycled to unbind the data on the ViewHolder
     *
     * @param viewHolder the viewHolder for the type at this position
     * @param position   the position of this viewHolder
     */
    @Override
    public void unBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        IItem item = (IItem) viewHolder.itemView.getTag(R.id.fastadapter_item);
        if (item != null) {
            item.unbindView(viewHolder);
            if (viewHolder instanceof FastAdapter.ViewHolder) {
                ((FastAdapter.ViewHolder) viewHolder).unbindView(item);
            }
            //remove set tag's
            viewHolder.itemView.setTag(R.id.fastadapter_item, null);
            viewHolder.itemView.setTag(R.id.fastadapter_item_adapter, null);
        } else {
            Log.e("FastAdapter", "The bindView method of this item should set the `Tag` on its itemView (https://github.com/mikepenz/FastAdapter/blob/develop/library-core/src/main/java/com/mikepenz/fastadapter/items/AbstractItem.java#L189)");
        }
    }

    /**
     * is called in onViewAttachedToWindow when the view is detached from the window
     *
     * @param viewHolder the viewHolder for the type at this position
     * @param position   the position of this viewHolder
     */
    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder viewHolder, int position) {
        IItem item = FastAdapter.getHolderAdapterItem(viewHolder, position);
        if (item != null) {
            try {
                item.attachToWindow(viewHolder);
                if (viewHolder instanceof FastAdapter.ViewHolder) {
                    ((FastAdapter.ViewHolder) viewHolder).attachToWindow(item);
                }
            } catch (AbstractMethodError e) {
                Log.e("FastAdapter", e.toString());
            }
        }
    }

    /**
     * is called in onViewDetachedFromWindow when the view is detached from the window
     *
     * @param viewHolder the viewHolder for the type at this position
     * @param position   the position of this viewHolder
     */
    @Override
    public void onViewDetachedFromWindow(RecyclerView.ViewHolder viewHolder, int position) {
        IItem item = FastAdapter.getHolderAdapterItem(viewHolder, position);
        if (item != null) {
            item.detachFromWindow(viewHolder);
            if (viewHolder instanceof FastAdapter.ViewHolder) {
                ((FastAdapter.ViewHolder) viewHolder).detachFromWindow(item);
            }
        }
    }

    /**
     * is called when the ViewHolder is in a transient state. return true if you want to reuse
     * that view anyways
     *
     * @param viewHolder the viewHolder for the view which failed to recycle
     * @param position   the position of this viewHolder
     * @return true if we want to recycle anyways (false - it get's destroyed)
     */
    @Override
    public boolean onFailedToRecycleView(RecyclerView.ViewHolder viewHolder, int position) {
        IItem item = (IItem) viewHolder.itemView.getTag(R.id.fastadapter_item);
        if (item != null) {
            boolean recycle = item.failedToRecycle(viewHolder);
            if (viewHolder instanceof FastAdapter.ViewHolder) {
                recycle = recycle || ((FastAdapter.ViewHolder) viewHolder).failedToRecycle(item);
            }
            return recycle;
        }
        return false;
    }
}
