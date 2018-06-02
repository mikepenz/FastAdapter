package com.mikepenz.fastadapter.commons.utils;

import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;
import android.support.v7.util.ListUpdateCallback;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapterExtension;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.adapters.ModelAdapter;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.utils.ComparableItemListImpl;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by mikepenz on 28.10.16.
 */

public class FastAdapterDiffUtil {

    /**
     * This method will compute a {@link android.support.v7.util.DiffUtil.DiffResult} based on the given adapter, and the list of new items.
     * <p>
     * It automatically collapses all expandables (if enabled) as they are not supported by the diff util,
     * pre sort the items based on the comparator if available,
     * map the new item types for the FastAdapter then calculates the {@link android.support.v7.util.DiffUtil.DiffResult} using the {@link DiffUtil}.
     * <p>
     * As the last step it will replace the items inside the adapter with the new set of items provided.
     *
     * @param adapter     the adapter containing the current items.
     * @param items       the new set of items we want to put into the adapter
     * @param callback    the callback used to implement the required checks to identify changes of items.
     * @param detectMoves configuration for the {@link DiffUtil#calculateDiff(DiffUtil.Callback, boolean)} method
     * @param <A>         The adapter type, whereas A extends {@link ModelAdapter}
     * @param <Model>     The model type we work with
     * @param <Item>      The item type kept in the adapter
     * @return the {@link android.support.v7.util.DiffUtil.DiffResult} computed.
     */
    public static <A extends ModelAdapter<Model, Item>, Model, Item extends IItem> DiffUtil.DiffResult calculateDiff(final A adapter, final List<Item> items, final DiffCallback<Item> callback, final boolean detectMoves) {
        if (adapter.isUseIdDistributor()) {
            adapter.getIdDistributor().checkIds(items);
        }

        // The FastAdapterDiffUtil does not handle expanded items. Call collapse if possible
        collapseIfPossible(adapter.getFastAdapter());

        //if we have a comparator then sort
        if (adapter.getItemList() instanceof ComparableItemListImpl) {
            Collections.sort(items, ((ComparableItemListImpl) adapter.getItemList()).getComparator());
        }

        //map the types
        adapter.mapPossibleTypes(items);

        //remember the old items
        final List<Item> adapterItems = adapter.getAdapterItems();
        final List<Item> oldItems = new ArrayList<>(adapterItems);

        //pass in the oldItem list copy as we will update the one in the adapter itself
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(new FastAdapterCallback<>(oldItems, items, callback), detectMoves);

        //make sure the new items list is not a reference of the already mItems list
        if (items != adapterItems) {
            //remove all previous items
            if (!adapterItems.isEmpty()) {
                adapterItems.clear();
            }

            //add all new items to the list
            adapterItems.addAll(items);
        }

        return result;
    }

    /**
     * Uses Reflection to collapse all items if this adapter uses expandable items
     *
     * @param fastAdapter
     */
    private static void collapseIfPossible(FastAdapter fastAdapter) {
        try {
            Class c = Class.forName("com.mikepenz.fastadapter.expandable.ExpandableExtension");
            if (c != null) {
                IAdapterExtension extension = fastAdapter.getExtension(c);
                if (extension != null) {
                    Method method = extension.getClass().getMethod("collapse");
                    method.invoke(extension);
                }
            }
        } catch (Exception ignored) {
            //
        }
    }

    /**
     * Dispatches a {@link android.support.v7.util.DiffUtil.DiffResult} to the given Adapter.
     *
     * @param adapter the adapter to dispatch the updates to
     * @param result  the computed {@link android.support.v7.util.DiffUtil.DiffResult}
     * @return the adapter to allow chaining
     */
    public static <A extends ModelAdapter<Model, Item>, Model, Item extends IItem> A set(final A adapter, DiffUtil.DiffResult result) {
        result.dispatchUpdatesTo(new FastAdapterListUpdateCallback<>(adapter));
        return adapter;
    }

