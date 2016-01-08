package com.mikepenz.fastadapter.adapters;

import android.widget.Filter;

import com.mikepenz.fastadapter.AbstractAdapter;
import com.mikepenz.fastadapter.ICollapsible;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.IItemAdapter;
import com.mikepenz.fastadapter.utils.IdDistributor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by mikepenz on 27.12.15.
 * A general ItemAdapter implementation based on the AbstractAdapter to speed up development for general items
 * This adapter has the order of 500 which is the centered order
 */
public class ItemAdapter<Item extends IItem> extends AbstractAdapter<Item> implements IItemAdapter<Item> {
    //the items handled and managed by this item
    private List<Item> mItems = new ArrayList<>();

    //defines if the IdDistributor is used to set ID's to all added items
    private boolean mUseIdDistributor = true;

    /**
     * defines if the IdDistributor is used to provide an ID to all added items which do not yet define an id
     *
     * @param useIdDistributor false if the IdDistributor shouldn't be used
     * @return this
     */
    public ItemAdapter withUseIdDistributor(boolean useIdDistributor) {
        this.mUseIdDistributor = useIdDistributor;
        return this;
    }

    //filters the items
    private ItemFilter mItemFilter = new ItemFilter();

    /**
     * @return the filter used to filter items
     */
    public ItemFilter getItemFilter() {
        return mItemFilter;
    }

    //the filter predicate which is used in the ItemFilter
    private Predicate<Item> mFilterPredicate;

    /**
     * define the predicate used to filter the list inside the ItemFilter
     *
     * @param filterPredicate the predicate used to filter the list inside the ItemFilter
     * @return this
     */
    public ItemAdapter withFilterPredicate(Predicate<Item> filterPredicate) {
        this.mFilterPredicate = filterPredicate;
        return this;
    }

    /**
     * filters the items with the constraint using the provided Predicate
     *
     * @param constraint the string used to filter the list
     */
    public void filter(CharSequence constraint) {
        mItemFilter.filter(constraint);
    }

    /**
     * @return the order of the items within the FastAdapter
     */
    @Override
    public int getOrder() {
        return 500;
    }

    /**
     * @return the count of items within this adapter
     */
    @Override
    public int getAdapterItemCount() {
        return mItems.size();
    }


    /**
     * @return the items within this adapter
     */
    @Override
    public List<Item> getAdapterItems() {
        return mItems;
    }

    /**
     * Searches for the given item and calculates it's relative position
     *
     * @param item the item which is searched for
     * @return the relative position
     */
    @Override
    public int getAdapterPosition(Item item) {
        for (int i = 0; i < mItems.size(); i++) {
            if (mItems.get(i).getIdentifier() == item.getIdentifier()) {
                return i;
            }
        }
        return -1;
    }

    /**
     * returns the global position if the relative position within this adapter was given
     *
     * @param position
     * @return
     */
    public int getGlobalPosition(int position) {
        return position + getFastAdapter().getItemCount(getOrder());
    }

    /**
     * @param position the relative position
     * @return the item inside this adapter
     */
    @Override
    public Item getAdapterItem(int position) {
        return mItems.get(position);
    }

    /**
     * sets the subItems of the given collapsible
     * This method also makes sure identifiers are set if we use the IdDistributor
     *
     * @param collapsible the collapsible which gets the subItems set
     * @param subItems    the subItems for this collapsible item
     * @return the item type of the collapsible
     */
    public <T> T setSubItems(ICollapsible<T, Item> collapsible, List<Item> subItems) {
        if (mUseIdDistributor) {
            IdDistributor.checkIds(subItems);
        }
        return collapsible.withSubItems(subItems);
    }

    /**
     * set a new list of items for this adapter
     *
     * @param items
     */
    public void set(List<Item> items) {
        if (mUseIdDistributor) {
            IdDistributor.checkIds(items);
        }
        mItems = (List<Item>) items;
        mapPossibleTypes(mItems);
        getFastAdapter().notifyAdapterItemRangeChanged(getFastAdapter().getItemCount(getOrder()), getAdapterItemCount());
    }

    /**
     * add an array of items to the end of the existing items
     *
     * @param items
     */
    public void add(Item... items) {
        if (items != null) {
            if (mUseIdDistributor) {
                IdDistributor.checkIds(items);
            }
            Collections.addAll(mItems, items);
            mapPossibleTypes(Arrays.asList(items));
            getFastAdapter().notifyAdapterItemRangeInserted(getFastAdapter().getItemCount(getOrder()), items.length);
        }
    }

    /**
     * add a list of items to the end of the existing items
     *
     * @param items
     */
    public void add(List<Item> items) {
        if (items != null) {
            if (mUseIdDistributor) {
                IdDistributor.checkIds(items);
            }
            mItems.addAll(items);
            mapPossibleTypes((Iterable<Item>) items);
            getFastAdapter().notifyAdapterItemRangeInserted(getFastAdapter().getItemCount(getOrder()), items.size());
        }
    }

    /**
     * add an array of items at the given position within the existing items
     *
     * @param position the global position
     * @param items
     */
    public void add(int position, Item... items) {
        if (mUseIdDistributor) {
            IdDistributor.checkIds(items);
        }
        if (items != null) {
            mItems.addAll(position - getFastAdapter().getItemCount(getOrder()), Arrays.asList(items));
            mapPossibleTypes(Arrays.asList(items));
            getFastAdapter().notifyAdapterItemRangeInserted(position, items.length);
        }
    }

