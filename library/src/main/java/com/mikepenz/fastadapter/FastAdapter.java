package com.mikepenz.fastadapter;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.mikepenz.fastadapter.utils.RecyclerViewCacheUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Created by mikepenz on 27.12.15.
 */
public class FastAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    protected static final String BUNDLE_SELECTIONS = "bundle_selections";

    // we remember all adapters
    private SortedMap<Integer, IAdapter> mAdapters = new TreeMap<>();
    // we remember all possible types so we can create a new view efficiently
    private Map<Integer, IItem> mTypeInstances = new HashMap<>();

    // if we want multiSelect enabled
    private boolean mMultiSelect = false;
    // we need to remember all selections to recreate them after orientation change
    private SortedMap<Integer, IItem> mSelections = new TreeMap<>();

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
     * re-selects all elements stored in the savedInstanceState
     * IMPORTANT! Call this method only after all items where added to the adapters again. Otherwise it may select wrong items!
     *
     * @param savedInstanceState
     * @return
     */
    public FastAdapter withSavedInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            ArrayList<Integer> selections = savedInstanceState.getIntegerArrayList(BUNDLE_SELECTIONS);
            for (Integer selection : selections) {
                select(selection);
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
    public void registerTypeInstance(IItem item) {
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
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {//first check if we (probably) have this item in the cache
        RecyclerView.ViewHolder vh = RecyclerViewCacheUtil.getInstance().obtain(viewType);
        if (vh == null) {
            return mTypeInstances.get(viewType).getViewHolder(parent);
        } else {
            return vh;
        }

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

        //handle click behavior
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = holder.getAdapterPosition();
                ItemHolder itemHolder = getInternalItem(pos);

                boolean consumed = false;
                if (mOnClickListener != null) {
                    consumed = mOnClickListener.onClick(v, pos, itemHolder.relativePosition, itemHolder.item);
                }

                if (!consumed) {
                    handleSelection(pos);
                }
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mOnLongClickListener != null) {
                    int pos = holder.getAdapterPosition();
                    ItemHolder itemHolder = getInternalItem(pos);
                    return mOnLongClickListener.onLongClick(v, pos, itemHolder.relativePosition, itemHolder.item);
                }
                return false;
            }
        });

        holder.itemView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mOnTouchListener != null) {
                    int pos = holder.getAdapterPosition();
                    ItemHolder itemHolder = getInternalItem(pos);
                    return mOnTouchListener.onTouch(v, event, pos, itemHolder.relativePosition, itemHolder.item);
                }
                return false;
            }
        });
    }

    /**
     * gets the IItem by a position, from all registered adapters
     *
     * @param position the global position
     * @return the found IItem or null
     */
    public IItem getItem(int position) {
        return getInternalItem(position).item;
    }

    /**
     * Internal method to get the Item as ItemHolder which comes with the relative position within it's adapter
     *
     * @param position the global position
     * @return an ItemHolder with the Item, and the relative position
     */
    private ItemHolder getInternalItem(int position) {
        if (position < 0) {
            return new ItemHolder();
        }

        int currentCount = 0;
        for (IAdapter adapter : mAdapters.values()) {
            if (currentCount <= position && currentCount + adapter.getAdapterItemCount() > position) {
                ItemHolder itemHolder = new ItemHolder();
                itemHolder.item = adapter.getAdapterItem(position - currentCount);
                itemHolder.relativePosition = position - currentCount;
                return itemHolder;
            }
            currentCount = currentCount + adapter.getAdapterItemCount();
        }
        return new ItemHolder();
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
        for (IAdapter adapter : mAdapters.values()) {
            size = adapter.getAdapterItemCount();
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
        for (IAdapter adapter : mAdapters.values()) {
            if (adapter.getOrder() < order) {
                size = adapter.getAdapterItemCount();
            } else {
                return size;
            }
        }
        return size;
    }

    /**
     * handles the selection and deselects item if multiSelect is disabled
     *
     * @param position the global position
     */
    private void handleSelection(int position) {
        IItem item = getItem(position);
        if (!item.isSelectable()) {
            return;
        }

        if (!mMultiSelect) {
            for (Map.Entry<Integer, IItem> entry : mSelections.entrySet()) {
                if (entry.getKey() != position) {
                    deselect(entry.getKey());
                }
            }
        }

        if (mSelections.containsKey(position)) {
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
            mSelections.put(position, item);
        }
        notifyItemChanged(position);
    }

    /**
     * deselects an item and removes it's position in the selections list
     *
     * @param position the global position
     */
    public void deselect(int position) {
        IItem item = getItem(position);
        if (item != null) {
            item.withSetSelected(false);
        }
        if (mSelections.containsKey(position)) {
            mSelections.remove(position);
        }
        notifyItemChanged(position);
    }

    /**
     * deselects all selections
     */
    public void deselect() {
        for (Map.Entry<Integer, IItem> entry : mSelections.entrySet()) {
            deselect(entry.getKey());
        }
    }

    /**
     * add the values to the bundle for saveInstanceState
     *
     * @param savedInstanceState
     * @return
     */
    public Bundle saveInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            ArrayList<Integer> selections = new ArrayList<>();
            selections.addAll(mSelections.keySet());
            savedInstanceState.putIntegerArrayList(BUNDLE_SELECTIONS, selections);
        }
        return savedInstanceState;
    }

    //-------------------------
    //-------------------------
    //wrap the notify* methods so we can have our required selection adjustment code
    //-------------------------
    //-------------------------

    /**
     * wraps notifyItemInserted
     *
     * @param position
     */
    public void notifyAdapterItemInserted(int position) {
        //we have to update all current stored selection in our map
        adjustSelectionsAfter(position, 1);
        notifyItemInserted(position);
    }

    /**
     * wraps notifyItemRangeInserted
     *
     * @param position
     * @param itemCount
     */
    public void notifyAdapterItemRangeInserted(int position, int itemCount) {
        //we have to update all current stored selection in our map
        adjustSelectionsAfter(position, itemCount);
        notifyItemRangeInserted(position, itemCount);
    }

    /**
     * wraps notifyItemRemoved
     *
     * @param position
     */
    public void notifyAdapterItemRemoved(int position) {
        //we have to update all current stored selection in our map
        adjustSelectionsAfter(position, -1);
        notifyItemRemoved(position);
    }

    /**
     * wraps notifyItemRangeRemoved
     *
     * @param position
     * @param itemCount
     */
    public void notifyAdapterItemRangeRemoved(int position, int itemCount) {
        //we have to update all current stored selection in our map
        adjustSelectionsAfter(position, itemCount * (-1));
        notifyItemRangeRemoved(position, itemCount);
    }

    /**
     * internal method to handle the selections if items are added / removed
     *
     * @param position
     * @param adjustBy
     */
    private void adjustSelectionsAfter(int position, int adjustBy) {
        SortedMap<Integer, IItem> selections = new TreeMap<>();

        for (Map.Entry<Integer, IItem> entry : mSelections.entrySet()) {
            if (entry.getKey() < position) {
                selections.put(entry.getKey(), entry.getValue());
            } else if (adjustBy > 0) {
                selections.put(entry.getKey() + adjustBy, entry.getValue());
            } else if (adjustBy < 0 && entry.getKey() > (position + (-1) * adjustBy)) {
                selections.put(entry.getKey() + adjustBy, entry.getValue());
            } else if (adjustBy < 0 && entry.getKey() < (position + (-1) * adjustBy)) {
                ;//we do not add them anymore. they were removed
            }
        }
        mSelections = selections;
    }

    /**
     * wraps notifyItemMoved
     *
     * @param fromPosition
     * @param toPosition
     */
    public void notifyAdapterItemMoved(int fromPosition, int toPosition) {
        if (mSelections.containsKey(fromPosition)) {
            IItem item = mSelections.get(fromPosition);
            mSelections.remove(fromPosition);
            mSelections.put(toPosition, item);
        }
        notifyItemMoved(fromPosition, toPosition);
    }

    /**
     * wraps notifyItemChanged
     *
     * @param position
     */
    public void notifyAdapterItemChanged(int position) {
        notifyAdapterItemChanged(position, null);
    }

    /**
     * wraps notifyItemChanged
     *
     * @param position
     * @param payload
     */
    public void notifyAdapterItemChanged(int position, Object payload) {
        IItem updateItem = getItem(position);
        if (updateItem.isSelected()) {
            mSelections.put(position, updateItem);
        } else if (mSelections.containsKey(position)) {
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
     * @param position
     * @param itemCount
     */
    public void notifyAdapterItemRangeChanged(int position, int itemCount) {
        notifyAdapterItemRangeChanged(position, itemCount, null);
    }

    /**
     * wraps notifyItemRangeChanged
     *
     * @param position
     * @param itemCount
     * @param payload
     */
    public void notifyAdapterItemRangeChanged(int position, int itemCount, Object payload) {
        for (int i = position; i < position + itemCount; i++) {
            IItem updateItem = getItem(position);
            if (updateItem.isSelected()) {
                mSelections.put(position, updateItem);
            } else if (mSelections.containsKey(position)) {
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
         * @param position
         * @param relativePosition
         * @param item
         * @return
         */
        boolean onTouch(View v, MotionEvent event, int position, int relativePosition, IItem item);
    }

    public interface OnClickListener {
        /**
         * the onClick event of a specific item inside the RecyclerView
         *
         * @param v
         * @param position
         * @param relativePosition
         * @param item
         * @return
         */
        boolean onClick(View v, int position, int relativePosition, IItem item);
    }

    public interface OnLongClickListener {
        /**
         * the onLongClick event of a specific item inside the RecyclerView
         *
         * @param v
         * @param position
         * @param relativePosition
         * @param item
         * @return
         */
        boolean onLongClick(View v, int position, int relativePosition, IItem item);
    }

    /**
     * an internal class to return the IItem and relativePosition at once. used to save one iteration inside the getInternalItem method
     */
    private class ItemHolder {
        public IItem item = null;
        public int relativePosition = -1;
    }
}