    /**
     * convenient function for {@link #calculateDiff(ModelAdapter, List, DiffCallback, boolean)}
     *
     * @return the {@link android.support.v7.util.DiffUtil.DiffResult} computed.
     */
    public static <A extends ModelAdapter<Model, Item>, Model, Item extends IItem> DiffUtil.DiffResult calculateDiff(final A adapter, final List<Item> items, final DiffCallback<Item> callback) {
        return calculateDiff(adapter, items, callback, true);
    }

    /**
     * convenient function for {@link #calculateDiff(ModelAdapter, List, DiffCallback, boolean)}
     *
     * @return the {@link android.support.v7.util.DiffUtil.DiffResult} computed.
     */
    public static <A extends ModelAdapter<Model, Item>, Model, Item extends IItem> DiffUtil.DiffResult calculateDiff(final A adapter, final List<Item> items, final boolean detectMoves) {
        return calculateDiff(adapter, items, new DiffCallbackImpl<Item>(), detectMoves);
    }

    /**
     * convenient function for {@link #calculateDiff(ModelAdapter, List, DiffCallback, boolean)}
     *
     * @return the {@link android.support.v7.util.DiffUtil.DiffResult} computed.
     */
    public static <A extends ModelAdapter<Model, Item>, Model, Item extends IItem> DiffUtil.DiffResult calculateDiff(final A adapter, final List<Item> items) {
        return calculateDiff(adapter, items, new DiffCallbackImpl<Item>(), true);
    }

    /**
     * Calculates a {@link android.support.v7.util.DiffUtil.DiffResult} given the adapter and the items, and will directly dispatch them to the adapter.
     *
     * @param adapter     the adapter containing the current items.
     * @param items       the new set of items we want to put into the adapter
     * @param callback    the callback used to implement the required checks to identify changes of items.
     * @param detectMoves configuration for the {@link DiffUtil#calculateDiff(DiffUtil.Callback, boolean)} method
     * @param <A>         The adapter type, whereas A extends {@link ModelAdapter}
     * @param <Model>     The model type we work with
     * @param <Item>      The item type kept in the adapter
     * @return the adapter to allow chaining
     */
    public static <A extends ModelAdapter<Model, Item>, Model, Item extends IItem> A set(final A adapter, final List<Item> items, final DiffCallback<Item> callback, final boolean detectMoves) {
        DiffUtil.DiffResult result = calculateDiff(adapter, items, callback, detectMoves);
        return set(adapter, result);
    }

    /**
     * convenient function for {@link #set(FastItemAdapter, List, DiffCallback, boolean)}
     *
     * @return the adapter to allow chaining
     */
    public static <A extends ModelAdapter<Model, Item>, Model, Item extends IItem> A set(final A adapter, final List<Item> items, final DiffCallback<Item> callback) {
        return set(adapter, items, callback, true);
    }

    /**
     * convenient function for {@link #set(FastItemAdapter, List, DiffCallback, boolean)}
     *
     * @return the adapter to allow chaining
     */
    public static <A extends ModelAdapter<Model, Item>, Model, Item extends IItem> A set(final A adapter, final List<Item> items, final boolean detectMoves) {
        return set(adapter, items, new DiffCallbackImpl<Item>(), detectMoves);
    }

    /**
     * convenient function for {@link #set(FastItemAdapter, List, DiffCallback, boolean)}
     *
     * @return the adapter to allow chaining
     */
    public static <A extends ModelAdapter<Model, Item>, Model, Item extends IItem> A set(final A adapter, final List<Item> items) {
        return set(adapter, items, new DiffCallbackImpl<Item>());
    }

    /**
     * convenient function for {@link #calculateDiff(ModelAdapter, List, DiffCallback, boolean)}
     *
     * @return the {@link android.support.v7.util.DiffUtil.DiffResult} computed.
     */
    public static <A extends FastItemAdapter<Item>, Item extends IItem> DiffUtil.DiffResult calculateDiff(final A adapter, final List<Item> items, final DiffCallback<Item> callback) {
        return calculateDiff(adapter.getItemAdapter(), items, callback);
    }

