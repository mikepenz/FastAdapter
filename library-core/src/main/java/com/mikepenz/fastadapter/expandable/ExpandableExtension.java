package com.mikepenz.fastadapter.expandable;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.MotionEvent;
import android.view.View;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.IAdapterExtension;
import com.mikepenz.fastadapter.IExpandable;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.IItemAdapter;
import com.mikepenz.fastadapter.utils.AdapterUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by mikepenz on 04/06/2017.
 */

public class ExpandableExtension<Item extends IItem> implements IAdapterExtension<Item> {
    protected static final String BUNDLE_EXPANDED = "bundle_expanded";

    //
    private FastAdapter<Item> mFastAdapter;
    // only one expanded section
    private boolean mOnlyOneExpandedItem = false;
    // we need to remember all expanded items to recreate them after orientation change
    private SparseIntArray mExpanded = new SparseIntArray();

    @Override
    public ExpandableExtension<Item> init(FastAdapter<Item> fastAdapter) {
        mFastAdapter = fastAdapter;
        return this;
    }

    /**
     * set if there should only be one opened expandable item
     * DEFAULT: false
     *
     * @param mOnlyOneExpandedItem true if there should be only one expanded, expandable item in the list
     * @return this
     */
    public ExpandableExtension<Item> withOnlyOneExpandedItem(boolean mOnlyOneExpandedItem) {
        this.mOnlyOneExpandedItem = mOnlyOneExpandedItem;
        return this;
    }

    /**
     * @return if there should be only one expanded, expandable item in the list
     */
    public boolean isOnlyOneExpandedItem() {
        return mOnlyOneExpandedItem;
    }

    @Override
    public void withSavedInstanceState(@Nullable Bundle savedInstanceState, String prefix) {
        if (savedInstanceState == null) {
            return;
        }
        if (mFastAdapter.isPositionBasedStateManagement()) {
            //first restore opened collasable items, as otherwise may not all selections could be restored
            int[] expandedItems = savedInstanceState.getIntArray(BUNDLE_EXPANDED + prefix);
            if (expandedItems != null) {
                for (Integer expandedItem : expandedItems) {
                    expand(expandedItem);
                }
            }
        } else {
            ArrayList<String> expandedItems = savedInstanceState.getStringArrayList(BUNDLE_EXPANDED + prefix);

            Item item;
            String id;
            for (int i = 0, size = mFastAdapter.getItemCount(); i < size; i++) {
                item = mFastAdapter.getItem(i);
                id = String.valueOf(item.getIdentifier());
                if (expandedItems != null && expandedItems.contains(id)) {
                    expand(i);
                    size = mFastAdapter.getItemCount();
                }
            }
        }
    }

    @Override
    public void saveInstanceState(Bundle savedInstanceState, String prefix) {
        if (savedInstanceState == null) {
            return;
        }
        if (mFastAdapter.isPositionBasedStateManagement()) {
            //remember the collapsed states
            savedInstanceState.putIntArray(BUNDLE_EXPANDED + prefix, getExpandedItems());
        } else {
            ArrayList<String> selections = new ArrayList<>();
            ArrayList<String> expandedItems = new ArrayList<>();

            Item item;
            for (int i = 0, size = mFastAdapter.getItemCount(); i < size; i++) {
                item = mFastAdapter.getItem(i);
                if (item instanceof IExpandable && ((IExpandable) item).isExpanded()) {
                    expandedItems.add(String.valueOf(item.getIdentifier()));
                }
                //we also have to find all selections in the sub hirachies
                AdapterUtil.findSubItemSelections(item, selections);
            }
            //remember the collapsed states
            savedInstanceState.putStringArrayList(BUNDLE_EXPANDED + prefix, expandedItems);
        }
    }

    @Override
    public boolean onClick(View v, int pos, FastAdapter<Item> fastAdapter, Item item) {
        boolean consumed = false;
        //if this is a expandable item :D (this has to happen after we handled the selection as we refer to the position)
        if (!consumed && item instanceof IExpandable) {
            if (((IExpandable) item).isAutoExpanding() && ((IExpandable) item).getSubItems() != null) {
                toggleExpandable(pos);
            }
        }

        //if there should be only one expanded item we want to collapse all the others but the current one (this has to happen after we handled the selection as we refer to the position)
        if (!consumed && mOnlyOneExpandedItem && item instanceof IExpandable) {
            if (((IExpandable) item).getSubItems() != null && ((IExpandable) item).getSubItems().size() > 0) {
                int[] expandedItems = getExpandedItems();
                for (int i = expandedItems.length - 1; i >= 0; i--) {
                    if (expandedItems[i] != pos) {
                        collapse(expandedItems[i], true);
                    }
                }
            }
        }
        return consumed;
    }

