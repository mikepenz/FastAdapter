package com.mikepenz.fastadapter.select;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.util.ArraySet;
import android.view.MotionEvent;
import android.view.View;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.IAdapterExtension;
import com.mikepenz.fastadapter.IExpandable;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.IItemAdapter;
import com.mikepenz.fastadapter.ISelectionListener;
import com.mikepenz.fastadapter.ISubItem;
import com.mikepenz.fastadapter.utils.AdapterPredicate;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

/**
 * Created by mikepenz on 04/06/2017.
 */

public class SelectExtension<Item extends IItem> implements IAdapterExtension<Item> {
    protected static final String BUNDLE_SELECTIONS = "bundle_selections";

    //
    private FastAdapter<Item> mFastAdapter;
    // if enabled we will select the item via a notifyItemChanged -> will animate with the Animator
    // you can also use this if you have any custom logic for selections, and do not depend on the "selected" state of the view
    // note if enabled it will feel a bit slower because it will animate the selection
    private boolean mSelectWithItemUpdate = false;
    // if we want multiSelect enabled
    private boolean mMultiSelect = false;
    // if we want the multiSelect only on longClick
    private boolean mSelectOnLongClick = false;
    // if a user can deselect a selection via click. required if there is always one selected item!
    private boolean mAllowDeselection = true;
    // if items are selectable in general
    private boolean mSelectable = false;

    //listeners we can attach
    private ISelectionListener<Item> mSelectionListener;


    /**
     * select between the different selection behaviors.
     * there are now 2 different variants of selection. you can toggle this via `withSelectWithItemUpdate(boolean)` (where false == default - variant 1)
     * 1.) direct selection via the view "selected" state, we also make sure we do not animate here so no notifyItemChanged is called if we repeatly press the same item
     * 2.) we select the items via a notifyItemChanged. this will allow custom selected logics within your views (isSelected() - do something...) and it will also animate the change via the provided itemAnimator. because of the animation of the itemAnimator the selection will have a small delay (time of animating)
     *
     * @param selectWithItemUpdate true if notifyItemChanged should be called upon select
     * @return this
     */
    public SelectExtension<Item> withSelectWithItemUpdate(boolean selectWithItemUpdate) {
        this.mSelectWithItemUpdate = selectWithItemUpdate;
        return this;
    }

    /**
     * Enable this if you want multiSelection possible in the list
     *
     * @param multiSelect true to enable multiSelect
     * @return this
     */
    public SelectExtension<Item> withMultiSelect(boolean multiSelect) {
        mMultiSelect = multiSelect;
        return this;
    }

    /**
     * Disable this if you want the selection on a single tap
     *
     * @param selectOnLongClick false to do select via single click
     * @return this
     */
    public SelectExtension<Item> withSelectOnLongClick(boolean selectOnLongClick) {
        mSelectOnLongClick = selectOnLongClick;
        return this;
    }

    /**
     * If false, a user can't deselect an item via click (you can still do this programmatically)
     *
     * @param allowDeselection true if a user can deselect an already selected item via click
     * @return this
     */
    public SelectExtension<Item> withAllowDeselection(boolean allowDeselection) {
        this.mAllowDeselection = allowDeselection;
        return this;
    }

    /**
     * set if no item is selectable
     *
     * @param selectable true if items are selectable
     * @return this
     */
    public SelectExtension<Item> withSelectable(boolean selectable) {
        this.mSelectable = selectable;
        return this;
    }

    /**
     * @return if items are selectable
     */
    public boolean isSelectable() {
        return mSelectable;
    }

    /**
     * set a listener that get's notified whenever an item is selected or deselected
     *
     * @param selectionListener the listener that will be notified about selection changes
     * @return this
     */
    public SelectExtension<Item> withSelectionListener(ISelectionListener<Item> selectionListener) {
        this.mSelectionListener = selectionListener;
        return this;
    }

    @Override
    public IAdapterExtension<Item> init(FastAdapter<Item> fastAdapter) {
        mFastAdapter = fastAdapter;
        return null;
    }

    @Override
    public void withSavedInstanceState(@Nullable Bundle savedInstanceState, String prefix) {
        if (savedInstanceState == null) {
            return;
        }
        long[] selectedItems = savedInstanceState.getLongArray(BUNDLE_SELECTIONS + prefix);
        if (selectedItems != null) {
            for (long id : selectedItems) {
                selectByIdentifier(id, false, true);
            }
        }
    }

