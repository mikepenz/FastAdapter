package com.mikepenz.fastadapter.adapters;

import android.widget.Filter;

import com.mikepenz.fastadapter.IAdapterExtension;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.IItemAdapter;
import com.mikepenz.fastadapter.listeners.ItemFilterListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import static java.util.Arrays.asList;

/**
 * ItemFilter which extends the Filter api provided by Android
 * This calls automatically all required methods, just overwrite the filterItems method
 */
public class ItemFilter<Model, Item extends IItem> extends Filter {
    private List<Item> mOriginalItems;
    private CharSequence mConstraint;
    private ModelAdapter<?, Item> mItemAdapter;

    public ItemFilter(ModelAdapter<?, Item> itemAdapter) {
        this.mItemAdapter = itemAdapter;
    }


    //the listener which will be called after the items were filtered
    protected ItemFilterListener<Item> mItemFilterListener;

    /**
     * @param listener which will be called after the items were filtered
     * @return this
     */
    public ItemFilter<Model, Item> withItemFilterListener(ItemFilterListener<Item> listener) {
        mItemFilterListener = listener;
        return this;
    }

    //the filter predicate which is used in the ItemFilter
    private IItemAdapter.Predicate<Item> mFilterPredicate;

    /**
     * define the predicate used to filter the list inside the ItemFilter
     *
     * @param filterPredicate the predicate used to filter the list inside the ItemFilter
     * @return this
     */
    public ItemFilter<Model, Item> withFilterPredicate(IItemAdapter.Predicate<Item> filterPredicate) {
        this.mFilterPredicate = filterPredicate;
        return this;
    }

    @Override
    public FilterResults performFiltering(@Nullable CharSequence constraint) {
        FilterResults results = new FilterResults();

        //return nothing
        if (mOriginalItems == null && (constraint == null || constraint.length() == 0)) {
            return results;
        }

        //call extensions
        for (IAdapterExtension<Item> ext : mItemAdapter.getFastAdapter().getExtensions()) {
            ext.performFiltering(constraint);
        }

        mConstraint = constraint;

        if (mOriginalItems == null) {
            mOriginalItems = new ArrayList<>(mItemAdapter.getAdapterItems());
        }

        // We implement here the filter logic
        if (constraint == null || constraint.length() == 0) {
            // No filter implemented we return all the list
            results.values = mOriginalItems;
            results.count = mOriginalItems.size();
            //our filter was cleared we can now forget the old OriginalItems
            mOriginalItems = null;

            if (mItemFilterListener != null) {
                mItemFilterListener.onReset();
            }
        } else {
            List<Item> filteredItems = new ArrayList<>();

            // We perform filtering operation
            if (mFilterPredicate != null) {
                for (Item item : mOriginalItems) {
                    if (mFilterPredicate.filter(item, constraint)) {
                        filteredItems.add(item);
                    }
                }
            } else {
                filteredItems = mItemAdapter.getAdapterItems();
            }

            results.values = filteredItems;
            results.count = filteredItems.size();
        }
        return results;
    }

    public CharSequence getConstraint() {
        return mConstraint;
    }

    @Override
    protected void publishResults(@Nullable CharSequence constraint, FilterResults results) {
        // Now we have to inform the adapter about the new list filtered
        if (results.values != null) {
            mItemAdapter.setInternal((List<Item>) results.values, false, null);
        }

        //only fire when we are filtered, not in onreset
        if (mItemFilterListener != null && mOriginalItems != null) {
            mItemFilterListener.itemsFiltered(constraint, (List<Item>) results.values);
        }
    }

    /**
     * helper method to get all selections from the ItemAdapter's original item list
     *
     * @return a Set with the global positions of all selected Items
     */
    public Set<Integer> getSelections() {
        if (mOriginalItems != null) {
            Set<Integer> selections = new HashSet<>();
            int adapterOffset = mItemAdapter.getFastAdapter().getPreItemCountByOrder(mItemAdapter.getOrder());
            for (int i = 0, size = mOriginalItems.size(); i < size; i++) {
                Item item = mOriginalItems.get(i);
                if (item.isSelected()) {
                    selections.add(i + adapterOffset);
                }
            }
            return selections;
        } else {
            return mItemAdapter.getFastAdapter().getSelections();
        }
    }

    /**
     * helper method to get all selections from the ItemAdapter's original item list
     *
     * @return a Set with the selected items out of all items in this itemAdapter (not the listed ones)
     */
    public Set<Item> getSelectedItems() {
        if (mOriginalItems != null) {
            Set<Item> selections = new HashSet<>();
            for (int i = 0, size = mOriginalItems.size(); i < size; i++) {
                Item item = mOriginalItems.get(i);
                if (item.isSelected()) {
                    selections.add(item);
                }
            }
            return selections;
        } else {
            return mItemAdapter.getFastAdapter().getSelectedItems();
        }
    }

    /**
     * Searches for the given item and calculates its relative position
     *
     * @param item the item which is searched for
     * @return the relative position
     */
    public int getAdapterPosition(Item item) {
        return getAdapterPosition(item.getIdentifier());
    }

    /**
     * Searches for the given identifier and calculates its relative position
     *
     * @param identifier the identifier of an item which is searched for
     * @return the relative position
     */
    public int getAdapterPosition(long identifier) {
        for (int i = 0, size = mOriginalItems.size(); i < size; i++) {
            if (mOriginalItems.get(i).getIdentifier() == identifier) {
                return i;
            }
        }
        return -1;
    }