    @Override
    public boolean onLongClick(View v, int pos, FastAdapter<Item> fastAdapter, Item item) {
        return false;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event, int position, FastAdapter<Item> fastAdapter, Item item) {
        return false;
    }

    @Override
    public void notifyAdapterDataSetChanged() {
        if (mFastAdapter.isPositionBasedStateManagement()) {
            mExpanded.clear();
            //we make sure the new items are displayed properly
            handleStates(mFastAdapter, 0, mFastAdapter.getItemCount() - 1);
        }
    }

    @Override
    public void notifyAdapterItemRangeInserted(int position, int itemCount) {
        if (mFastAdapter.isPositionBasedStateManagement()) {
            mExpanded = AdapterUtil.adjustPosition(mExpanded, position, Integer.MAX_VALUE, itemCount);
            //we make sure the new items are displayed properly
            handleStates(mFastAdapter, position, position + itemCount - 1);
        }
    }

    @Override
    public void notifyAdapterItemRangeRemoved(int position, int itemCount) {
        //we have to update all current stored selection and expandable states in our map
        if (mFastAdapter.isPositionBasedStateManagement()) {
            mExpanded = AdapterUtil.adjustPosition(mExpanded, position, Integer.MAX_VALUE, itemCount * (-1));
        }
    }

    @Override
    public void notifyAdapterItemMoved(int fromPosition, int toPosition) {
        //collapse items we move. just in case :D
        collapse(fromPosition);
        collapse(toPosition);
    }

    @Override
    public void notifyAdapterItemRangeChanged(int position, int itemCount, Object payload) {
        for (int i = position; i < position + itemCount; i++) {
            if (mFastAdapter.isPositionBasedStateManagement()) {
                if (mExpanded.indexOfKey(i) >= 0) {
                    collapse(i);
                }
            } else {
                Item item = mFastAdapter.getItem(position);
                if (item instanceof IExpandable && ((IExpandable) item).isExpanded()) {
                    collapse(position);
                }
            }
        }
        if (mFastAdapter.isPositionBasedStateManagement()) {
            //we make sure the new items are displayed properly
            AdapterUtil.handleStates(mFastAdapter, position, position + itemCount - 1);
        }
    }

    @Override
    public void set(List<Item> items, boolean resetFilter) {
        //first collapse all items
        collapse(false);
    }

    @Override
    public void performFiltering(CharSequence constraint) {
        collapse(false);
    }

    /**
     * notifies the fastAdapter about new / removed items within a sub hierarchy
     * NOTE this currently only works for sub items with only 1 level
     *
     * @param position the global position of the parent item
     */
    public void notifyAdapterSubItemsChanged(int position) {
        //TODO ALSO CARE ABOUT SUB SUB ... HIRACHIES

        if (mFastAdapter.isPositionBasedStateManagement()) {
            //we only need to do something if this item is expanded
            if (mExpanded.indexOfKey(position) > -1) {
                int previousCount = mExpanded.get(position);
                int itemsCount = notifyAdapterSubItemsChanged(position, previousCount);
                if (itemsCount == 0) {
                    mExpanded.delete(position);
                } else {
                    mExpanded.put(position, itemsCount);
                }
            }
        } else {
            Log.e("FastAdapter", "please use the notifyAdapterSubItemsChanged(int position, int previousCount) method instead in the PositionBasedStateManagement mode, as we are not able to calculate the previous count ");
        }
    }

