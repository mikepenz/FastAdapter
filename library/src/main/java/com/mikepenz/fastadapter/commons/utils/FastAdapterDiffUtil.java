package com.mikepenz.fastadapter.commons.utils;

import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;
import android.support.v7.util.ListUpdateCallback;

import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.adapters.ItemAdapter;

import java.util.Collections;
import java.util.List;

/**
 * Created by mikepenz on 28.10.16.
 */

public class FastAdapterDiffUtil {
    public static <A extends ItemAdapter<Item>, Item extends IItem> A set(final A adapter, final List<Item> items, final DiffCallback<Item> callback, final boolean detectMoves) {
        if (adapter.isUseIdDistributor()) {
            adapter.getIdDistributor().checkIds(items);
        }

        //if we have a comparator then sort
        if (adapter.getComparator() != null) {
            Collections.sort(items, adapter.getComparator());
        }

        //map the types
        adapter.mapPossibleTypes(items);

        //remember the old items
        final List<Item> oldItems = adapter.getAdapterItems();

        DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override
            public int getOldListSize() {
                return oldItems.size();
            }

            @Override
            public int getNewListSize() {
                return items.size();
            }

            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                return callback.areItemsTheSame(oldItems.get(oldItemPosition), items.get(newItemPosition));
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                return callback.areContentsTheSame(oldItems.get(oldItemPosition), items.get(newItemPosition));
            }

            @Nullable
            @Override
            public Object getChangePayload(int oldItemPosition, int newItemPosition) {
                Object result = callback.getChangePayload(oldItems.get(oldItemPosition), oldItemPosition, items.get(newItemPosition), newItemPosition);
                return result == null ? super.getChangePayload(oldItemPosition, newItemPosition) : result;
            }
        }, detectMoves);

        //make sure the new items list is not a reference of the already mItems list
        if (items != oldItems) {
            //remove all previous items
            if (!oldItems.isEmpty()) {
                oldItems.clear();
            }

            //add all new items to the list
            oldItems.addAll(items);
        }

        result.dispatchUpdatesTo(new ListUpdateCallback() {
            @Override
            public void onInserted(int position, int count) {
                adapter.getFastAdapter().notifyAdapterItemRangeInserted(adapter.getFastAdapter().getPreItemCountByOrder(adapter.getOrder()) + position, count);
            }

            @Override
            public void onRemoved(int position, int count) {
                adapter.getFastAdapter().notifyAdapterItemRangeRemoved(adapter.getFastAdapter().getPreItemCountByOrder(adapter.getOrder()) + position, count);
            }

            @Override
            public void onMoved(int fromPosition, int toPosition) {
                adapter.getFastAdapter().notifyAdapterItemMoved(adapter.getFastAdapter().getPreItemCountByOrder(adapter.getOrder()) + fromPosition, toPosition);
            }

            @Override
            public void onChanged(int position, int count, Object payload) {
                adapter.getFastAdapter().notifyAdapterItemRangeChanged(adapter.getFastAdapter().getPreItemCountByOrder(adapter.getOrder()) + position, count, payload);
            }
        });

        return adapter;
    }
}