    /**
     * add an array of items to the end of the existing items
     *
     * @param items the items to add
     */
    @SafeVarargs
    public final ModelAdapter<?, Item> add(Item... items) {
        return add(asList(items));
    }

    /**
     * add a list of items to the end of the existing items
     * will prior check if we are currently filtering
     *
     * @param items the items to add
     */
    public ModelAdapter<?, Item> add(List<Item> items) {
        if (mOriginalItems != null && items.size() > 0) {
            if (mItemAdapter.isUseIdDistributor()) {
                mItemAdapter.getIdDistributor().checkIds(items);
            }
            mOriginalItems.addAll(items);
            publishResults(mConstraint, performFiltering(mConstraint));
            return mItemAdapter;
        } else {
            return mItemAdapter.addInternal(items);
        }
    }

    /**
     * add an array of items at the given position within the existing items
     *
     * @param position the global position
     * @param items    the items to add
     */
    @SafeVarargs
    public final ModelAdapter<?, Item> add(int position, Item... items) {
        return add(position, asList(items));
    }

    /**
     * add a list of items at the given position within the existing items
     *
     * @param position the global position
     * @param items    the items to add
     */
    public ModelAdapter<?, Item> add(int position, List<Item> items) {
        if (mOriginalItems != null && items.size() > 0) {
            if (mItemAdapter.isUseIdDistributor()) {
                mItemAdapter.getIdDistributor().checkIds(items);
            }
            mOriginalItems.addAll(getAdapterPosition(mItemAdapter.getAdapterItems().get(position)) - mItemAdapter.getFastAdapter().getPreItemCount(position), items);
            publishResults(mConstraint, performFiltering(mConstraint));
            return mItemAdapter;
        } else {
            return mItemAdapter.addInternal(position, items);
        }
    }

    /**
     * sets an item at the given position, overwriting the previous item
     *
     * @param position the global position
     * @param item     the item to set
     */
    public ModelAdapter<?, Item> set(int position, Item item) {
        if (mOriginalItems != null) {
            if (mItemAdapter.isUseIdDistributor()) {
                mItemAdapter.getIdDistributor().checkId(item);
            }
            mOriginalItems.set(getAdapterPosition(mItemAdapter.getAdapterItems().get(position)) - mItemAdapter.getFastAdapter().getPreItemCount(position), item);
            publishResults(mConstraint, performFiltering(mConstraint));
            return mItemAdapter;
        } else {
            return mItemAdapter.setInternal(position, item);
        }
    }

    /**
     * moves an item within the list from a position to a position
     *
     * @param fromPosition the position global from which we want to move
     * @param toPosition   the global position to which to move
     * @return this
     */
    public ModelAdapter<?, Item> move(int fromPosition, int toPosition) {
        if (mOriginalItems != null) {
            int preItemCount = mItemAdapter.getFastAdapter().getPreItemCount(fromPosition);
            int adjustedFrom = getAdapterPosition(mItemAdapter.getAdapterItems().get(fromPosition));
            int adjustedTo = getAdapterPosition(mItemAdapter.getAdapterItems().get(toPosition));
            Item item = mOriginalItems.get(adjustedFrom - preItemCount);
            mOriginalItems.remove(adjustedFrom - preItemCount);
            mOriginalItems.add(adjustedTo - preItemCount, item);
            performFiltering(mConstraint);
            return mItemAdapter;
        } else {
            return mItemAdapter.move(fromPosition, toPosition);
        }
    }

    /**
     * removes an item at the given position within the existing icons
     *
     * @param position the global position
     */
    public ModelAdapter<?, Item> remove(int position) {
        if (mOriginalItems != null) {
            mOriginalItems.remove(getAdapterPosition(mItemAdapter.getAdapterItems().get(position)) - mItemAdapter.getFastAdapter().getPreItemCount(position));
            publishResults(mConstraint, performFiltering(mConstraint));
            return mItemAdapter;
        } else {
            return mItemAdapter.remove(position);
        }
    }

    /**
     * removes a range of items starting with the given position within the existing icons
     *
     * @param position  the global position
     * @param itemCount the count of items which were removed
     */
    public ModelAdapter<?, Item> removeRange(int position, int itemCount) {
        if (mOriginalItems != null) {
            //global position to relative
            int length = mOriginalItems.size();
            int preItemCount = mItemAdapter.getFastAdapter().getPreItemCount(position);
            //make sure we do not delete to many items
            int saveItemCount = Math.min(itemCount, length - position + preItemCount);
            for (int i = 0; i < saveItemCount; i++) {
                mOriginalItems.remove(position - preItemCount);
            }
            publishResults(mConstraint, performFiltering(mConstraint));
            return mItemAdapter;
        } else {
            return mItemAdapter.removeRange(position, itemCount);
        }
    }

    /**
     * removes all items of this adapter
     */
    public ModelAdapter<?, Item> clear() {
        if (mOriginalItems != null) {
            mOriginalItems.clear();
            publishResults(mConstraint, performFiltering(mConstraint));
            return mItemAdapter;
        } else {
            return mItemAdapter.clear();
        }
    }
}