    @Override
    public void saveInstanceState(@Nullable Bundle savedInstanceState, String prefix) {
        if (savedInstanceState == null) {
            return;
        }

        Set<Item> selections = mFastAdapter.getSelectedItems();
        long[] selectionsArray = new long[selections.size()];
        int i = 0;
        for (Item item : selections) {
            selectionsArray[i] = item.getIdentifier();
            i++;
        }
        //remember the selections
        savedInstanceState.putLongArray(BUNDLE_SELECTIONS + prefix, selectionsArray);
    }

    @Override
    public boolean onClick(View v, int pos, FastAdapter<Item> fastAdapter, Item item) {
        //handle the selection if the event was not yet consumed, and we are allowed to select an item (only occurs when we select with long click only)
        //this has to happen before expand or collapse. otherwise the position is wrong which is used to select
        if (!mSelectOnLongClick && mSelectable) {
            handleSelection(v, item, pos);
        }
        return false;
    }

    @Override
    public boolean onLongClick(View v, int pos, FastAdapter<Item> fastAdapter, Item item) {
        //now handle the selection if we are in multiSelect mode and allow selecting on longClick
        if (mSelectOnLongClick && mSelectable) {
            handleSelection(v, item, pos);
        }
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
    }

    @Override
    public void notifyAdapterItemRangeChanged(int position, int itemCount, @Nullable Object payload) {
    }

    @Override
    public void set(List<Item> items, boolean resetFilter) {

    }

    @Override
    public void performFiltering(CharSequence constraint) {

    }

    //-------------------------
    //-------------------------
    //Selection stuff
    //-------------------------
    //-------------------------

    /**
     * @return a set with the global positions of all selected items (which are currently in the list (includes expanded expandable items))
     */
    public Set<Integer> getSelections() {
        Set<Integer> selections = new ArraySet<>();
        for (int i = 0, size = mFastAdapter.getItemCount(); i < size; i++) {
            if (mFastAdapter.getItem(i).isSelected()) {
                selections.add(i);
            }
        }
        return selections;
    }

    /**
     * @return a set with all items which are currently selected (includes subitems)
     */
    public Set<Item> getSelectedItems() {
        final Set<Item> items = new ArraySet<>();
        mFastAdapter.recursive(new AdapterPredicate<Item>() {
            @Override
            public boolean apply(@NonNull IAdapter<Item> lastParentAdapter, int lastParentPosition, Item item, int position) {
                if (item.isSelected()) {
                    items.add(item);
                }
                return false;
            }
        }, false);
        return items;
    }

    /**
     * toggles the selection of the item at the given position
     *
     * @param position the global position
     */
    public void toggleSelection(int position) {
        if (mFastAdapter.getItem(position).isSelected()) {
            deselect(position);
        } else {
            select(position);
        }
    }

    /**
     * handles the selection and deselects item if multiSelect is disabled
     *
     * @param position the global position
     */
    private void handleSelection(@Nullable View view, Item item, int position) {
        //if this item is not selectable don't continue
        if (!item.isSelectable()) {
            return;
        }

        //if we have disabled deselection via click don't continue
        if (item.isSelected() && !mAllowDeselection) {
            return;
        }

        boolean selected = item.isSelected();

        if (mSelectWithItemUpdate || view == null) {
            if (!mMultiSelect) {
                deselect();
            }
            if (selected) {
                deselect(position);
            } else {
                select(position);
            }
        } else {
            if (!mMultiSelect) {
                //we have to separately handle deselection here because if we toggle the current item we do not want to deselect this first!
                Set<Item> selections = getSelectedItems();
                selections.remove(item);
                deselectByItems(selections);
            }

            //we toggle the state of the view
            item.withSetSelected(!selected);
            view.setSelected(!selected);

            //notify that the selection changed
            if (mSelectionListener != null)
                mSelectionListener.onSelectionChanged(item, !selected);
        }
    }


    /**
     * select all items
     */
    public void select() {
        select(false);
    }

    /**
     * select all items
     *
     * @param considerSelectableFlag true if the select method should not select an item if its not selectable
     */
    public void select(final boolean considerSelectableFlag) {
        mFastAdapter.recursive(new AdapterPredicate<Item>() {
            @Override
            public boolean apply(@NonNull IAdapter<Item> lastParentAdapter, int lastParentPosition, Item item, int position) {
                select(lastParentAdapter, item, -1, false, considerSelectableFlag);
                return false;
            }
        }, false);
        mFastAdapter.notifyDataSetChanged();
    }