    /**
     * add a list of items at the given position within the existing items
     *
     * @param position the global position
     * @param items
     */
    public void add(int position, List<Item> items) {
        if (mUseIdDistributor) {
            IdDistributor.checkIds(items);
        }
        if (items != null) {
            mItems.addAll(position - getFastAdapter().getItemCount(getOrder()), items);
            mapPossibleTypes(items);
            getFastAdapter().notifyAdapterItemRangeInserted(position, items.size());
        }
    }

    /**
     * sets an item at the given position, overwriting the previous item
     *
     * @param position the global position
     * @param item
     */
    public void set(int position, Item item) {
        if (mUseIdDistributor) {
            IdDistributor.checkId(item);
        }
        mItems.set(position - getFastAdapter().getItemCount(getOrder()), item);
        mapPossibleType(item);
        getFastAdapter().notifyAdapterItemChanged(position);
    }

    /**
     * add an item at the end of the existing items
     *
     * @param item
     */
    public void add(Item item) {
        if (mUseIdDistributor) {
            IdDistributor.checkId(item);
        }
        mItems.add(item);
        mapPossibleType(item);
        getFastAdapter().notifyAdapterItemInserted(getFastAdapter().getItemCount(getOrder()) + mItems.size());
    }

    /**
     * add an item at the given position within the existing icons
     *
     * @param position the global position
     * @param item
     */
    public void add(int position, Item item) {
        if (mUseIdDistributor) {
            IdDistributor.checkId(item);
        }
        mItems.add(position - getFastAdapter().getItemCount(getOrder()), item);
        mapPossibleType(item);
        getFastAdapter().notifyAdapterItemInserted(position);
    }

    /**
     * removes an item at the given position within the existing icons
     *
     * @param position the global position
     */
    public void remove(int position) {
        mItems.remove(position - getFastAdapter().getItemCount(getOrder()));
        getFastAdapter().notifyAdapterItemRemoved(position);
    }

    /**
     * removes a range of items starting with the given position within the existing icons
     *
     * @param position  the global position
     * @param itemCount
     */
    public void removeItemRange(int position, int itemCount) {
        //global position to relative
        int length = mItems.size();
        //make sure we do not delete to many items
        int saveItemCount = Math.min(itemCount, length - position + getFastAdapter().getItemCount(getOrder()));

        for (int i = 0; i < saveItemCount; i++) {
            mItems.remove(position - getFastAdapter().getItemCount(getOrder()));
        }

        getFastAdapter().notifyAdapterItemRangeRemoved(position, saveItemCount);
    }

    /**
     * removes all items of this adapter
     */
    public void clear() {
        int count = mItems.size();
        mItems.clear();
        getFastAdapter().notifyAdapterItemRangeRemoved(getFastAdapter().getItemCount(getOrder()), count);
    }

    /**
     * ItemFilter which extends the Filter api provided by Android
     * This calls automatically all required methods, just overwrite the filterItems method
     */
    public class ItemFilter extends Filter {
        private List<Item> mOriginalItems;

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if (mOriginalItems == null) {
                mOriginalItems = new ArrayList<>(mItems);
            }

            FilterResults results = new FilterResults();
            // We implement here the filter logic
            if (constraint == null || constraint.length() == 0) {
                // No filter implemented we return all the list
                results.values = mOriginalItems;
                results.count = mOriginalItems.size();
            } else {
                List<Item> filteredItems = new ArrayList<>();

                // We perform filtering operation
                if (mFilterPredicate != null) {
                    for (Item item : mOriginalItems) {
                        if (!mFilterPredicate.filter(item, constraint)) {
                            filteredItems.add(item);
                        }
                    }
                } else {
                    filteredItems = mItems;
                }

                results.values = filteredItems;
                results.count = filteredItems.size();
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            // Now we have to inform the adapter about the new list filtered
            animateTo(mItems, (List<Item>) results.values);
        }

        /**
         * helper class to animate from one list to the other
         *
         * @param from
         * @param models
         */
        private void animateTo(List<Item> from, List<Item> models) {
            applyAndAnimateRemovals(from, models);
            applyAndAnimateAdditions(from, models);
        }

        /**
         * find out all removed items and animate them
         *
         * @param from
         * @param newModels
         */
        private void applyAndAnimateRemovals(List<Item> from, List<Item> newModels) {
            for (int i = from.size() - 1; i >= 0; i--) {
                final Item model = from.get(i);
                if (!newModels.contains(model)) {
                    //our methods work only with the global position
                    remove(i + getFastAdapter().getItemCount(getOrder()));
                }
            }
        }

        /**
         * find out all added items and animate them
         *
         * @param from
         * @param newModels
         */
        private void applyAndAnimateAdditions(List<Item> from, List<Item> newModels) {
            for (int i = 0, count = newModels.size(); i < count; i++) {
                final Item model = newModels.get(i);
                if (!from.contains(model)) {
                    //our methods work only with the global position
                    add(i + getFastAdapter().getItemCount(getOrder()), model);
                }
            }
        }
    }
}
