package com.mikepenz.fastadapter.commons.utils;

import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;
import android.support.v7.util.ListUpdateCallback;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.adapters.ModelAdapter;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;

import java.util.List;

/**
 * Created by mikepenz on 28.10.16.
 */

public class FastAdapterDiffUtil {

    public static <A extends ModelAdapter<Model, Item>, Model, Item extends IItem> DiffUtil.DiffResult calculateDiff(final A adapter, final List<Item> items, final DiffCallback<Item> callback, final boolean detectMoves) {
        if (adapter.isUseIdDistributor()) {
            adapter.getIdDistributor().checkIds(items);
        }

        //map the types
        adapter.mapPossibleTypes(items);

        //remember the old items
        final List<Item> oldItems = adapter.getAdapterItems();

        DiffUtil.DiffResult result = DiffUtil.calculateDiff(new FastAdapterCallback<>(oldItems, items, callback), detectMoves);

        //make sure the new items list is not a reference of the already mItems list
        if (items != oldItems) {
            //remove all previous items
            if (!oldItems.isEmpty()) {
                oldItems.clear();
            }

            //add all new items to the list
            oldItems.addAll(items);
        }

        return result;
    }


    public static <A extends ModelAdapter<Model, Item>, Model, Item extends IItem> A set(final A adapter, DiffUtil.DiffResult result) {
        result.dispatchUpdatesTo(new FastAdapterListUpdateCallback<>(adapter));
        return adapter;
    }

    public static <A extends ModelAdapter<Model, Item>, Model, Item extends IItem> DiffUtil.DiffResult calculateDiff(final A adapter, final List<Item> items, final DiffCallback<Item> callback) {
        return calculateDiff(adapter, items, callback, true);
    }

    public static <A extends ModelAdapter<Model, Item>, Model, Item extends IItem> DiffUtil.DiffResult calculateDiff(final A adapter, final List<Item> items, final boolean detectMoves) {
        return calculateDiff(adapter, items, new DiffCallbackImpl<Item>(), detectMoves);
    }

    public static <A extends ModelAdapter<Model, Item>, Model, Item extends IItem> DiffUtil.DiffResult calculateDiff(final A adapter, final List<Item> items) {
        return calculateDiff(adapter, items, new DiffCallbackImpl<Item>(), true);
    }

    public static <A extends ModelAdapter<Model, Item>, Model, Item extends IItem> A set(final A adapter, final List<Item> items, final DiffCallback<Item> callback, final boolean detectMoves) {
        DiffUtil.DiffResult result = calculateDiff(adapter, items, callback, detectMoves);
        return set(adapter, result);
    }

    public static <A extends ModelAdapter<Model, Item>, Model, Item extends IItem> A set(final A adapter, final List<Item> items, final DiffCallback<Item> callback) {
        return set(adapter, items, callback, true);
    }

    public static <A extends ModelAdapter<Model, Item>, Model, Item extends IItem> A set(final A adapter, final List<Item> items, final boolean detectMoves) {
        return set(adapter, items, new DiffCallbackImpl<Item>(), detectMoves);
    }

    public static <A extends ModelAdapter<Model, Item>, Model, Item extends IItem> A set(final A adapter, final List<Item> items) {
        return set(adapter, items, new DiffCallbackImpl<Item>());
    }

    public static <A extends FastItemAdapter<Item>, Item extends IItem> DiffUtil.DiffResult calculateDiff(final A adapter, final List<Item> items, final DiffCallback<Item> callback) {
        return calculateDiff(adapter.getItemAdapter(), items, callback);
    }

    public static <A extends FastItemAdapter<Item>, Item extends IItem> DiffUtil.DiffResult calculateDiff(final A adapter, final List<Item> items, final boolean detectMoves) {
        return calculateDiff(adapter.getItemAdapter(), items, detectMoves);
    }

    public static <A extends FastItemAdapter<Item>, Item extends IItem> DiffUtil.DiffResult calculateDiff(final A adapter, final List<Item> items) {
        return calculateDiff(adapter.getItemAdapter(), items);
    }

    public static <A extends FastItemAdapter<Item>, Item extends IItem> A set(final A adapter, final List<Item> items, final DiffCallback<Item> callback, final boolean detectMoves) {
        set(adapter.getItemAdapter(), items, callback, detectMoves);
        return adapter;
    }

    public static <A extends FastItemAdapter<Item>, Item extends IItem> A set(final A adapter, final List<Item> items, final DiffCallback<Item> callback) {
        set(adapter.getItemAdapter(), items, callback);
        return adapter;
    }

    public static <A extends FastItemAdapter<Item>, Item extends IItem> A set(final A adapter, final List<Item> items, final boolean detectMoves) {
        set(adapter.getItemAdapter(), items, detectMoves);
        return adapter;
    }

    public static <A extends FastItemAdapter<Item>, Item extends IItem> A set(final A adapter, final List<Item> items) {
        return set(adapter, items, new DiffCallbackImpl<Item>());
    }

    public static <A extends FastItemAdapter<Item>, Item extends IItem> A set(final A adapter, DiffUtil.DiffResult result) {
        set(adapter.getItemAdapter(), result);
        return adapter;
    }

    private static final class FastAdapterCallback<Item extends IItem> extends DiffUtil.Callback {

        private final List<Item> oldItems;
        private final List<Item> items;
        private final DiffCallback<Item> callback;

        FastAdapterCallback(List<Item> oldItems, List<Item> items, DiffCallback<Item> callback) {
            this.oldItems = oldItems;
            this.items = items;
            this.callback = callback;
        }

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
    }

    private static final class FastAdapterListUpdateCallback<A extends ModelAdapter<Model, Item>, Model, Item extends IItem> implements ListUpdateCallback {

        private final A adapter;

        FastAdapterListUpdateCallback(A adapter) {
            this.adapter = adapter;
        }

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
    }
}