    /**
     * select's a provided item, this won't notify the adapter
     *
     * @param item                   the item to select
     * @param considerSelectableFlag true if the select method should not select an item if its not selectable
     */
    public void select(Item item, boolean considerSelectableFlag) {
        if (considerSelectableFlag && !item.isSelectable()) {
            return;
        }
        item.withSetSelected(true);
        if (mSelectionListener != null) {
            mSelectionListener.onSelectionChanged(item, true);
        }
    }

    /**
     * selects all items at the positions in the iteratable
     *
     * @param positions the global positions to select
     */
    public void select(Iterable<Integer> positions) {
        for (Integer position : positions) {
            select(position);
        }
    }

    /**
     * selects an item and remembers its position in the selections list
     *
     * @param position the global position
     */
    public void select(int position) {
        select(position, false);
    }

    /**
     * selects an item and remembers its position in the selections list
     *
     * @param position  the global position
     * @param fireEvent true if the onClick listener should be called
     */
    public void select(int position, boolean fireEvent) {
        select(position, fireEvent, false);
    }

    /**
     * selects an item and remembers its position in the selections list
     *
     * @param position               the global position
     * @param fireEvent              true if the onClick listener should be called
     * @param considerSelectableFlag true if the select method should not select an item if its not selectable
     */
    public void select(int position, boolean fireEvent, boolean considerSelectableFlag) {
        FastAdapter.RelativeInfo<Item> relativeInfo = mFastAdapter.getRelativeInfo(position);
        if (relativeInfo == null || relativeInfo.item == null) {
            return;
        }
        select(relativeInfo.adapter, relativeInfo.item, position, fireEvent, considerSelectableFlag);
    }

    /**
     * selects an item and remembers its position in the selections list
     *
     * @param adapter                adapter holding this item (or it's parent)
     * @param item                   the item to select
     * @param position               the global position (or &lt; 0 if the item is not displayed)
     * @param fireEvent              true if the onClick listener should be called
     * @param considerSelectableFlag true if the select method should not select an item if its not selectable
     */
    public void select(IAdapter<Item> adapter, Item item, int position, boolean fireEvent, boolean considerSelectableFlag) {
        if (considerSelectableFlag && !item.isSelectable()) {
            return;
        }

        item.withSetSelected(true);

        mFastAdapter.notifyItemChanged(position);

        if (mSelectionListener != null)
            mSelectionListener.onSelectionChanged(item, true);

        if (mFastAdapter.getOnClickListener() != null && fireEvent) {
            mFastAdapter.getOnClickListener().onClick(null, adapter, item, position);
        }
    }

    /**
     * selects an item by it's identifier
     *
     * @param identifier             the identifier of the item to select
     * @param fireEvent              true if the onClick listener should be called
     * @param considerSelectableFlag true if the select method should not select an item if its not selectable
     */
    public void selectByIdentifier(final long identifier, final boolean fireEvent, final boolean considerSelectableFlag) {
        mFastAdapter.recursive(new AdapterPredicate<Item>() {
            @Override
            public boolean apply(@NonNull IAdapter<Item> lastParentAdapter, int lastParentPosition, Item item, int position) {
                if (item.getIdentifier() == identifier) {
                    select(lastParentAdapter, item, position, fireEvent, considerSelectableFlag);
                    return true;
                }
                return false;
            }
        }, true);
    }

    /**
     * @param identifiers            the set of identifiers to select
     * @param fireEvent              true if the onClick listener should be called
     * @param considerSelectableFlag true if the select method should not select an item if its not selectable
     */
    public void selectByIdentifiers(final Set<Long> identifiers, final boolean fireEvent, final boolean considerSelectableFlag) {
        mFastAdapter.recursive(new AdapterPredicate<Item>() {
            @Override
            public boolean apply(@NonNull IAdapter<Item> lastParentAdapter, int lastParentPosition, Item item, int position) {
                if (identifiers.contains(item.getIdentifier())) {
                    select(lastParentAdapter, item, position, fireEvent, considerSelectableFlag);
                }
                return false;
            }
        }, false);
    }

    /**
     * deselects all selections
     */
    public void deselect() {
        mFastAdapter.recursive(new AdapterPredicate<Item>() {
            @Override
            public boolean apply(@NonNull IAdapter<Item> lastParentAdapter, int lastParentPosition, Item item, int position) {
                deselect(item);
                return false;
            }
        }, false);
        mFastAdapter.notifyDataSetChanged();
    }

    /**
     * deselect's a provided item, this won't notify the adapter
     *
     * @param item the item to select
     */
    public void deselect(Item item) {
        deselect(item, -1, null);
    }