    /**
     * convenient function for {@link #calculateDiff(ModelAdapter, List, DiffCallback, boolean)}
     *
     * @return the {@link android.support.v7.util.DiffUtil.DiffResult} computed.
     */
    public static <A extends FastItemAdapter<Item>, Item extends IItem> DiffUtil.DiffResult calculateDiff(final A adapter, final List<Item> items, final boolean detectMoves) {
        return calculateDiff(adapter.getItemAdapter(), items, detectMoves);
    }

    /**
     * convenient function for {@link #calculateDiff(ModelAdapter, List, DiffCallback, boolean)}
     *
     * @return the {@link android.support.v7.util.DiffUtil.DiffResult} computed.
     */
    public static <A extends FastItemAdapter<Item>, Item extends IItem> DiffUtil.DiffResult calculateDiff(final A adapter, final List<Item> items) {
        return calculateDiff(adapter.getItemAdapter(), items);
    }

    /**
     * convenient function for {@link #set(FastItemAdapter, List, DiffCallback, boolean)}
     *
     * @return the adapter to allow chaining
     */
    public static <A extends FastItemAdapter<Item>, Item extends IItem> A set(final A adapter, final List<Item> items, final DiffCallback<Item> callback, final boolean detectMoves) {
        set(adapter.getItemAdapter(), items, callback, detectMoves);
        return adapter;
    }

    /**
     * convenient function for {@link #set(FastItemAdapter, List, DiffCallback, boolean)}
     *
     * @return the adapter to allow chaining
     */
    public static <A extends FastItemAdapter<Item>, Item extends IItem> A set(final A adapter, final List<Item> items, final DiffCallback<Item> callback) {
        set(adapter.getItemAdapter(), items, callback);
        return adapter;
    }

    /**
     * convenient function for {@link #set(FastItemAdapter, List, DiffCallback, boolean)}
     *
     * @return the adapter to allow chaining
     */
    public static <A extends FastItemAdapter<Item>, Item extends IItem> A set(final A adapter, final List<Item> items, final boolean detectMoves) {
        set(adapter.getItemAdapter(), items, detectMoves);
        return adapter;
    }

    /**
     * convenient function for {@link #set(FastItemAdapter, List, DiffCallback, boolean)}
     *
     * @return the adapter to allow chaining
     */

    public static <A extends FastItemAdapter<Item>, Item extends IItem> A set(final A adapter, final List<Item> items) {
        return set(adapter, items, new DiffCallbackImpl<Item>());
    }

    /**
     * convenient function for {@link #set(FastItemAdapter, List, DiffCallback, boolean)}
     *
     * @return the adapter to allow chaining
     */
    public static <A extends FastItemAdapter<Item>, Item extends IItem> A set(final A adapter, DiffUtil.DiffResult result) {
        set(adapter.getItemAdapter(), result);
        return adapter;
    }

    /**
     * Convenient implementation for the {@link android.support.v7.util.DiffUtil.Callback} to simplify difference calculation using {@link FastAdapter} items.
     *
     * @param <Item> the item type in the adapter
     */
    private static final class FastAdapterCallback<Item extends IItem> extends DiffUtil.Callback {

        private final List<Item> oldItems;
        private final List<Item> newItems;
        private final DiffCallback<Item> callback;

        FastAdapterCallback(List<Item> oldItems, List<Item> newItems, DiffCallback<Item> callback) {
            this.oldItems = oldItems;
            this.newItems = newItems;
            this.callback = callback;
        }

        @Override
        public int getOldListSize() {
            return oldItems.size();
        }

        @Override
        public int getNewListSize() {
            return newItems.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return callback.areItemsTheSame(oldItems.get(oldItemPosition), newItems.get(newItemPosition));
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            return callback.areContentsTheSame(oldItems.get(oldItemPosition), newItems.get(newItemPosition));
        }

        @Nullable
        @Override
        public Object getChangePayload(int oldItemPosition, int newItemPosition) {
            Object result = callback.getChangePayload(oldItems.get(oldItemPosition), oldItemPosition, newItems.get(newItemPosition), newItemPosition);
            return result == null ? super.getChangePayload(oldItemPosition, newItemPosition) : result;
        }
    }

    /**
     * Default implementation of the {@link ListUpdateCallback} to apply changes to the adapter and notify about the changes.
     */
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