    /**
     * notifies the fastAdapter about new / removed items within a sub hierarchy
     * NOTE this currently only works for sub items with only 1 level
     *
     * @param position      the global position of the parent item
     * @param previousCount the previous count of sub items
     * @return the new count of subItems
     */
    public int notifyAdapterSubItemsChanged(int position, int previousCount) {
        Item item = mFastAdapter.getItem(position);
        if (item != null && item instanceof IExpandable) {
            IExpandable expandable = (IExpandable) item;
            IAdapter adapter = mFastAdapter.getAdapter(position);
            if (adapter != null && adapter instanceof IItemAdapter) {
                ((IItemAdapter) adapter).removeRange(position + 1, previousCount);
                ((IItemAdapter) adapter).add(position + 1, expandable.getSubItems());
            }
            return expandable.getSubItems().size();
        }
        return 0;
    }

    //-------------------------
    //-------------------------
    //Expandable stuff
    //-------------------------
    //-------------------------

    /**
     * internal method which correctly set the selected state and expandable state on the newly added items
     *
     * @param fastAdapter   the fastAdapter which manages everything
     * @param startPosition the position of the first item to handle
     * @param endPosition   the position of the last item to handle
     */
    public <Item extends IItem> void handleStates(FastAdapter<Item> fastAdapter, int startPosition, int endPosition) {
        for (int i = endPosition; i >= startPosition; i--) {
            Item updateItem = fastAdapter.getItem(i);
            if (updateItem != null) {
                if (updateItem instanceof IExpandable) {
                    if (((IExpandable) updateItem).isExpanded() && getExpanded().indexOfKey(i) < 0) {
                        expand(i);
                    }
                }
            }
        }
    }

    /**
     * returns the expanded items this contains position and the count of items
     * which are expanded by this position
     *
     * @return the expanded items
     */
    public SparseIntArray getExpanded() {
        if (mFastAdapter.isPositionBasedStateManagement()) {
            return mExpanded;
        } else {
            SparseIntArray expandedItems = new SparseIntArray();
            Item item;
            for (int i = 0, size = mFastAdapter.getItemCount(); i < size; i++) {
                item = mFastAdapter.getItem(i);
                if (item instanceof IExpandable && ((IExpandable) item).isExpanded()) {
                    expandedItems.put(i, ((IExpandable) item).getSubItems().size());
                }
            }
            return expandedItems;
        }
    }

    /**
     * @return a set with the global positions of all expanded items
     */
    public int[] getExpandedItems() {
        int[] expandedItems;
        if (mFastAdapter.isPositionBasedStateManagement()) {
            int length = mExpanded.size();
            expandedItems = new int[length];
            for (int i = 0; i < length; i++) {
                expandedItems[i] = mExpanded.keyAt(i);
            }
        } else {
            ArrayList<Integer> expandedItemsList = new ArrayList<>();
            Item item;
            for (int i = 0, size = mFastAdapter.getItemCount(); i < size; i++) {
                item = mFastAdapter.getItem(i);
                if (item instanceof IExpandable && ((IExpandable) item).isExpanded()) {
                    expandedItemsList.add(i);
                }
            }

            int expandedItemsListLength = expandedItemsList.size();
            expandedItems = new int[expandedItemsListLength];
            for (int i = 0; i < expandedItemsListLength; i++) {
                expandedItems[i] = expandedItemsList.get(i);
            }
        }
        return expandedItems;
    }

    /**
     * toggles the expanded state of the given expandable item at the given position
     *
     * @param position the global position
     */
    public void toggleExpandable(int position) {
        if (mFastAdapter.isPositionBasedStateManagement()) {
            if (mExpanded.indexOfKey(position) >= 0) {
                collapse(position);
            } else {
                expand(position);
            }
        } else {
            Item item = mFastAdapter.getItem(position);
            if (item instanceof IExpandable && ((IExpandable) item).isExpanded()) {
                collapse(position);
            } else {
                expand(position);
            }
        }
    }

    /**
     * collapses all expanded items
     */
    public void collapse() {
        collapse(true);
    }

    /**
     * collapses all expanded items
     *
     * @param notifyItemChanged true if we need to call notifyItemChanged. DEFAULT: false
     */
    public void collapse(boolean notifyItemChanged) {
        int[] expandedItems = getExpandedItems();
        for (int i = expandedItems.length - 1; i >= 0; i--) {
            collapse(expandedItems[i], notifyItemChanged);
        }
    }


    /**
     * collapses (closes) the given collapsible item at the given position
     *
     * @param position the global position
     */
    public void collapse(int position) {
        collapse(position, false);
    }

