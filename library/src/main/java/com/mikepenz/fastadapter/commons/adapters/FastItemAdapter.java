package com.mikepenz.fastadapter.commons.adapters;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.adapters.ItemFilter;

import java.util.List;

import static com.mikepenz.fastadapter.adapters.ItemAdapter.items;

/**
 * Created by mikepenz on 18.01.16.
 */
public class FastItemAdapter<Item extends IItem> extends FastAdapter<Item> {
    private ItemAdapter<Item> itemAdapter;

    /**
     * ctor
     */
    public FastItemAdapter() {
        itemAdapter = items();
        addAdapter(0, itemAdapter);
        cacheSizes();
    }

    /**
     * returns the internal created ItemAdapter
     *
     * @return the ItemAdapter used inside this FastItemAdapter
     */
    public ItemAdapter<Item> getItemAdapter() {
        return itemAdapter;
    }

    /**
     * defines if the IdDistributor is used to provide an ID to all added items which do not yet define an id
     *
     * @param useIdDistributor false if the IdDistributor shouldn't be used
     * @return this
     */
    public FastItemAdapter<Item> withUseIdDistributor(boolean useIdDistributor) {
        getItemAdapter().withUseIdDistributor(useIdDistributor);
        return this;
    }

    /**
     * @return the filter used to filter items
     */
    public ItemFilter<?, Item> getItemFilter() {
        return getItemAdapter().getItemFilter();
    }

    /**
     * filters the items with the constraint using the provided Predicate
     *
     * @param constraint the string used to filter the list
     */
    public void filter(CharSequence constraint) {
        getItemAdapter().filter(constraint);
    }

    /**
     * @return the order of the items within the FastAdapter
     */
    public int getOrder() {
        return getItemAdapter().getOrder();
    }

    /**
     * @return the count of items within this adapter
     */
    public int getAdapterItemCount() {
        return getItemAdapter().getAdapterItemCount();
    }


    /**
     * @return the items within this adapter
     */
    public List<Item> getAdapterItems() {
        return getItemAdapter().getAdapterItems();
    }

    /**
     * Searches for the given item and calculates its relative position
     *
     * @param item the item which is searched for
     * @return the relative position
     */
    public int getAdapterPosition(Item item) {
        return getItemAdapter().getAdapterPosition(item);
    }

    /**
     * returns the global position if the relative position within this adapter was given
     *
     * @param position the relative postion
     * @return the global position
     */
    public int getGlobalPosition(int position) {
        return getItemAdapter().getGlobalPosition(position);
    }

    /**
     * @param position the relative position
     * @return the item inside this adapter
     */
    public Item getAdapterItem(int position) {
        return getItemAdapter().getAdapterItem(position);
    }

    /**
     * set a new list of items and apply it to the existing list (clear - add) for this adapter
     *
     * @param items the new items to set
     */
    public FastItemAdapter<Item> set(List<Item> items) {
        getItemAdapter().set(items);
        return this;
    }

    /**
     * sets a complete new list of items onto this adapter, using the new list. Calls notifyDataSetChanged
     *
     * @param items the new items to set
     * @return this
     */
    public FastItemAdapter<Item> setNewList(List<Item> items) {
        getItemAdapter().setNewList(items);
        return this;
    }


    /**
     * sets a complete new list of items onto this adapter, using the new list. Calls notifyDataSetChanged
     *
     * @param items        the new items to set
     * @param retainFilter set to true if you want to keep the filter applied
     * @return this
     */
    public FastItemAdapter<Item> setNewList(List<Item> items, boolean retainFilter) {
        getItemAdapter().setNewList(items, retainFilter);
        return this;
    }

    /**
     * add an array of items to the end of the existing items
     *
     * @param items the items to add
     */
    @SafeVarargs
    public final FastItemAdapter<Item> add(Item... items) {
        getItemAdapter().add(items);
        return this;
    }

    /**
     * add a list of items to the end of the existing items
     *
     * @param items the items to add
     */
    public FastItemAdapter<Item> add(List<Item> items) {
        getItemAdapter().add(items);
        return this;
    }

    /**
     * add an array of items at the given position within the existing items
     *
     * @param position the global position
     * @param items    the items to add
     */
    @SafeVarargs
    public final FastItemAdapter<Item> add(int position, Item... items) {
        getItemAdapter().add(position, items);
        return this;
    }

    /**
     * add a list of items at the given position within the existing items
     *
     * @param position the global position
     * @param items    the items to add
     */
    public FastItemAdapter<Item> add(int position, List<Item> items) {
        getItemAdapter().add(position, items);
        return this;
    }

    /**
     * sets an item at the given position, overwriting the previous item
     *
     * @param position the global position
     * @param item     the item to set
     */
    public FastItemAdapter<Item> set(int position, Item item) {
        getItemAdapter().set(position, item);
        return this;
    }

    /**
     * add an item at the end of the existing items
     *
     * @param item the item to add
     */
    public FastItemAdapter<Item> add(Item item) {
        getItemAdapter().add(item);
        return this;
    }

    /**
     * add an item at the given position within the existing icons
     *
     * @param position the global position
     * @param item     the item to add
     */
    public FastItemAdapter<Item> add(int position, Item item) {
        getItemAdapter().add(position, item);
        return this;
    }

    /**
     * moves an item within the list from a position to a position
     *
     * @param fromPosition the position global from which we want to move
     * @param toPosition   the global position to which to move
     * @return this
     */
    public FastItemAdapter<Item> move(int fromPosition, int toPosition) {
        getItemAdapter().move(fromPosition, toPosition);
        return this;
    }

    /**
     * removes an item at the given position within the existing icons
     *
     * @param position the global position
     */
    public FastItemAdapter<Item> remove(int position) {
        getItemAdapter().remove(position);
        return this;
    }

    /**
     * removes a range of items starting with the given position within the existing icons
     *
     * @param position  the global position
     * @param itemCount the count of items removed
     */
    public FastItemAdapter<Item> removeItemRange(int position, int itemCount) {
        getItemAdapter().removeRange(position, itemCount);
        return this;
    }

    /**
     * removes all items of this adapter
     */
    public FastItemAdapter<Item> clear() {
        getItemAdapter().clear();
        return this;
    }

    /**
     * convenient functions, to force to remap all possible types for the RecyclerView
     */
    public void remapMappedTypes() {
        getItemAdapter().remapMappedTypes();
    }
}
