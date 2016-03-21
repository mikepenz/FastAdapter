package com.mikepenz.fastadapter.adapters;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IExpandable;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.IItemAdapter;

import java.util.List;

/**
 * Created by mikepenz on 18.01.16.
 */
public class FastItemAdapter<Item extends IItem> extends FastAdapter<Item> {
    private final ItemAdapter<Item> mItemAdapter = new ItemAdapter<>();

    /**
     * ctor
     */
    public FastItemAdapter() {
        mItemAdapter.wrap(this);
    }

    /**
     * returns the internal created ItemAdapter
     *
     * @return the ItemAdapter used inside this FastItemAdapter
     */
    public ItemAdapter<Item> getItemAdapter() {
        return mItemAdapter;
    }

    /**
     * defines if the IdDistributor is used to provide an ID to all added items which do not yet define an id
     *
     * @param useIdDistributor false if the IdDistributor shouldn't be used
     * @return this
     */
    public FastItemAdapter<Item> withUseIdDistributor(boolean useIdDistributor) {
        mItemAdapter.withUseIdDistributor(useIdDistributor);
        return this;
    }

    /**
     * @return the filter used to filter items
     */
    public ItemAdapter.ItemFilter getItemFilter() {
        return mItemAdapter.getItemFilter();
    }

    /**
     * define the predicate used to filter the list inside the ItemFilter
     *
     * @param filterPredicate the predicate used to filter the list inside the ItemFilter
     * @return this
     */
    public FastItemAdapter<Item> withFilterPredicate(IItemAdapter.Predicate<Item> filterPredicate) {
        this.mItemAdapter.withFilterPredicate(filterPredicate);
        return this;
    }

    /**
     * filters the items with the constraint using the provided Predicate
     *
     * @param constraint the string used to filter the list
     */
    public void filter(CharSequence constraint) {
        mItemAdapter.filter(constraint);
    }

    /**
     * @return the order of the items within the FastAdapter
     */
    public int getOrder() {
        return mItemAdapter.getOrder();
    }

    /**
     * @return the count of items within this adapter
     */
    public int getAdapterItemCount() {
        return mItemAdapter.getAdapterItemCount();
    }


    /**
     * @return the items within this adapter
     */
    public List<Item> getAdapterItems() {
        return mItemAdapter.getAdapterItems();
    }

    /**
     * Searches for the given item and calculates it's relative position
     *
     * @param item the item which is searched for
     * @return the relative position
     */
    public int getAdapterPosition(Item item) {
        return mItemAdapter.getAdapterPosition(item);
    }

    /**
     * returns the global position if the relative position within this adapter was given
     *
     * @param position the relative postion
     * @return the global position
     */
    public int getGlobalPosition(int position) {
        return mItemAdapter.getGlobalPosition(position);
    }

    /**
     * @param position the relative position
     * @return the item inside this adapter
     */
    public Item getAdapterItem(int position) {
        return mItemAdapter.getAdapterItem(position);
    }

    /**
     * sets the subItems of the given collapsible
     * This method also makes sure identifiers are set if we use the IdDistributor
     *
     * @param collapsible the collapsible which gets the subItems set
     * @param subItems    the subItems for this collapsible item
     * @return the item type of the collapsible
     */
    public <T> T setSubItems(IExpandable<T, Item> collapsible, List<Item> subItems) {
        return mItemAdapter.setSubItems(collapsible, subItems);
    }

    /**
     * set a new list of items and apply it to the existing list (clear - add) for this adapter
     *
     * @param items the new items to set
     */
    public FastItemAdapter<Item> set(List<Item> items) {
        mItemAdapter.set(items);
        return this;
    }

    /**
     * sets a complete new list of items onto this adapter, using the new list. Calls notifyDataSetChanged
     *
     * @param items the new items to set
     */
    public FastItemAdapter<Item> setNewList(List<Item> items) {
        mItemAdapter.setNewList(items);
        return this;
    }

    /**
     * add an array of items to the end of the existing items
     *
     * @param items the items to add
     */
    @SafeVarargs
    public final FastItemAdapter<Item> add(Item... items) {
        mItemAdapter.add(items);
        return this;
    }

    /**
     * add a list of items to the end of the existing items
     *
     * @param items the items to add
     */
    public FastItemAdapter<Item> add(List<Item> items) {
        mItemAdapter.add(items);
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
        mItemAdapter.add(position, items);
        return this;
    }

    /**
     * add a list of items at the given position within the existing items
     *
     * @param position the global position
     * @param items    the items to add
     */
    public FastItemAdapter<Item> add(int position, List<Item> items) {
        mItemAdapter.add(position, items);
        return this;
    }

    /**
     * sets an item at the given position, overwriting the previous item
     *
     * @param position the global position
     * @param item     the item to set
     */
    public FastItemAdapter<Item> set(int position, Item item) {
        mItemAdapter.set(position, item);
        return this;
    }

    /**
     * add an item at the end of the existing items
     *
     * @param item the item to add
     */
    public FastItemAdapter<Item> add(Item item) {
        mItemAdapter.add(item);
        return this;
    }

    /**
     * add an item at the given position within the existing icons
     *
     * @param position the global position
     * @param item     the item to add
     */
    public FastItemAdapter<Item> add(int position, Item item) {
        mItemAdapter.add(position, item);
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
        mItemAdapter.move(fromPosition, toPosition);
        return this;
    }

    /**
     * removes an item at the given position within the existing icons
     *
     * @param position the global position
     */
    public FastItemAdapter<Item> remove(int position) {
        mItemAdapter.remove(position);
        return this;
    }

    /**
     * removes a range of items starting with the given position within the existing icons
     *
     * @param position  the global position
     * @param itemCount the count of items removed
     */
    public FastItemAdapter<Item> removeItemRange(int position, int itemCount) {
        mItemAdapter.removeRange(position, itemCount);
        return this;
    }

    /**
     * removes all items of this adapter
     */
    public FastItemAdapter<Item> clear() {
        mItemAdapter.clear();
        return this;
    }
}