    /**
     * collapses (closes) the given collapsible item at the given position
     *
     * @param position          the global position
     * @param notifyItemChanged true if we need to call notifyItemChanged. DEFAULT: false
     */
    public void collapse(int position, boolean notifyItemChanged) {
        Item item = mFastAdapter.getItem(position);
        if (item != null && item instanceof IExpandable) {
            IExpandable expandable = (IExpandable) item;
            //as we now know the item we will collapse we can collapse all subitems
            //if this item is not already collapsed and has sub items we go on
            if (expandable.isExpanded() && expandable.getSubItems() != null && expandable.getSubItems().size() > 0) {
                if (mFastAdapter.isPositionBasedStateManagement()) {
                    //first we find out how many items were added in total
                    int totalAddedItems = expandable.getSubItems().size();

                    int length = mExpanded.size();
                    for (int i = 0; i < length; i++) {
                        if (mExpanded.keyAt(i) > position && mExpanded.keyAt(i) <= position + totalAddedItems) {
                            totalAddedItems = totalAddedItems + mExpanded.get(mExpanded.keyAt(i));
                        }
                    }

                    //we will deselect starting with the lowest one
                    Iterator<Integer> selectionsIterator = mFastAdapter.getSelections().iterator();
                    while (selectionsIterator.hasNext()) {
                        Integer value = selectionsIterator.next();
                        if (value > position && value <= position + totalAddedItems) {
                            mFastAdapter.deselect(value, selectionsIterator);
                        }
                    }

                    //now we start to collapse them
                    for (int i = length - 1; i >= 0; i--) {
                        if (mExpanded.keyAt(i) > position && mExpanded.keyAt(i) <= position + totalAddedItems) {
                            //we collapsed those items now we remove update the added items
                            totalAddedItems = totalAddedItems - mExpanded.get(mExpanded.keyAt(i));

                            //we collapse the item
                            internalCollapse(mExpanded.keyAt(i), notifyItemChanged);
                        }
                    }

                    //we collapse our root element
                    internalCollapse(expandable, position, notifyItemChanged);
                } else {
                    //first we find out how many items were added in total
                    //also counting subitems
                    int totalAddedItems = expandable.getSubItems().size();
                    for (int i = position + 1; i < position + totalAddedItems; i++) {
                        Item tmp = mFastAdapter.getItem(i);
                        if (tmp instanceof IExpandable) {
                            IExpandable tmpExpandable = ((IExpandable) tmp);
                            if (tmpExpandable.getSubItems() != null && tmpExpandable.isExpanded()) {
                                totalAddedItems = totalAddedItems + tmpExpandable.getSubItems().size();
                            }
                        }
                    }

                    //why... WHY?!
                    for (int i = position + totalAddedItems - 1; i > position; i--) {
                        Item tmp = mFastAdapter.getItem(i);
                        if (tmp instanceof IExpandable) {
                            IExpandable tmpExpandable = ((IExpandable) tmp);
                            if (tmpExpandable.isExpanded()) {
                                collapse(i);
                                if (tmpExpandable.getSubItems() != null) {
                                    i = i - tmpExpandable.getSubItems().size();
                                }
                            }
                        }
                    }

                    //we collapse our root element
                    internalCollapse(expandable, position, notifyItemChanged);
                }
            }
        }
    }

    private void internalCollapse(int position, boolean notifyItemChanged) {
        Item item = mFastAdapter.getItem(position);
        if (item != null && item instanceof IExpandable) {
            IExpandable expandable = (IExpandable) item;
            //if this item is not already collapsed and has sub items we go on
            if (expandable.isExpanded() && expandable.getSubItems() != null && expandable.getSubItems().size() > 0) {
                internalCollapse(expandable, position, notifyItemChanged);
            }
        }
    }

    private void internalCollapse(IExpandable expandable, int position, boolean notifyItemChanged) {
        IAdapter adapter = mFastAdapter.getAdapter(position);
        if (adapter != null && adapter instanceof IItemAdapter) {
            ((IItemAdapter) adapter).removeRange(position + 1, expandable.getSubItems().size());
        }

        //remember that this item is now collapsed again
        expandable.withIsExpanded(false);
        //remove the information that this item was opened

        if (mFastAdapter.isPositionBasedStateManagement()) {
            int indexOfKey = mExpanded.indexOfKey(position);
            if (indexOfKey >= 0) {
                mExpanded.removeAt(indexOfKey);
            }
        }
        //we need to notify to get the correct drawable if there is one showing the current state
        if (notifyItemChanged) {
            mFastAdapter.notifyItemChanged(position);
        }
    }

