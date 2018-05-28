package com.mikepenz.fastadapter.expandable;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.ArraySet;
import android.util.SparseIntArray;
import android.view.MotionEvent;
import android.view.View;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.IAdapterExtension;
import com.mikepenz.fastadapter.IExpandable;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.IItemAdapter;
import com.mikepenz.fastadapter.ISubItem;
import com.mikepenz.fastadapter.select.SelectExtension;
import com.mikepenz.fastadapter.utils.AdapterPredicate;
import com.mikepenz.fastadapter.utils.AdapterUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mikepenz on 04/06/2017.
 */

public class ExpandableExtension<Item extends IItem> implements IAdapterExtension<Item> {
    protected static final String BUNDLE_EXPANDED = "bundle_expanded";
    protected static final String BUNDLE_EXPANDED_SELECTIONS = "bundle_expanded_selections";

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
        ArrayList<String> expandedItems = savedInstanceState.getStringArrayList(BUNDLE_EXPANDED + prefix);
        String id;
        for (int i = 0, size = mFastAdapter.getItemCount(); i < size; i++) {
            Item item = mFastAdapter.getItem(i);
            id = String.valueOf(item.getIdentifier());
            if (expandedItems != null && expandedItems.contains(id)) {
                expand(i);
                size = mFastAdapter.getItemCount();
            }
        }
    }

    @Override
    public void saveInstanceState(Bundle savedInstanceState, String prefix) {
        if (savedInstanceState == null) {
            return;
        }
        ArrayList<String> expandedItems = new ArrayList<>();

        Item item;
        for (int i = 0, size = mFastAdapter.getItemCount(); i < size; i++) {
            item = mFastAdapter.getItem(i);
            if (item instanceof IExpandable && ((IExpandable) item).isExpanded()) {
                expandedItems.add(String.valueOf(item.getIdentifier()));
            }
        }
        //remember the collapsed states
        savedInstanceState.putStringArrayList(BUNDLE_EXPANDED + prefix, expandedItems);
    }

    @Override
    public boolean onClick(@NonNull View v, int pos, @NonNull FastAdapter<Item> fastAdapter, @NonNull Item item) {
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
                int[] expandedItems = getExpandedItemsSameLevel(pos);
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
    }

    @Override
    public void notifyAdapterItemRangeInserted(int position, int itemCount) {
    }

    @Override
    public void notifyAdapterItemRangeRemoved(int position, int itemCount) {
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
            Item item = mFastAdapter.getItem(position);
            if (item instanceof IExpandable && ((IExpandable) item).isExpanded()) {
                collapse(position);
            }
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
     * returns the expanded items this contains position and the count of items
     * which are expanded by this position
     *
     * @return the expanded items
     */
    public SparseIntArray getExpanded() {
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

    /**
     * @return a set with the global positions of all expanded items
     */
    public int[] getExpandedItems() {
        int[] expandedItems;
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
        return expandedItems;
    }

    /**
     * @param position the global position of the current item
     * @return a set with the global positions of all expanded items on the same level as the current item
     */
    public int[] getExpandedItemsSameLevel(int position) {
        Item item = mFastAdapter.getItem(position);
        if (!(item instanceof ISubItem)) {
            //if it isn't a SubItem, has to be on the root level
            return getExpandedItemsRootLevel(position);
        } else {
            IItem parent = ((ISubItem) item).getParent();
            if (!(parent instanceof IExpandable)) {
                //if it has no parent, has to be on the root level
                return getExpandedItemsRootLevel(position);
            }

            //if it is a SubItem and has a parent, only return the expanded items on the same level
            int[] expandedItems;
            ArrayList<Integer> expandedItemsList = new ArrayList<>();
            for (Object subItem : ((IExpandable) parent).getSubItems()) {
                if (subItem instanceof IExpandable && ((IExpandable) subItem).isExpanded() && subItem != item) {
                    expandedItemsList.add(mFastAdapter.getPosition((Item) subItem));
                }
            }
            int expandedItemsListLength = expandedItemsList.size();
            expandedItems = new int[expandedItemsListLength];
            for (int i = 0; i < expandedItemsListLength; i++) {
                expandedItems[i] = expandedItemsList.get(i);
            }
            return expandedItems;
        }
    }

    /**
     * @param position the global position of the current item
     * @return a set with the global positions of all expanded items on the root level
     */
    public int[] getExpandedItemsRootLevel(int position) {
        int[] expandedItems;
        ArrayList<Integer> expandedItemsList = new ArrayList<>();
        Item item = mFastAdapter.getItem(position);

        for (int i = 0, size = mFastAdapter.getItemCount(); i < size; i++) {
            Item currItem = mFastAdapter.getItem(i);
            if (currItem instanceof ISubItem) {
                IItem parent = ((ISubItem) currItem).getParent();
                if (parent instanceof IExpandable && ((IExpandable) parent).isExpanded()) {
                    i += ((IExpandable) parent).getSubItems().size();
                    if (parent != item)
                        expandedItemsList.add(mFastAdapter.getPosition((Item) parent));
                }
            }
        }

        int expandedItemsListLength = expandedItemsList.size();
        expandedItems = new int[expandedItemsListLength];
        for (int i = 0; i < expandedItemsListLength; i++) {
            expandedItems[i] = expandedItemsList.get(i);
        }
        return expandedItems;
    }

    /**
     * toggles the expanded state of the given expandable item at the given position
     *
     * @param position the global position
     */
    public void toggleExpandable(int position) {
        Item item = mFastAdapter.getItem(position);
        if (item instanceof IExpandable && ((IExpandable) item).isExpanded()) {
            collapse(position);
        } else {
            expand(position);
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
        final int[] expandedItemsCount = {0};
        mFastAdapter.recursive(new AdapterPredicate<Item>() {
            ArraySet<IItem> allowedParents = new ArraySet<>();

            @Override
            public boolean apply(@NonNull IAdapter<Item> lastParentAdapter, int lastParentPosition, @NonNull Item item, int position) {
                //we do not care about non visible items
                if (position == -1) {
                    return false;
                }

                //this is the entrance parent
                if (allowedParents.size() > 0 && item instanceof ISubItem) {
                    // Go on until we hit an item with a parent which was not in our expandable hierarchy
                    IItem parent = ((ISubItem) item).getParent();
                    if (parent == null || !allowedParents.contains(parent)) {
                        return true;
                    }
                }

                if (item instanceof IExpandable) {
                    IExpandable expandable = (IExpandable) item;
                    if (expandable.isExpanded()) {
                        expandable.withIsExpanded(false);

                        if (expandable.getSubItems() != null) {
                            expandedItemsCount[0] += expandable.getSubItems().size();
                            allowedParents.add(item);
                        }
                    }
                }

                return false;
            }
        }, position, true);

        IAdapter adapter = mFastAdapter.getAdapter(position);
        if (adapter != null && adapter instanceof IItemAdapter) {
            ((IItemAdapter) adapter).removeRange(position + 1, expandedItemsCount[0]);
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
            //if this item is not already expanded and has sub items we go on
            if (!expandable.isExpanded() && expandable.getSubItems() != null && expandable.getSubItems().size() > 0) {
                IAdapter<Item> adapter = mFastAdapter.getAdapter(position);
                if (adapter != null && adapter instanceof IItemAdapter) {
                    ((IItemAdapter<?, Item>) adapter).addInternal(position + 1, expandable.getSubItems());
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

    /**
     * calculates the count of expandable items before a given position
     *
     * @param from     the global start position you should pass here the count of items of the previous adapters (or 0 if you want to start from the beginning)
     * @param position the global position
     * @return the count of expandable items before a given position
     */
    public int getExpandedItemsCount(int from, int position) {
        int totalAddedItems = 0;
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
        return totalAddedItems;
    }

    /**
     * deselects all selections
     */
    public void deselect() {
        SelectExtension<Item> selectExtension = mFastAdapter.getExtension(SelectExtension.class);
        if (selectExtension == null) {
            return;
        }
        for (Item item : AdapterUtil.getAllItems(mFastAdapter)) {
            selectExtension.deselect(item);
        }
        mFastAdapter.notifyDataSetChanged();
    }

    /**
     * select all items
     *
     * @param considerSelectableFlag true if the select method should not select an item if its not selectable
     */
    public void select(boolean considerSelectableFlag) {
        SelectExtension<Item> selectExtension = mFastAdapter.getExtension(SelectExtension.class);
        if (selectExtension == null) {
            return;
        }
        for (Item item : AdapterUtil.getAllItems(mFastAdapter)) {
            selectExtension.select(item, considerSelectableFlag);
        }
        mFastAdapter.notifyDataSetChanged();
    }

}