    /**
     * deselects all items at the positions in the iteratable
     *
     * @param positions the global positions to deselect
     */
    public void deselect(Iterable<Integer> positions) {
        Iterator<Integer> entries = positions.iterator();
        while (entries.hasNext()) {
            deselect(entries.next(), entries);
        }
    }

    /**
     * deselects an item and removes its position in the selections list
     *
     * @param position the global position
     */
    public void deselect(int position) {
        deselect(position, null);
    }

    /**
     * deselects an item and removes its position in the selections list
     * also takes an iterator to remove items from the map
     *
     * @param position the global position
     * @param entries  the iterator which is used to deselect all
     */
    public void deselect(int position, @Nullable Iterator<Integer> entries) {
        Item item = mFastAdapter.getItem(position);
        if (item == null) {
            return;
        }
        deselect(item, position, entries);
    }

    /**
     * deselects an item and removes its position in the selections list
     * also takes an iterator to remove items from the map
     *
     * @param item     the item to deselected
     * @param position the global position (or &lt; 0 if the item is not displayed)
     * @param entries  the iterator which is used to deselect all
     */

    public void deselect(Item item, int position, @Nullable Iterator<Integer> entries) {
        item.withSetSelected(false);
        if (entries != null) {
            entries.remove();
        }
        if (position >= 0) {
            mFastAdapter.notifyItemChanged(position);
        }

        if (mSelectionListener != null) {
            mSelectionListener.onSelectionChanged(item, false);
        }
    }


    /**
     * selects an item by it's identifier
     *
     * @param identifier the identifier of the item to deselect
     */
    public void deselectByIdentifier(final long identifier) {
        mFastAdapter.recursive(new AdapterPredicate<Item>() {
            @Override
            public boolean apply(@NonNull IAdapter<Item> lastParentAdapter, int lastParentPosition, Item item, int position) {
                if (item.getIdentifier() == identifier) {
                    deselect(item, position, null);
                    return true;
                }
                return false;
            }
        }, true);
    }

    /**
     * @param identifiers the set of identifiers to deselect
     */
    public void deselectByIdentifiers(final Set<Long> identifiers) {
        mFastAdapter.recursive(new AdapterPredicate<Item>() {
            @Override
            public boolean apply(@NonNull IAdapter<Item> lastParentAdapter, int lastParentPosition, Item item, int position) {
                if (identifiers.contains(item.getIdentifier())) {
                    deselect(item, position, null);
                }
                return false;
            }
        }, false);
    }

    /**
     * @param items the set of items to deselect. They require a identifier.
     */
    public void deselectByItems(final Set<Item> items) {
        mFastAdapter.recursive(new AdapterPredicate<Item>() {
            @Override
            public boolean apply(@NonNull IAdapter<Item> lastParentAdapter, int lastParentPosition, Item item, int position) {
                if (items.contains(item)) {
                    deselect(item, position, null);
                }
                return false;
            }
        }, false);
    }

    /**
     * deletes all current selected items
     *
     * @return a list of the IItem elements which were deleted
     */
    public List<Item> deleteAllSelectedItems() {
        List<Item> deletedItems = new ArrayList<>();

        final List<Integer> positions = new ArrayList<>();
        mFastAdapter.recursive(new AdapterPredicate<Item>() {
            @Override
            public boolean apply(@NonNull IAdapter<Item> lastParentAdapter, int lastParentPosition, Item item, int position) {
                if (item.isSelected()) {
                    //if it's a subitem remove it from the parent
                    if (item instanceof ISubItem) {
                        //a sub item which is not in the list can be instantly deleted
                        IExpandable parent = (IExpandable) ((ISubItem) item).getParent();
                        //parent should not be null, but check in any case..
                        if (parent != null) {
                            parent.getSubItems().remove(item);
                        }
                    }
                    if (position != -1) {
                        //a normal displayed item can only be deleted afterwards
                        positions.add(position);
                    }
                }
                return false;
            }
        }, false);

        //we have to re-fetch the selections array again and again as the position will change after one item is deleted
        for (int i = positions.size() - 1; i >= 0; i--) {
            FastAdapter.RelativeInfo<Item> ri = mFastAdapter.getRelativeInfo(positions.get(i));
            if (ri.item != null && ri.item.isSelected()) { //double verify
                if (ri.adapter != null && ri.adapter instanceof IItemAdapter) {
                    ((IItemAdapter) ri.adapter).remove(positions.get(i));
                }
            }
        }
        return deletedItems;
    }
}
