package com.mikepenz.fastadapter;

import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Created by mikepenz on 27.12.15.
 */
public class FastAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private SortedMap<Integer, AbstractAdapter> mAdapters = new TreeMap<>();
    private Map<Integer, IItem> mTypeInstances = new HashMap<>();

    private boolean mMultiSelect = false;
    private SortedMap<Integer, IItem> mSelections = new TreeMap<>();

    private OnClickListener mOnClickListener;
    private OnLongClickListener mOnLongClickListener;
    private OnTouchListener mOnTouchListener;

    public FastAdapter withOnClickListener(OnClickListener mOnClickListener) {
        this.mOnClickListener = mOnClickListener;
        return this;
    }

    public FastAdapter withOnLongClickListener(OnLongClickListener mOnLongClickListener) {
        this.mOnLongClickListener = mOnLongClickListener;
        return this;
    }

    public FastAdapter withOnTouchListener(OnTouchListener mOnTouchListener) {
        this.mOnTouchListener = mOnTouchListener;
        return this;
    }

    public FastAdapter withMultiSelect(boolean multiSelect) {
        mMultiSelect = multiSelect;
        return this;
    }

    /*
    public FastAdapter wrap(AbstractAdapter abstractAdapter) {
        if (abstractAdapter != null) {
            registerAdapter(abstractAdapter);
            abstractAdapter.setBaseAdapter(this);
            wrap(abstractAdapter.getParentAdapter());
        }
        return this;
    }
    */

    public <A extends AbstractAdapter> void registerAdapter(A adapter) {
        if (!mAdapters.containsKey(adapter.getOrder())) {
            mAdapters.put(adapter.getOrder(), adapter);
        }
    }

    public void registerTypeInstance(IItem item) {
        if (!mTypeInstances.containsKey(item.getType())) {
            mTypeInstances.put(item.getType(), item);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //TODO check fail behavior if a type wasn't registered?
        return mTypeInstances.get(viewType).getViewHolder(parent);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        //TODO check if nullpointer is possible
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

    public IItem getItem(int position) {
        return getInternalItem(position).item;
    }

    private ItemHolder getInternalItem(int position) {
        if (position < 0) {
            return new ItemHolder();
        }

        int currentCount = 0;
        for (AbstractAdapter adapter : mAdapters.values()) {
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

    @Override
    public int getItemViewType(int position) {
        return getItem(position).getType();
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getIdentifier();
    }

    @Override
    public int getItemCount() {
        //we go over all adapters and fetch all item sizes
        int size = 0;
        for (AbstractAdapter adapter : mAdapters.values()) {
            size = adapter.getAdapterItemCount();
        }
        return size;
    }

    public int getItemCount(int order) {
        //we go over all adapters and fetch all item sizes
        int size = 0;
        for (AbstractAdapter adapter : mAdapters.values()) {
            if (adapter.getOrder() < order) {
                size = adapter.getAdapterItemCount();
            } else {
                return size;
            }
        }
        return size;
    }

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

    public void select(int position) {
        IItem item = getItem(position);
        if (item != null) {
            item.withSetSelected(true);
            mSelections.put(position, item);
        }
        notifyItemChanged(position);
    }

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

    public void deselect() {
        for (Map.Entry<Integer, IItem> entry : mSelections.entrySet()) {
            deselect(entry.getKey());
        }
    }

    //-------------------------
    //-------------------------
    //wrap the notify* methods so we can have our required selection adjustment code
    //-------------------------
    //-------------------------
    public void notifyAdapterItemInserted(int position) {
        //we have to update all current stored selection in our map
        adjustSelectionsAfter(position, 1);
        notifyItemInserted(position);
    }

    public void notifyAdapterItemRangeInserted(int position, int itemCount) {
        //we have to update all current stored selection in our map
        adjustSelectionsAfter(position, itemCount);
        notifyItemRangeInserted(position, itemCount);
    }

    public void notifyAdapterItemRemoved(int position) {
        //we have to update all current stored selection in our map
        adjustSelectionsAfter(position, -1);
        notifyItemRemoved(position);
    }

    public void notifyAdapterItemRangeRemoved(int position, int itemCount) {
        //we have to update all current stored selection in our map
        adjustSelectionsAfter(position, itemCount * (-1));
        notifyItemRangeRemoved(position, itemCount);
    }

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

    public void notifyAdapterItemMoved(int fromPosition, int toPosition) {
        if (mSelections.containsKey(fromPosition)) {
            IItem item = mSelections.get(fromPosition);
            mSelections.remove(fromPosition);
            mSelections.put(toPosition, item);
        }
    }

    public void notifyAdapterItemChanged(int position) {
        notifyAdapterItemChanged(position, null);
    }

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

    public void notifyAdapterItemRangeChanged(int position, int itemCount) {
        notifyAdapterItemRangeChanged(position, itemCount, null);
    }

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
        boolean onTouch(View v, MotionEvent event, int position, int relativePosition, IItem item);
    }

    public interface OnClickListener {
        boolean onClick(View v, int position, int relativePosition, IItem item);
    }

    public interface OnLongClickListener {
        boolean onLongClick(View v, int position, int relativePosition, IItem item);
    }

    private class ItemHolder {
        public IItem item = null;
        public int relativePosition = -1;
    }
}