    /**
     * expands all expandable items
     */
    public void expand() {
        expand(false);
    }

    /**
     * expands all expandable items
     *
     * @param notifyItemChanged true if we need to call notifyItemChanged. DEFAULT: false
     */
    public void expand(boolean notifyItemChanged) {
        int length = mFastAdapter.getItemCount();
        for (int i = length - 1; i >= 0; i--) {
            expand(i, notifyItemChanged);
        }
    }

    /**
     * opens the expandable item at the given position
     *
     * @param position the global position
     */
    public void expand(int position) {
        expand(position, false);
    }


    /**
     * opens the expandable item at the given position
     *
     * @param position          the global position
     * @param notifyItemChanged true if we need to call notifyItemChanged. DEFAULT: false
     */
    public void expand(int position, boolean notifyItemChanged) {
        Item item = mFastAdapter.getItem(position);
        if (item != null && item instanceof IExpandable) {
            IExpandable expandable = (IExpandable) item;

            if (mFastAdapter.isPositionBasedStateManagement()) {
                //if this item is not already expanded and has sub items we go on
                if (mExpanded.indexOfKey(position) < 0 && expandable.getSubItems() != null && expandable.getSubItems().size() > 0) {
                    IAdapter<Item> adapter = mFastAdapter.getAdapter(position);
                    if (adapter != null && adapter instanceof IItemAdapter) {
                        ((IItemAdapter<Item>) adapter).add(position + 1, expandable.getSubItems());
                    }

                    //remember that this item is now opened (not collapsed)
                    expandable.withIsExpanded(true);

                    //we need to notify to get the correct drawable if there is one showing the current state
                    if (notifyItemChanged) {
                        mFastAdapter.notifyItemChanged(position);
                    }

                    //store it in the list of opened expandable items
                    mExpanded.put(position, expandable.getSubItems() != null ? expandable.getSubItems().size() : 0);
                }
            } else {
                //if this item is not already expanded and has sub items we go on
                if (!expandable.isExpanded() && expandable.getSubItems() != null && expandable.getSubItems().size() > 0) {
                    IAdapter<Item> adapter = mFastAdapter.getAdapter(position);
                    if (adapter != null && adapter instanceof IItemAdapter) {
                        ((IItemAdapter<Item>) adapter).add(position + 1, expandable.getSubItems());
                    }

                    //remember that this item is now opened (not collapsed)
                    expandable.withIsExpanded(true);

                    //we need to notify to get the correct drawable if there is one showing the current state
                    if (notifyItemChanged) {
                        mFastAdapter.notifyItemChanged(position);
                    }
                }
            }
        }
    }

    /**
     * calculates the count of expandable items before a given position
     *
     * @param from     the global start position you should pass here the count of items of the previous adapters (or 0 if you want to start from the beginning)
     * @param position the global position
     * @return the count of expandable items before a given position
     */
    public int getExpandedItemsCount(int from, int position) {
        int totalAddedItems = 0;
        if (mFastAdapter.isPositionBasedStateManagement()) {
            for (int i = 0, size = mExpanded.size(); i < size; i++) {
                //now we count the amount of expanded items within our range we check
                if (mExpanded.keyAt(i) >= from && mExpanded.keyAt(i) < position) {
                    totalAddedItems = totalAddedItems + mExpanded.get(mExpanded.keyAt(i));
                } else if (mExpanded.keyAt(i) >= position) {
                    //we do not care about all expanded items which are outside our range
                    break;
                }
            }
        } else {
            //first we find out how many items were added in total
            //also counting subItems
            Item tmp;
            for (int i = from; i < position; i++) {
                tmp = mFastAdapter.getItem(i);
                if (tmp instanceof IExpandable) {
                    IExpandable tmpExpandable = ((IExpandable) tmp);
                    if (tmpExpandable.getSubItems() != null && tmpExpandable.isExpanded()) {
                        totalAddedItems = totalAddedItems + tmpExpandable.getSubItems().size();
                    }
                }
            }
        }
        return totalAddedItems;
    }
}
