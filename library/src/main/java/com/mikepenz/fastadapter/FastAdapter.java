package com.mikepenz.fastadapter;

import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.mikepenz.fastadapter.utils.AdapterUtil;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Created by mikepenz on 27.12.15.
 */
public class FastAdapter<Item extends IItem> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    protected static final String BUNDLE_SELECTIONS = "bundle_selections";
    protected static final String BUNDLE_EXPANDED = "bundle_expanded";

    // we remember all adapters
    //priority queue...
    final private ArrayMap<Integer, IAdapter<Item>> mAdapters = new ArrayMap<>();
    // we remember all possible types so we can create a new view efficiently
    final private ArrayMap<Integer, Item> mTypeInstances = new ArrayMap<>();
    // cache the sizes of the different adapters so we can access the items more performant
    final private NavigableMap<Integer, IAdapter<Item>> mAdapterSizes = new TreeMap<>();
    // the total size
    private int mGlobalSize = 0;

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
    // only one expanded section
    private boolean mOnlyOneExpandedItem = false;

    // we need to remember all selections to recreate them after orientation change
    private SortedSet<Integer> mSelections = new TreeSet<>();
    // we need to remember all expanded items to recreate them after orientation change
    private SparseIntArray mExpanded = new SparseIntArray();

    // the listeners which can be hooked on an item
    private OnClickListener<Item> mOnPreClickListener;
    private OnClickListener<Item> mOnClickListener;
    private OnLongClickListener<Item> mOnPreLongClickListener;
    private OnLongClickListener<Item> mOnLongClickListener;
    private OnTouchListener<Item> mOnTouchListener;

    //the listeners for onCreateViewHolder or onBindViewHolder
    private OnCreateViewHolderListener mOnCreateViewHolderListener = new OnCreateViewHolderListenerImpl();
    private OnBindViewHolderListener mOnBindViewHolderListener = new OnBindViewHolderListenerImpl();

    /**
     * default CTOR
     */
    public FastAdapter() {
        setHasStableIds(true);
    }

    /**
     * Define the OnClickListener which will be used for a single item
     *
     * @param onClickListener the OnClickListener which will be used for a single item
     * @return this
     */
    public FastAdapter<Item> withOnClickListener(OnClickListener<Item> onClickListener) {
        this.mOnClickListener = onClickListener;
        return this;
    }

    /**
     * Define the OnPreClickListener which will be used for a single item and is called after all internal methods are done
     *
     * @param onPreClickListener the OnPreClickListener which will be called after a single item was clicked and all internal methods are done
     * @return this
     */
    public FastAdapter<Item> withOnPreClickListener(OnClickListener<Item> onPreClickListener) {
        this.mOnPreClickListener = onPreClickListener;
        return this;
    }

    /**
     * Define the OnLongClickListener which will be used for a single item
     *
     * @param onLongClickListener the OnLongClickListener which will be used for a single item
     * @return this
     */
    public FastAdapter<Item> withOnLongClickListener(OnLongClickListener<Item> onLongClickListener) {
        this.mOnLongClickListener = onLongClickListener;
        return this;
    }

    /**
     * Define the OnLongClickListener which will be used for a single item and is called after all internal methods are done
     *
     * @param onPreLongClickListener the OnLongClickListener which will be called after a single item was clicked and all internal methods are done
     * @return this
     */
    public FastAdapter<Item> withOnPreLongClickListener(OnLongClickListener<Item> onPreLongClickListener) {
        this.mOnPreLongClickListener = onPreLongClickListener;
        return this;
    }

    /**
     * Define the TouchListener which will be used for a single item
     *
     * @param onTouchListener the TouchListener which will be used for a single item
     * @return this
     */
    public FastAdapter<Item> withOnTouchListener(OnTouchListener<Item> onTouchListener) {
        this.mOnTouchListener = onTouchListener;
        return this;
    }

    /**
     * allows you to set a custom OnCreateViewHolderListener which will be used before and after the ViewHolder is created
     * You may check the OnCreateViewHolderListenerImpl for the default behavior
     *
     * @param onCreateViewHolderListener the OnCreateViewHolderListener (you may use the OnCreateViewHolderListenerImpl)
     */
    public FastAdapter<Item> withOnCreateViewHolderListener(OnCreateViewHolderListener onCreateViewHolderListener) {
        this.mOnCreateViewHolderListener = onCreateViewHolderListener;
        return this;
    }

    /**
     * allows you to set an custom OnBindViewHolderListener which is used to bind the view. This will overwrite the libraries behavior.
     * You may check the OnBindViewHolderListenerImpl for the default behavior
     *
     * @param onBindViewHolderListener the OnBindViewHolderListener
     */
    public FastAdapter<Item> withOnBindViewHolderListener(OnBindViewHolderListener onBindViewHolderListener) {
        this.mOnBindViewHolderListener = onBindViewHolderListener;
        return this;
    }

    /**
     * select between the different selection behaviors.
     * there are now 2 different variants of selection. you can toggle this via `withSelectWithItemUpdate(boolean)` (where false == default - variant 1)
     * 1.) direct selection via the view "selected" state, we also make sure we do not animate here so no notifyItemChanged is called if we repeatly press the same item
     * 2.) we select the items via a notifyItemChanged. this will allow custom selected logics within your views (isSelected() - do something...) and it will also animate the change via the provided itemAnimator. because of the animation of the itemAnimator the selection will have a small delay (time of animating)
     *
     * @param selectWithItemUpdate true if notifyItemChanged should be called upon select
     * @return this
     */
    public FastAdapter<Item> withSelectWithItemUpdate(boolean selectWithItemUpdate) {
        this.mSelectWithItemUpdate = selectWithItemUpdate;
        return this;
    }

    /**
     * Enable this if you want multiSelection possible in the list
     *
     * @param multiSelect true to enable multiSelect
     * @return this
     */
    public FastAdapter<Item> withMultiSelect(boolean multiSelect) {
        mMultiSelect = multiSelect;
        return this;
    }

    /**
     * Disable this if you want the selection on a single tap
     *
     * @param selectOnLongClick false to do select via single click
     * @return this
     */
    public FastAdapter<Item> withSelectOnLongClick(boolean selectOnLongClick) {
        mSelectOnLongClick = selectOnLongClick;
        return this;
    }

    /**
     * If false, a user can't deselect an item via click (you can still do this programmatically)
     *
     * @param allowDeselection true if a user can deselect an already selected item via click
     * @return this
     */
    public FastAdapter<Item> withAllowDeselection(boolean allowDeselection) {
        this.mAllowDeselection = allowDeselection;
        return this;
    }

    /**
     * set if no item is selectable
     *
     * @param selectable true if items are selectable
     * @return this
     */
    public FastAdapter<Item> withSelectable(boolean selectable) {
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
     * set if there should only be one opened expandable item
     * DEFAULT: false
     *
     * @param mOnlyOneExpandedItem true if there should be only one expanded, expandable item in the list
     * @return this
     */
    public FastAdapter<Item> withOnlyOneExpandedItem(boolean mOnlyOneExpandedItem) {
        this.mOnlyOneExpandedItem = mOnlyOneExpandedItem;
        return this;
    }

    /**
     * @return if there should be only one expanded, expandable item in the list
     */
    public boolean isOnlyOneExpandedItem() {
        return mOnlyOneExpandedItem;
    }

    /**
     * re-selects all elements stored in the savedInstanceState
     * IMPORTANT! Call this method only after all items where added to the adapters again. Otherwise it may select wrong items!
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down then this Bundle contains the data it most
     *                           recently supplied in Note: Otherwise it is null.
     * @return this
     */
    public FastAdapter<Item> withSavedInstanceState(Bundle savedInstanceState) {
        return withSavedInstanceState(savedInstanceState, "");
    }

    /**
     * re-selects all elements stored in the savedInstanceState
     * IMPORTANT! Call this method only after all items where added to the adapters again. Otherwise it may select wrong items!
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down then this Bundle contains the data it most
     *                           recently supplied in Note: Otherwise it is null.
     * @param prefix             a prefix added to the savedInstance key so we can store multiple states
     * @return this
     */
    public FastAdapter<Item> withSavedInstanceState(Bundle savedInstanceState, String prefix) {
        if (savedInstanceState != null) {
            //make sure already done selections are removed
            deselect();

            //first restore opened collasable items, as otherwise may not all selections could be restored
            int[] expandedItems = savedInstanceState.getIntArray(BUNDLE_EXPANDED + prefix);
            if (expandedItems != null) {
                for (Integer expandedItem : expandedItems) {
                    expand(expandedItem);
                }
            }

            //restore the selections
            int[] selections = savedInstanceState.getIntArray(BUNDLE_SELECTIONS + prefix);
            if (selections != null) {
                for (Integer selection : selections) {
                    select(selection);
                }
            }
        }
        return this;
    }

    /**
     * registers an AbstractAdapter which will be hooked into the adapter chain
     *
     * @param adapter an adapter which extends the AbstractAdapter
     */
    public <A extends AbstractAdapter<Item>> void registerAdapter(A adapter) {
        if (!mAdapters.containsKey(adapter.getOrder())) {
            mAdapters.put(adapter.getOrder(), adapter);
            cacheSizes();
        }
    }

    /**
     * register a new type into the TypeInstances to be able to efficiently create thew ViewHolders
     *
     * @param item an IItem which will be shown in the list
     */
    public void registerTypeInstance(Item item) {
        if (!mTypeInstances.containsKey(item.getType())) {
            mTypeInstances.put(item.getType(), item);
        }
    }

    /**
     * gets the TypeInstance remembered within the FastAdapter for an item
     *
     * @param type the int type of the item
     * @return the Item typeInstance
     */
    public Item getTypeInstance(int type) {
        return mTypeInstances.get(type);
    }

    /**
     * Creates the ViewHolder by the viewType
     *
     * @param parent   the parent view (the RecyclerView)
     * @param viewType the current viewType which is bound
     * @return the ViewHolder with the bound data
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final RecyclerView.ViewHolder holder = mOnCreateViewHolderListener.onPreCreateViewHolder(parent, viewType);

        //handle click behavior
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = holder.getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    boolean consumed = false;
                    RelativeInfo<Item> relativeInfo = getRelativeInfo(pos);
                    Item item = relativeInfo.item;
                    if (item != null && item.isEnabled()) {
                        //on the very first we call the click listener from the item itself (if defined)
                        if (item instanceof IClickable && ((IClickable) item).getOnPreItemClickListener() != null) {
                            consumed = ((IClickable<Item>) item).getOnPreItemClickListener().onClick(v, relativeInfo.adapter, item, pos);
                        }

                        //first call the onPreClickListener which would allow to prevent executing of any following code, including selection
                        if (!consumed && mOnPreClickListener != null) {
                            consumed = mOnPreClickListener.onClick(v, relativeInfo.adapter, item, pos);
                        }

                        //if this is a expandable item :D
                        if (!consumed && item instanceof IExpandable) {
                            if (((IExpandable) item).getSubItems() != null) {
                                toggleExpandable(pos);
                            }
                        }

                        //if there should be only one expanded item we want to collapse all the others but the current one
                        if (mOnlyOneExpandedItem) {
                            int[] expandedItems = getExpandedItems();
                            for (int i = expandedItems.length - 1; i >= 0; i--) {
                                if (expandedItems[i] != pos) {
                                    collapse(expandedItems[i], true);
                                }
                            }
                        }

                        //handle the selection if the event was not yet consumed, and we are allowed to select an item (only occurs when we select with long click only)
                        if (!consumed && !mSelectOnLongClick && mSelectable) {
                            handleSelection(v, item, pos);
                        }

                        //before calling the global adapter onClick listener call the item specific onClickListener
                        if (item instanceof IClickable && ((IClickable) item).getOnItemClickListener() != null) {
                            consumed = ((IClickable<Item>) item).getOnItemClickListener().onClick(v, relativeInfo.adapter, item, pos);
                        }

                        //call the normal click listener after selection was handlded
                        if (!consumed && mOnClickListener != null) {
                            mOnClickListener.onClick(v, relativeInfo.adapter, item, pos);
                        }
                    }
                }
            }
        });

        //handle long click behavior
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int pos = holder.getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    boolean consumed = false;
                    RelativeInfo<Item> relativeInfo = getRelativeInfo(pos);
                    if (relativeInfo.item != null && relativeInfo.item.isEnabled()) {
                        //first call the OnPreLongClickListener which would allow to prevent executing of any following code, including selection
                        if (mOnPreLongClickListener != null) {
                            consumed = mOnPreLongClickListener.onLongClick(v, relativeInfo.adapter, relativeInfo.item, pos);
                        }

                        //now handle the selection if we are in multiSelect mode and allow selecting on longClick
                        if (!consumed && mSelectOnLongClick && mSelectable) {
                            handleSelection(v, relativeInfo.item, pos);
                        }

                        //call the normal long click listener after selection was handled
                        if (mOnLongClickListener != null) {
                            consumed = mOnLongClickListener.onLongClick(v, relativeInfo.adapter, relativeInfo.item, pos);
                        }
                    }
                    return consumed;
                }
                return false;
            }
        });

        //handle touch behavior
        holder.itemView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mOnTouchListener != null) {
                    int pos = holder.getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        RelativeInfo<Item> relativeInfo = getRelativeInfo(pos);
                        return mOnTouchListener.onTouch(v, event, relativeInfo.adapter, relativeInfo.item, pos);
                    }
                }
                return false;
            }
        });

        return mOnCreateViewHolderListener.onPostCreateViewHolder(holder);
    }

    /**
     * Binds the data to the created ViewHolder and sets the listeners to the holder.itemView
     *
     * @param holder   the viewHolder we bind the data on
     * @param position the global position
     */
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        mOnBindViewHolderListener.onBindViewHolder(holder, position);
    }

    /**
     * Searches for the given item and calculates it's global position
     *
     * @param item the item which is searched for
     * @return the global position, or -1 if not found
     */
    public int getPosition(Item item) {
        if (item.getIdentifier() == -1) {
            Log.e("FastAdapter", "You have to define an identifier for your item to retrieve the position via this method");
            return -1;
        }

        int position = 0;
        int length = mAdapters.size();
        for (int i = 0; i < length; i++) {
            IAdapter<Item> adapter = mAdapters.valueAt(i);
            if (adapter.getOrder() < 0) {
                continue;
            }

            int relativePosition = adapter.getAdapterPosition(item);
            if (relativePosition != -1) {
                return position + relativePosition;
            }
            position = adapter.getAdapterItemCount();
        }

        return -1;
    }

    /**
     * gets the IItem by a position, from all registered adapters
     *
     * @param position the global position
     * @return the found IItem or null
     */
    public Item getItem(int position) {
        //if we are out of range just return null
        if (position < 0 || position >= mGlobalSize) {
            return null;
        }
        //now get the adapter which is responsible for the given position
        Map.Entry<Integer, IAdapter<Item>> entry = mAdapterSizes.floorEntry(position);
        return entry.getValue().getAdapterItem(position - entry.getKey());
    }

    /**
     * Internal method to get the Item as ItemHolder which comes with the relative position within it's adapter
     * Finds the responsible adapter for the given position
     *
     * @param position the global position
     * @return the adapter which is responsible for this position
     */
    public RelativeInfo<Item> getRelativeInfo(int position) {
        if (position < 0) {
            return new RelativeInfo<>();
        }

        RelativeInfo<Item> relativeInfo = new RelativeInfo<>();
        Map.Entry<Integer, IAdapter<Item>> entry = mAdapterSizes.floorEntry(position);
        if (entry != null) {
            relativeInfo.item = entry.getValue().getAdapterItem(position - entry.getKey());
            relativeInfo.adapter = entry.getValue();
        }
        return relativeInfo;
    }

    /**
     * Gets the adapter for the given position
     *
     * @param position the global position
     * @return the adapter responsible for this global position
     */
    public IAdapter<Item> getAdapter(int position) {
        //if we are out of range just return null
        if (position < 0 || position >= mGlobalSize) {
            return null;
        }
        //now get the adapter which is responsible for the given position
        return mAdapterSizes.floorEntry(position).getValue();
    }

    /**
     * finds the int ItemViewType from the IItem which exists at the given position
     *
     * @param position the global position
     * @return the viewType for this position
     */
    @Override
    public int getItemViewType(int position) {
        return getItem(position).getType();
    }

    /**
     * finds the int ItemId from the IItem which exists at the given position
     *
     * @param position the global position
     * @return the itemId for this position
     */
    @Override
    public long getItemId(int position) {
        return getItem(position).getIdentifier();
    }

    /**
     * calculates the total ItemCount over all registered adapters
     *
     * @return the global count
     */
    public int getItemCount() {
        return mGlobalSize;
    }

    /**
     * calculates the item count up to a given (excluding this) order number
     *
     * @param order the number up to which the items are counted
     * @return the total count of items up to the adapter order
     */
    public int getPreItemCountByOrder(int order) {
        //if we are empty just return 0 count
        if (mGlobalSize == 0) {
            return 0;
        }

        int size = 0;

        //count the number of items before the adapter with the given order
        for (IAdapter<Item> adapter : mAdapters.values()) {
            if (adapter.getOrder() == order) {
                return size;
            } else {
                size = size + adapter.getAdapterItemCount();
            }
        }

        //get the count of items which are before this order
        return size;
    }


    /**
     * calculates the item count up to a given (excluding this) adapter (defined by the global position of the item)
     *
     * @param position the global position of an adapter item
     * @return the total count of items up to the adapter which holds the given position
     */
    public int getPreItemCount(int position) {
        //if we are empty just return 0 count
        if (mGlobalSize == 0) {
            return 0;
        }

        //get the count of items which are before this order
        return mAdapterSizes.floorKey(position);
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
        int length = mExpanded.size();
        for (int i = 0; i < length; i++) {
            //now we count the amount of expanded items within our range we check
            if (mExpanded.keyAt(i) >= from && mExpanded.keyAt(i) < position) {
                totalAddedItems = totalAddedItems + mExpanded.get(mExpanded.keyAt(i));
            } else if (mExpanded.keyAt(i) >= position) {
                //we do not care about all expanded items which are outside our range
                break;
            }
        }
        return totalAddedItems;
    }


    /**
     * add the values to the bundle for saveInstanceState
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down then this Bundle contains the data it most
     *                           recently supplied in Note: Otherwise it is null.
     * @return the passed bundle with the newly added data
     */
    public Bundle saveInstanceState(Bundle savedInstanceState) {
        return saveInstanceState(savedInstanceState, "");
    }

    /**
     * add the values to the bundle for saveInstanceState
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down then this Bundle contains the data it most
     *                           recently supplied in Note: Otherwise it is null.
     * @param prefix             a prefix added to the savedInstance key so we can store multiple states
     * @return the passed bundle with the newly added data
     */
    public Bundle saveInstanceState(Bundle savedInstanceState, String prefix) {
        if (savedInstanceState != null) {
            //remember the selections
            int[] selections = new int[mSelections.size()];
            int index = 0;
            for (Integer selection : mSelections) {
                selections[index] = selection;
                index++;
            }
            savedInstanceState.putIntArray(BUNDLE_SELECTIONS + prefix, selections);

            //remember the collapsed states
            savedInstanceState.putIntArray(BUNDLE_EXPANDED + prefix, getExpandedItems());
        }
        return savedInstanceState;
    }

    /**
     * we cache the sizes of our adapters so get accesses are faster
     */
    private void cacheSizes() {
        mAdapterSizes.clear();
        int size = 0;

        //we also have to add this for the first adapter otherwise the floorKey method will return the wrong value
        if (mAdapters.size() > 0) {
            mAdapterSizes.put(0, mAdapters.valueAt(0));
        }

        for (IAdapter<Item> adapter : mAdapters.values()) {
            if (adapter.getAdapterItemCount() > 0) {
                mAdapterSizes.put(size, adapter);
                size = size + adapter.getAdapterItemCount();
            }
        }
        mGlobalSize = size;
    }

    //-------------------------
    //-------------------------
    //Selection stuff
    //-------------------------
    //-------------------------

    /**
     * @return a set with the global positions of all selected items
     */
    public Set<Integer> getSelections() {
        return mSelections;
    }

    /**
     * @return a set with the items which are currently selected
     */
    public Set<Item> getSelectedItems() {
        Set<Item> items = new HashSet<>();
        for (Integer position : getSelections()) {
            items.add(getItem(position));
        }
        return items;
    }

    /**
     * toggles the selection of the item at the given position
     *
     * @param position the global position
     */
    public void toggleSelection(int position) {
        if (mSelections.contains(position)) {
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
    private void handleSelection(View view, Item item, int position) {
        //if this item is not selectable don't continue
        if (!item.isSelectable()) {
            return;
        }

        //if we have disabled deselection via click don't continue
        if (item.isSelected() && !mAllowDeselection) {
            return;
        }

        boolean selected = mSelections.contains(position);
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
                Iterator<Integer> entries = mSelections.iterator();
                while (entries.hasNext()) {
                    //deselect all but the current one! this is important!
                    Integer pos = entries.next();
                    if (pos != position) {
                        deselect(pos, entries);
                    }
                }
            }

            //we toggle the state of the view
            item.withSetSelected(!selected);
            view.setSelected(!selected);

            //now we make sure we remember the selection!
            if (selected) {
                if (mSelections.contains(position)) {
                    mSelections.remove(position);
                }
            } else {
                mSelections.add(position);
            }
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
     * selects an item and remembers it's position in the selections list
     *
     * @param position the global position
     */
    public void select(int position) {
        select(position, false);
    }

    /**
     * selects an item and remembers it's position in the selections list
     *
     * @param position  the global position
     * @param fireEvent true if the onClick listener should be called
     */
    public void select(int position, boolean fireEvent) {
        Item item = getItem(position);
        if (item != null) {
            item.withSetSelected(true);
            mSelections.add(position);
        }
        notifyItemChanged(position);

        if (mOnClickListener != null && fireEvent) {
            mOnClickListener.onClick(null, getAdapter(position), item, position);
        }
    }

    /**
     * deselects all selections
     */
    public void deselect() {
        deselect(mSelections);
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
     * deselects an item and removes it's position in the selections list
     *
     * @param position the global position
     */
    public void deselect(int position) {
        deselect(position, null);
    }

    /**
     * deselects an item and removes it's position in the selections list
     * also takes an iterator to remove items from the map
     *
     * @param position the global position
     * @param entries  the iterator which is used to deselect all
     */
    private void deselect(int position, Iterator<Integer> entries) {
        Item item = getItem(position);
        if (item != null) {
            item.withSetSelected(false);
        }
        if (entries == null) {
            if (mSelections.contains(position)) {
                mSelections.remove(position);
            }
        } else {
            entries.remove();
        }
        notifyItemChanged(position);
    }

    /**
     * deletes all current selected items
     *
     * @return a list of the IItem elements which were deleted
     */
    public List<Item> deleteAllSelectedItems() {
        List<Item> deletedItems = new LinkedList<>();
        //we have to re-fetch the selections array again and again as the position will change after one item is deleted
        Set<Integer> selections = getSelections();
        while (selections.size() > 0) {
            Iterator<Integer> iterator = selections.iterator();
            int position = iterator.next();
            IAdapter adapter = getAdapter(position);
            if (adapter != null && adapter instanceof IItemAdapter) {
                deletedItems.add(getItem(position));
                ((IItemAdapter) adapter).remove(position);
            } else {
                iterator.remove();
            }
            selections = getSelections();
        }
        return deletedItems;
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
        return mExpanded;
    }

    /**
     * @return a set with the global positions of all expanded items
     */
    public int[] getExpandedItems() {
        int[] expandedItems = new int[mExpanded.size()];
        int length = mExpanded.size();
        for (int i = 0; i < length; i++) {
            expandedItems[i] = mExpanded.keyAt(i);
        }
        return expandedItems;
    }

    /**
     * toggles the expanded state of the given expandable item at the given position
     *
     * @param position the global position
     */
    public void toggleExpandable(int position) {
        if (mExpanded.indexOfKey(position) >= 0) {
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
        Item item = getItem(position);
        if (item != null && item instanceof IExpandable) {
            IExpandable expandable = (IExpandable) item;

            //as we now know the item we will collapse we can collapse all subitems
            //if this item is not already collapsed and has sub items we go on
            if (expandable.isExpanded() && expandable.getSubItems() != null && expandable.getSubItems().size() > 0) {
                //first we find out how many items were added in total
                int totalAddedItems = expandable.getSubItems().size();

                int length = mExpanded.size();
                for (int i = 0; i < length; i++) {
                    if (mExpanded.keyAt(i) > position && mExpanded.keyAt(i) <= position + totalAddedItems) {
                        totalAddedItems = totalAddedItems + mExpanded.get(mExpanded.keyAt(i));
                    }
                }

                //we will deselect starting with the lowest one
                for (Integer value : mSelections) {
                    if (value > position && value <= position + totalAddedItems) {
                        deselect(value);
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
            }
        }
    }

    private void internalCollapse(int position, boolean notifyItemChanged) {
        Item item = getItem(position);
        if (item != null && item instanceof IExpandable) {
            IExpandable expandable = (IExpandable) item;
            //if this item is not already collapsed and has sub items we go on
            if (expandable.isExpanded() && expandable.getSubItems() != null && expandable.getSubItems().size() > 0) {
                internalCollapse(expandable, position, notifyItemChanged);
            }
        }
    }

    private void internalCollapse(IExpandable expandable, int position, boolean notifyItemChanged) {
        IAdapter adapter = getAdapter(position);
        if (adapter != null && adapter instanceof IItemAdapter) {
            ((IItemAdapter) adapter).removeRange(position + 1, expandable.getSubItems().size());
        }

        //remember that this item is now collapsed again
        expandable.withIsExpanded(false);
        //remove the information that this item was opened
        int indexOfKey = mExpanded.indexOfKey(position);
        if (indexOfKey >= 0) {
            mExpanded.removeAt(indexOfKey);
        }
        //we need to notify to get the correct drawable if there is one showing the current state
        if (notifyItemChanged) {
            notifyItemChanged(position);
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
        Item item = getItem(position);
        if (item != null && item instanceof IExpandable) {
            IExpandable<?, Item> expandable = (IExpandable<?, Item>) item;

            //if this item is not already expanded and has sub items we go on
            if (mExpanded.indexOfKey(position) < 0 && expandable.getSubItems() != null && expandable.getSubItems().size() > 0) {
                IAdapter<Item> adapter = getAdapter(position);
                if (adapter != null && adapter instanceof IItemAdapter) {
                    ((IItemAdapter<Item>) adapter).add(position + 1, expandable.getSubItems());
                }

                //remember that this item is now opened (not collapsed)
                expandable.withIsExpanded(true);

                //we need to notify to get the correct drawable if there is one showing the current state
                if (notifyItemChanged) {
                    notifyItemChanged(position);
                }

                //store it in the list of opened expandable items
                mExpanded.put(position, expandable.getSubItems() != null ? expandable.getSubItems().size() : 0);
            }
        }
    }

    //-------------------------
    //-------------------------
    //wrap the notify* methods so we can have our required selection adjustment code
    //-------------------------
    //-------------------------

    /**
     * wraps notifyDataSetChanged
     */
    public void notifyAdapterDataSetChanged() {
        mSelections.clear();
        mExpanded.clear();
        cacheSizes();
        notifyDataSetChanged();

        //we make sure the new items are displayed properly
        AdapterUtil.handleStates(this, 0, getItemCount() - 1);
    }

    /**
     * wraps notifyItemInserted
     *
     * @param position the global position
     */
    public void notifyAdapterItemInserted(int position) {
        notifyAdapterItemRangeInserted(position, 1);
    }

    /**
     * wraps notifyItemRangeInserted
     *
     * @param position  the global position
     * @param itemCount the count of items inserted
     */
    public void notifyAdapterItemRangeInserted(int position, int itemCount) {
        //we have to update all current stored selection and expandable states in our map
        mSelections = AdapterUtil.adjustPosition(mSelections, position, Integer.MAX_VALUE, itemCount);
        mExpanded = AdapterUtil.adjustPosition(mExpanded, position, Integer.MAX_VALUE, itemCount);
        cacheSizes();

        notifyItemRangeInserted(position, itemCount);

        //we make sure the new items are displayed properly
        AdapterUtil.handleStates(this, position, position + itemCount - 1);
    }

    /**
     * wraps notifyItemRemoved
     *
     * @param position the global position
     */
    public void notifyAdapterItemRemoved(int position) {
        notifyAdapterItemRangeRemoved(position, 1);
    }

    /**
     * wraps notifyItemRangeRemoved
     *
     * @param position  the global position
     * @param itemCount the count of items removed
     */
    public void notifyAdapterItemRangeRemoved(int position, int itemCount) {
        //we have to update all current stored selection and expandable states in our map
        mSelections = AdapterUtil.adjustPosition(mSelections, position, Integer.MAX_VALUE, itemCount * (-1));
        mExpanded = AdapterUtil.adjustPosition(mExpanded, position, Integer.MAX_VALUE, itemCount * (-1));
        cacheSizes();
        notifyItemRangeRemoved(position, itemCount);
    }

    /**
     * wraps notifyItemMoved
     *
     * @param fromPosition the global fromPosition
     * @param toPosition   the global toPosition
     */
    public void notifyAdapterItemMoved(int fromPosition, int toPosition) {
        //collapse items we move. just in case :D
        collapse(fromPosition);
        collapse(toPosition);

        if (!mSelections.contains(fromPosition) && mSelections.contains(toPosition)) {
            mSelections.remove(toPosition);
            mSelections.add(fromPosition);
        } else if (mSelections.contains(fromPosition) && !mSelections.contains(toPosition)) {
            mSelections.remove(fromPosition);
            mSelections.add(toPosition);
        }

        notifyItemMoved(fromPosition, toPosition);
    }

    /**
     * wraps notifyItemChanged
     *
     * @param position the global position
     */
    public void notifyAdapterItemChanged(int position) {
        notifyAdapterItemChanged(position, null);
    }

    /**
     * wraps notifyItemChanged
     *
     * @param position the global position
     * @param payload  additional payload
     */
    public void notifyAdapterItemChanged(int position, Object payload) {
        notifyAdapterItemRangeChanged(position, 1, payload);
    }

    /**
     * wraps notifyItemRangeChanged
     *
     * @param position  the global position
     * @param itemCount the count of items changed
     */
    public void notifyAdapterItemRangeChanged(int position, int itemCount) {
        notifyAdapterItemRangeChanged(position, itemCount, null);
    }

    /**
     * wraps notifyItemRangeChanged
     *
     * @param position  the global position
     * @param itemCount the count of items changed
     * @param payload   an additional payload
     */
    public void notifyAdapterItemRangeChanged(int position, int itemCount, Object payload) {
        for (int i = position; i < position + itemCount; i++) {
            if (mExpanded.indexOfKey(i) >= 0) {
                collapse(i);
            }
        }

        if (payload == null) {
            notifyItemRangeChanged(position, itemCount);
        } else {
            notifyItemRangeChanged(position, itemCount, payload);
        }

        //we make sure the new items are displayed properly
        AdapterUtil.handleStates(this, position, position + itemCount - 1);
    }

    /**
     * notifies the fastAdapter about new / removed items within a sub hierarchy
     * NOTE this currently only works for sub items with only 1 level
     *
     * @param position the global position of the parent item
     */
    public void notifyAdapterSubItemsChanged(int position) {
        Item item = getItem(position);
        if (item != null && item instanceof IExpandable) {
            IExpandable expandable = (IExpandable) item;

            //TODO ALSO CARE ABOUT SUB SUB ... HIRACHIES

            //first we find out how many items this parent item contains
            int itemsCount = expandable.getSubItems().size();

            //we only need to do something if this item is expanded
            if (mExpanded.indexOfKey(position) > -1) {
                int previousCount = mExpanded.get(position);
                IAdapter adapter = getAdapter(position);
                if (adapter != null && adapter instanceof IItemAdapter) {
                    ((IItemAdapter) adapter).removeRange(position + 1, previousCount);
                    ((IItemAdapter) adapter).add(position + 1, expandable.getSubItems());
                }
                mExpanded.put(position, itemsCount);
            }
        }
    }

    //listeners
    public interface OnTouchListener<Item extends IItem> {
        /**
         * the onTouch event of a specific item inside the RecyclerView
         *
         * @param v        the view we clicked
         * @param event    the touch event
         * @param adapter  the adapter which is responsible for the given item
         * @param item     the IItem which was clicked
         * @param position the global position
         * @return return true if the event was consumed, otherwise false
         */
        boolean onTouch(View v, MotionEvent event, IAdapter<Item> adapter, Item item, int position);
    }

    public interface OnClickListener<Item extends IItem> {
        /**
         * the onClick event of a specific item inside the RecyclerView
         *
         * @param v        the view we clicked
         * @param adapter  the adapter which is responsible for the given item
         * @param item     the IItem which was clicked
         * @param position the global position
         * @return return true if the event was consumed, otherwise false
         */
        boolean onClick(View v, IAdapter<Item> adapter, Item item, int position);
    }

    public interface OnLongClickListener<Item extends IItem> {
        /**
         * the onLongClick event of a specific item inside the RecyclerView
         *
         * @param v        the view we clicked
         * @param adapter  the adapter which is responsible for the given item
         * @param item     the IItem which was clicked
         * @param position the global position
         * @return return true if the event was consumed, otherwise false
         */
        boolean onLongClick(View v, IAdapter<Item> adapter, Item item, int position);
    }

    public interface OnCreateViewHolderListener {
        /**
         * is called inside the onCreateViewHolder method and creates the viewHolder based on the provided viewTyp
         *
         * @param parent   the parent which will host the View
         * @param viewType the type of the ViewHolder we want to create
         * @return the generated ViewHolder based on the given viewType
         */
        RecyclerView.ViewHolder onPreCreateViewHolder(ViewGroup parent, int viewType);

        /**
         * is called after the viewHolder was created and the default listeners were added
         *
         * @param viewHolder the created viewHolder after all listeners were set
         * @return the viewHolder given as param
         */
        RecyclerView.ViewHolder onPostCreateViewHolder(RecyclerView.ViewHolder viewHolder);
    }

    /**
     * default implementation of the OnCreateViewHolderListener
     */
    public class OnCreateViewHolderListenerImpl implements OnCreateViewHolderListener {
        /**
         * is called inside the onCreateViewHolder method and creates the viewHolder based on the provided viewTyp
         *
         * @param parent   the parent which will host the View
         * @param viewType the type of the ViewHolder we want to create
         * @return the generated ViewHolder based on the given viewType
         */
        @Override
        public RecyclerView.ViewHolder onPreCreateViewHolder(ViewGroup parent, int viewType) {
            return getTypeInstance(viewType).getViewHolder(parent);
        }

        /**
         * is called after the viewHolder was created and the default listeners were added
         *
         * @param viewHolder the created viewHolder after all listeners were set
         * @return the viewHolder given as param
         */
        @Override
        public RecyclerView.ViewHolder onPostCreateViewHolder(RecyclerView.ViewHolder viewHolder) {
            return viewHolder;
        }
    }

    public interface OnBindViewHolderListener {
        /**
         * is called in onBindViewHolder to bind the data on the ViewHolder
         *
         * @param viewHolder the viewHolder for the type at this position
         * @param position   the position of thsi viewHolder
         */
        void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position);
    }

    public class OnBindViewHolderListenerImpl implements OnBindViewHolderListener {
        /**
         * is called in onBindViewHolder to bind the data on the ViewHolder
         *
         * @param viewHolder the viewHolder for the type at this position
         * @param position   the position of this viewHolder
         */
        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
            getItem(position).bindView(viewHolder);
        }
    }

    /**
     * an internal class to return the IItem and relativePosition and it's adapter at once. used to save one iteration inside the getInternalItem method
     */
    public static class RelativeInfo<Item extends IItem> {
        public IAdapter<Item> adapter = null;
        public Item item = null;
    }
}
