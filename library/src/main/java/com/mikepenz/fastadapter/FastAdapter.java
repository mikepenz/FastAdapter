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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Created by mikepenz on 27.12.15.
 */
public class FastAdapter<Item extends IItem> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    protected static final String BUNDLE_SELECTIONS = "bundle_selections";
    protected static final String BUNDLE_COLLAPSIBLE = "bundle_collapsible";

    // we remember all adapters
    private ArrayMap<Integer, IAdapter> mAdapters = new ArrayMap<>();
    // we remember all possible types so we can create a new view efficiently
    private ArrayMap<Integer, Item> mTypeInstances = new ArrayMap<>();

    // if we want multiSelect enabled
    private boolean mMultiSelect = false;
    // if we want the multiSelect only on longClick
    private boolean mMultiSelectOnLongClick = true;

    // we need to remember all selections to recreate them after orientation change
    private SortedSet<Integer> mSelections = new TreeSet<>();
    // we need to remember all opened collapse items to recreate them after orientation change
    private SparseIntArray mCollapsibleOpened = new SparseIntArray();

    // the listeners which can be hooked on an item
    private OnClickListener mOnClickListener;
    private OnLongClickListener mOnLongClickListener;
    private OnTouchListener mOnTouchListener;

    /**
     * Define the OnClickListener which will be used for a single item
     *
     * @param mOnClickListener the OnClickListener which will be used for a single item
     * @return this
     */
    public FastAdapter withOnClickListener(OnClickListener mOnClickListener) {
        this.mOnClickListener = mOnClickListener;
        return this;
    }

    /**
     * Define the OnLongClickListener which will be used for a single item
     *
     * @param mOnLongClickListener the OnLongClickListener which will be used for a single item
     * @return this
     */
    public FastAdapter withOnLongClickListener(OnLongClickListener mOnLongClickListener) {
        this.mOnLongClickListener = mOnLongClickListener;
        return this;
    }

    /**
     * Define the TouchListener which will be used for a single item
     *
     * @param mOnTouchListener the TouchListener which will be used for a single item
     * @return this
     */
    public FastAdapter withOnTouchListener(OnTouchListener mOnTouchListener) {
        this.mOnTouchListener = mOnTouchListener;
        return this;
    }

    /**
     * Enable this if you want multiSelection possible in the list
     *
     * @param multiSelect true to enable multiSelect
     * @return this
     */
    public FastAdapter withMultiSelect(boolean multiSelect) {
        mMultiSelect = multiSelect;
        return this;
    }

    /**
     * Disable this if you want the multiSelection on a single tap (note you have to enable multiSelect for this to make a difference)
     *
     * @param multiSelectOnLongClick false to do multiSelect via single click
     * @return this
     */
    public FastAdapter withMultiSelectOnLongClick(boolean multiSelectOnLongClick) {
        mMultiSelectOnLongClick = multiSelectOnLongClick;
        return this;
    }

    /**
     * re-selects all elements stored in the savedInstanceState
     * IMPORTANT! Call this method only after all items where added to the adapters again. Otherwise it may select wrong items!
     *
     * @param savedInstanceState
     * @return
     */
    public FastAdapter withSavedInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            //first restore opened collasable items, as otherwise may not all selections could be restored
            int[] collapsibles = savedInstanceState.getIntArray(BUNDLE_COLLAPSIBLE);
            if (collapsibles != null) {
                for (Integer collapsible : collapsibles) {
                    open(collapsible);
                }
            }

            //restore the selections
            int[] selections = savedInstanceState.getIntArray(BUNDLE_SELECTIONS);
            if (selections != null) {
                for (Integer selection : selections) {
                    select(selection);
                }
            }
        }
        return this;
    }

    /*
    public FastAdapter wrap(AbstractAdapter abstractAdapter) {
        if (abstractAdapter != null) {
            registerAdapter(abstractAdapter);
            abstractAdapter.setFastAdapter(this);
            wrap(abstractAdapter.getParentAdapter());
        }
        return this;
    }
    */

    /**
     * registers an AbstractAdapter which will be hooked into the adapter chain
     *
     * @param adapter
     * @param <A>     an adapter which extends the AbstractAdapter
     */
    public <A extends AbstractAdapter> void registerAdapter(A adapter) {
        if (!mAdapters.containsKey(adapter.getOrder())) {
            mAdapters.put(adapter.getOrder(), adapter);
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
     * Creates the ViewHolder by the viewType
     *
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final RecyclerView.ViewHolder holder = mTypeInstances.get(viewType).getViewHolder(parent);

        //handle click behavior
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = holder.getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    boolean consumed = false;
                    if (mOnClickListener != null) {
                        RelativeInfo relativeInfo = getRelativePosition(pos);
                        consumed = mOnClickListener.onClick(v, pos, (IItem) relativeInfo.item, relativeInfo);
                    }

                    if (!consumed && (!(mMultiSelect && mMultiSelectOnLongClick) || !mMultiSelect)) {
                        handleSelection(pos);
                    }
                }
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int pos = holder.getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    boolean consumed = false;
                    if (mOnLongClickListener != null) {
                        RelativeInfo relativeInfo = getRelativePosition(pos);
                        consumed = mOnLongClickListener.onLongClick(v, pos, (IItem) relativeInfo.item, relativeInfo);
                    }

                    if (!consumed && (mMultiSelect && mMultiSelectOnLongClick)) {
                        handleSelection(pos);
                    }
                    return consumed;
                }
                return false;
            }
        });

        holder.itemView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mOnTouchListener != null) {
                    int pos = holder.getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        RelativeInfo relativeInfo = getRelativePosition(pos);
                        return mOnTouchListener.onTouch(v, event, pos, (IItem) relativeInfo.item, relativeInfo);
                    }
                }
                return false;
            }
        });

        return holder;
    }

    /**
     * Binds the data to the created ViewHolder and sets the listeners to the holder.itemView
     *
     * @param holder
     * @param position the global position
     */
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        getItem(position).bindView(holder);
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
            IAdapter adapter = mAdapters.valueAt(i);
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
        return getRelativePosition(position).item;
    }

    /**
     * Internal method to get the Item as ItemHolder which comes with the relative position within it's adapter
     * Finds the responsible adapter for the given position
     *
     * @param position the global position
     * @return the adapter which is responsible for this position
     */
    public RelativeInfo<Item> getRelativePosition(int position) {
        if (position < 0) {
            return new RelativeInfo<>();
        }

        int currentCount = 0;
        int length = mAdapters.size();
        for (int i = 0; i < length; i++) {
            IAdapter adapter = mAdapters.valueAt(i);
            if (adapter.getOrder() < 0) {
                continue;
            }

            if (currentCount <= position && currentCount + adapter.getAdapterItemCount() > position) {
                RelativeInfo relativeInfo = new RelativeInfo();
                relativeInfo.adapter = adapter;
                relativeInfo.relativePosition = position - currentCount;
                relativeInfo.item = adapter.getAdapterItem(position - currentCount);
                return relativeInfo;
            }
            currentCount = currentCount + adapter.getAdapterItemCount();
        }
        return new RelativeInfo<>();
    }

    /**
     * finds the int ItemViewType from the IItem which exists at the given position
     *
     * @param position the global position
     * @return
     */
    @Override
    public int getItemViewType(int position) {
        return getItem(position).getType();
    }

    /**
     * finds the int ItemId from the IItem which exists at the given position
     *
     * @param position the global position
     * @return
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
    @Override
    public int getItemCount() {
        //we go over all adapters and fetch all item sizes
        int size = 0;
        int length = mAdapters.size();
        for (int i = 0; i < length; i++) {
            IAdapter adapter = mAdapters.valueAt(i);
            if (adapter.getOrder() < 0) {
                continue;
            }

            size = size + adapter.getAdapterItemCount();
        }
        return size;
    }

    /**
     * calculates the item count up to a given (excluding this) order number
     *
     * @param order the number up to which the items are counted
     * @return the total count of items up to the adapter order
     */
    public int getItemCount(int order) {
        //we go over all adapters and fetch all item sizes
        int size = 0;

        int length = mAdapters.size();
        for (int i = 0; i < length; i++) {
            IAdapter adapter = mAdapters.valueAt(i);
            if (adapter.getOrder() < 0) {
                continue;
            }

            if (adapter.getOrder() < order) {
                size = adapter.getAdapterItemCount();
            } else {
                return size;
            }
        }
        return size;
    }

    /**
     * add the values to the bundle for saveInstanceState
     *
     * @param savedInstanceState
     * @return
     */
    public Bundle saveInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            //remember the selections
            int[] selections = new int[mSelections.size()];
            int index = 0;
            for (Integer selection : mSelections) {
                selections[index] = selection;
                index++;
            }
            savedInstanceState.putIntArray(BUNDLE_SELECTIONS, selections);

            //remember the collapsed states
            savedInstanceState.putIntArray(BUNDLE_COLLAPSIBLE, getOpenedCollapsibleItems());
        }
        return savedInstanceState;
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
    private void handleSelection(int position) {
        Item item = getItem(position);
        if (!item.isSelectable()) {
            return;
        }

        if (!mMultiSelect) {
            for (Integer entry : mSelections) {
                if (entry != position) {
                    deselect(entry);
                }
            }
        }

        if (mSelections.contains(position)) {
            deselect(position);
        } else {
            select(position);
        }
    }

    /**
     * selects an item and remembers it's position in the selections list
     *
     * @param position the global position
     */
    public void select(int position) {
        IItem item = getItem(position);
        if (item != null) {
            item.withSetSelected(true);
            mSelections.add(position);
        }
        notifyItemChanged(position);
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
     * deselects all selections
     */
    public void deselect() {
        Iterator<Integer> entries = mSelections.iterator();
        while (entries.hasNext()) {
            deselect(entries.next(), entries);
        }
    }

    //-------------------------
    //-------------------------
    //Collapse stuff
    //-------------------------
    //-------------------------

    /**
     * @return a set with the global positions of all opened collapsible items
     */
    public int[] getOpenedCollapsibleItems() {
        int[] collapsibleItems = new int[mCollapsibleOpened.size()];
        int length = mCollapsibleOpened.size();
        for (int i = 0; i < length; i++) {
            collapsibleItems[i] = mCollapsibleOpened.keyAt(i);
        }
        return collapsibleItems;
    }

    /**
     * toggles the collapse state of the given collapsible item at the given position
     *
     * @param position the global position
     */
    public void toggleCollapsible(int position) {
        if (mCollapsibleOpened.indexOfKey(position) >= 0) {
            collapse(position);
        } else {
            open(position);
        }
    }

    /**
     * collapses (closes) the given collapsible item at the given position
     *
     * @param position the global position
     */
    public void collapse(int position) {
        Item item = getItem(position);
        if (item != null && item instanceof ICollapsible) {
            ICollapsible collapsible = (ICollapsible) item;

            //as we now know the item we will collapse we can collapse all subitems
            //if this item is not already callapsed and has sub items we go on
            if (!collapsible.isCollapsed() && collapsible.getSubItems() != null && collapsible.getSubItems().size() > 0) {
                //first we find out how many items were added in total
                int totalAddedItems = collapsible.getSubItems().size();

                int length = mCollapsibleOpened.size();
                for (int i = 0; i < length; i++) {
                    if (mCollapsibleOpened.keyAt(i) > position && mCollapsibleOpened.keyAt(i) <= position + totalAddedItems) {
                        totalAddedItems = totalAddedItems + mCollapsibleOpened.get(mCollapsibleOpened.keyAt(i));
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
                    if (mCollapsibleOpened.keyAt(i) > position && mCollapsibleOpened.keyAt(i) <= position + totalAddedItems) {
                        //we collapsed those items now we remove update the added items
                        totalAddedItems = totalAddedItems - mCollapsibleOpened.get(mCollapsibleOpened.keyAt(i));

                        //we collapse the item
                        internalCollapse(mCollapsibleOpened.keyAt(i));
                    }
                }

                //we collapse our root element
                internalCollapse(collapsible, position);
            }
        }
    }

    private void internalCollapse(int position) {
        Item item = getItem(position);
        if (item != null && item instanceof ICollapsible) {
            ICollapsible collapsible = (ICollapsible) item;
            //if this item is not already callapsed and has sub items we go on
            if (!collapsible.isCollapsed() && collapsible.getSubItems() != null && collapsible.getSubItems().size() > 0) {
                internalCollapse(collapsible, position);
            }
        }
    }

    private void internalCollapse(ICollapsible collapsible, int position) {
        RelativeInfo adapterHolder = getRelativePosition(position);

        //if we find the adapter for this item we will remove all of its subitems
        if (adapterHolder.adapter != null && adapterHolder.adapter instanceof IItemAdapter) {
            IItemAdapter itemAdapter = (IItemAdapter) adapterHolder.adapter;
            itemAdapter.removeItemRange(adapterHolder.relativePosition + 1, collapsible.getSubItems().size());
        }

        //remember that this item is now collapsed again
        collapsible.withCollapsed(true);
        //remove the information that this item was opened
        int indexOfKey = mCollapsibleOpened.indexOfKey(position);
        if (indexOfKey >= 0) {
            mCollapsibleOpened.removeAt(indexOfKey);
        }
    }

    /**
     * deletes all current selected items
     *
     * @return a list of the IItem elements which were deleted
     */
    public List<Item> deleteAllSelectedItems() {
        List<Item> deletedItems = new LinkedList<>();
        //we have to refetch the selections array again and again as the position will change after one item is deleted
        Set<Integer> selections = getSelections();
        while (selections.size() > 0) {
            RelativeInfo relativeInfo = getRelativePosition(selections.iterator().next());
            if (relativeInfo.adapter instanceof IItemAdapter) {
                deletedItems.add((Item) relativeInfo.adapter.getAdapterItem(relativeInfo.relativePosition));
                ((IItemAdapter) relativeInfo.adapter).remove(relativeInfo.relativePosition);
            }
            selections = getSelections();
        }
        return deletedItems;
    }

    /**
     * opens the collapsible item at the given position
     *
     * @param position the global position
     */
    public void open(int position) {
        Item item = getItem(position);
        if (item != null && item instanceof ICollapsible) {
            ICollapsible collapsible = (ICollapsible) item;

            //if this item is not already callapsed and has sub items we go on
            if (collapsible.isCollapsed() && collapsible.getSubItems() != null && collapsible.getSubItems().size() > 0) {
                RelativeInfo relativeInfo = getRelativePosition(position);

                //if we find the adapter for this item we add the sub items
                if (relativeInfo.adapter != null && relativeInfo.adapter instanceof IItemAdapter) {
                    IItemAdapter itemAdapter = (IItemAdapter) relativeInfo.adapter;
                    itemAdapter.add(relativeInfo.relativePosition + 1, collapsible.getSubItems());
                }

                //remember that this item is now opened (not collapsed)
                collapsible.withCollapsed(false);
                //store it in the list of opened collapsible items
                mCollapsibleOpened.put(position, collapsible.getSubItems() != null ? collapsible.getSubItems().size() : 0);
            }
        }
    }

    //-------------------------
    //-------------------------
    //wrap the notify* methods so we can have our required selection adjustment code
    //-------------------------
    //-------------------------

    /**
     * wraps notifyItemInserted
     *
     * @param position the global position
     */
    public void notifyAdapterItemInserted(int position) {
        //we have to update all current stored selection and collapsed states in our map
        mSelections = AdapterUtil.adjustPosition(mSelections, position, Integer.MAX_VALUE, 1);
        mCollapsibleOpened = AdapterUtil.adjustPosition(mCollapsibleOpened, position, Integer.MAX_VALUE, 1);
        notifyItemInserted(position);
    }

    /**
     * wraps notifyItemRangeInserted
     *
     * @param position  the global position
     * @param itemCount
     */
    public void notifyAdapterItemRangeInserted(int position, int itemCount) {
        //we have to update all current stored selection and collapsed states in our map
        mSelections = AdapterUtil.adjustPosition(mSelections, position, Integer.MAX_VALUE, itemCount);
        mCollapsibleOpened = AdapterUtil.adjustPosition(mCollapsibleOpened, position, Integer.MAX_VALUE, itemCount);
        notifyItemRangeInserted(position, itemCount);
    }

    /**
     * wraps notifyItemRemoved
     *
     * @param position the global position
     */
    public void notifyAdapterItemRemoved(int position) {
        //we have to update all current stored selection and collapsed states in our map
        mSelections = AdapterUtil.adjustPosition(mSelections, position, Integer.MAX_VALUE, -1);
        mCollapsibleOpened = AdapterUtil.adjustPosition(mCollapsibleOpened, position, Integer.MAX_VALUE, -1);
        notifyItemRemoved(position);
    }

    /**
     * wraps notifyItemRangeRemoved
     *
     * @param position  the global position
     * @param itemCount
     */
    public void notifyAdapterItemRangeRemoved(int position, int itemCount) {
        //we have to update all current stored selection and collapsed states in our map
        mSelections = AdapterUtil.adjustPosition(mSelections, position, Integer.MAX_VALUE, itemCount * (-1));
        mCollapsibleOpened = AdapterUtil.adjustPosition(mCollapsibleOpened, position, Integer.MAX_VALUE, itemCount * (-1));
        notifyItemRangeRemoved(position, itemCount);
    }

    /**
     * wraps notifyItemMoved
     *
     * @param fromPosition the global fromPosition
     * @param toPosition   the global toPosition
     */
    public void notifyAdapterItemMoved(int fromPosition, int toPosition) {
        if (mSelections.contains(fromPosition)) {
            mSelections.remove(fromPosition);
            mSelections.add(toPosition);
        }

        //we have to update all current stored selection and collapsed states in our map
        if (fromPosition < toPosition) {
            mSelections = AdapterUtil.adjustPosition(mSelections, fromPosition, toPosition, -1);
            mCollapsibleOpened = AdapterUtil.adjustPosition(mCollapsibleOpened, fromPosition, toPosition, -1);
        } else {
            mSelections = AdapterUtil.adjustPosition(mSelections, toPosition, fromPosition, 1);
            mCollapsibleOpened = AdapterUtil.adjustPosition(mCollapsibleOpened, toPosition, fromPosition, 1);
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
     * @param payload
     */
    public void notifyAdapterItemChanged(int position, Object payload) {
        Item updateItem = getItem(position);
        if (updateItem.isSelected()) {
            mSelections.add(position);
        } else if (mSelections.contains(position)) {
            mSelections.remove(position);
        }

        if (payload == null) {
            notifyItemChanged(position);
        } else {
            notifyItemChanged(position, payload);
        }
    }

    /**
     * wraps notifyItemRangeChanged
     *
     * @param position  the global position
     * @param itemCount
     */
    public void notifyAdapterItemRangeChanged(int position, int itemCount) {
        notifyAdapterItemRangeChanged(position, itemCount, null);
    }

    /**
     * wraps notifyItemRangeChanged
     *
     * @param position  the global position
     * @param itemCount
     * @param payload
     */
    public void notifyAdapterItemRangeChanged(int position, int itemCount, Object payload) {
        for (int i = position; i < position + itemCount; i++) {
            Item updateItem = getItem(position);
            if (updateItem.isSelected()) {
                mSelections.add(position);
            } else if (mSelections.contains(position)) {
                mSelections.remove(position);
            }
        }

        if (payload == null) {
            notifyItemRangeChanged(position, itemCount);
        } else {
            notifyItemRangeChanged(position, itemCount, payload);
        }
    }

    //listeners
    public interface OnTouchListener {
        /**
         * the onTouch event of a specific item inside the RecyclerView
         *
         * @param v
         * @param event
         * @param position     the global position
         * @param item         the IItem which was clicked
         * @param relativeInfo the relative position of this item and adapter within the AdapterHolder
         * @return return true if the event was consumed, otherwise false
         */
        boolean onTouch(View v, MotionEvent event, int position, IItem item, RelativeInfo relativeInfo);
    }

    public interface OnClickListener {
        /**
         * the onClick event of a specific item inside the RecyclerView
         *
         * @param v
         * @param position     the global position
         * @param item         the IItem which was clicked
         * @param relativeInfo the relative position of this item and adapter within the AdapterHolder
         * @return return true if the event was consumed, otherwise false
         */
        boolean onClick(View v, int position, IItem item, RelativeInfo relativeInfo);
    }

    public interface OnLongClickListener {
        /**
         * the onLongClick event of a specific item inside the RecyclerView
         *
         * @param v
         * @param position     the global position
         * @param item         the IItem which was clicked
         * @param relativeInfo the relative position of this item and adapter within the AdapterHolder
         * @return return true if the event was consumed, otherwise false
         */
        boolean onLongClick(View v, int position, IItem item, RelativeInfo relativeInfo);
    }

    /**
     * an internal class to return the IItem and relativePosition and it's adapter at once. used to save one iteration inside the getInternalItem method
     */
    public static class RelativeInfo<Item> {
        public IAdapter adapter = null;
        public Item item = null;
        public int relativePosition = -1;
    }
}
