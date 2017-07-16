package com.mikepenz.fastadapter;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.mikepenz.fastadapter.listeners.ClickEventHook;
import com.mikepenz.fastadapter.listeners.EventHook;
import com.mikepenz.fastadapter.listeners.LongClickEventHook;
import com.mikepenz.fastadapter.listeners.OnBindViewHolderListener;
import com.mikepenz.fastadapter.listeners.OnBindViewHolderListenerImpl;
import com.mikepenz.fastadapter.listeners.OnClickListener;
import com.mikepenz.fastadapter.listeners.OnCreateViewHolderListener;
import com.mikepenz.fastadapter.listeners.OnCreateViewHolderListenerImpl;
import com.mikepenz.fastadapter.listeners.OnLongClickListener;
import com.mikepenz.fastadapter.listeners.OnTouchListener;
import com.mikepenz.fastadapter.listeners.TouchEventHook;
import com.mikepenz.fastadapter.select.SelectExtension;
import com.mikepenz.fastadapter.utils.EventHookUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static com.mikepenz.fastadapter.adapters.ItemAdapter.items;

/**
 * Created by mikepenz on 27.12.15.
 */
public class FastAdapter<Item extends IItem> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "FastAdapter";

    // we remember all adapters
    //priority queue...
    final private ArrayList<IAdapter<Item>> mAdapters = new ArrayList<>();
    // we remember all possible types so we can create a new view efficiently
    final private SparseArray<Item> mTypeInstances = new SparseArray<>();
    // cache the sizes of the different adapters so we can access the items more performant
    final private SparseArray<IAdapter<Item>> mAdapterSizes = new SparseArray<>();
    // the total size
    private int mGlobalSize = 0;

    // event hooks for the items
    private List<EventHook<Item>> eventHooks;
    // the extensions we support
    final private Set<IAdapterExtension<Item>> mExtensions = new HashSet<>();

    //
    private SelectExtension<Item> mSelectExtension = new SelectExtension<>();
    // legacy bindView mode. if activated we will forward onBindView without paylodas to the method with payloads
    private boolean mLegacyBindViewMode = false;

    // verbose
    private boolean mVerbose = false;

    // the listeners which can be hooked on an item
    private OnClickListener<Item> mOnPreClickListener;
    private OnClickListener<Item> mOnClickListener;
    private OnLongClickListener<Item> mOnPreLongClickListener;
    private OnLongClickListener<Item> mOnLongClickListener;
    private OnTouchListener<Item> mOnTouchListener;

    //the listeners for onCreateViewHolder or onBindViewHolder
    private OnCreateViewHolderListener mOnCreateViewHolderListener = new OnCreateViewHolderListenerImpl();
    private OnBindViewHolderListener mOnBindViewHolderListener = new OnBindViewHolderListenerImpl();

    private static int floorIndex(SparseArray<?> sparseArray, int key) {
        int index = sparseArray.indexOfKey(key);
        if (index < 0) {
            index = ~index - 1;
        }
        return index;
    }

    /**
     * default CTOR
     */
    public FastAdapter() {
        setHasStableIds(true);
    }

    /**
     * enables the verbose log for the adapter
     *
     * @return this
     */
    public FastAdapter<Item> enableVerboseLog() {
        this.mVerbose = true;
        return this;
    }

    /**
     * creates a new FastAdapter with the provided adapters
     * if adapters is null, a default ItemAdapter is defined
     *
     * @param adapter the adapters which this FastAdapter should use
     * @return a new FastAdapter
     */
    public static <Item extends IItem, A extends IAdapter> FastAdapter<Item> with(A adapter) {
        FastAdapter<Item> fastAdapter = new FastAdapter<>();
        fastAdapter.addAdapter(0, adapter);
        return fastAdapter;
    }

    /**
     * creates a new FastAdapter with the provided adapters
     * if adapters is null, a default ItemAdapter is defined
     *
     * @param adapters the adapters which this FastAdapter should use
     * @return a new FastAdapter
     */
    public static <Item extends IItem, A extends IAdapter> FastAdapter<Item> with(@Nullable Collection<A> adapters) {
        return with(adapters, null);
    }

    /**
     * creates a new FastAdapter with the provided adapters
     * if adapters is null, a default ItemAdapter is defined
     *
     * @param adapters the adapters which this FastAdapter should use
     * @return a new FastAdapter
     */
    public static <Item extends IItem, A extends IAdapter> FastAdapter<Item> with(@Nullable Collection<A> adapters, @Nullable Collection<IAdapterExtension<Item>> extensions) {
        FastAdapter<Item> fastAdapter = new FastAdapter<>();
        if (adapters == null) {
            fastAdapter.mAdapters.add((IAdapter<Item>) items());
        } else {
            for (A adapter : adapters) {
                fastAdapter.mAdapters.add(adapter);
            }
        }
        for (int i = 0; i < fastAdapter.mAdapters.size(); i++) {
            fastAdapter.mAdapters.get(i).withFastAdapter(fastAdapter).setOrder(i);
        }
        fastAdapter.cacheSizes();

        if (extensions != null) {
            for (IAdapterExtension<Item> extension : extensions) {
                fastAdapter.addExtension(extension);
            }
        }

        return fastAdapter;
    }

    /**
     * add's a new adapter at the specific position
     *
     * @param index   the index where the new adapter should be added
     * @param adapter the new adapter to be added
     * @return this
     */
    public <A extends IAdapter<Item>> FastAdapter<Item> addAdapter(int index, A adapter) {
        mAdapters.add(index, adapter);
        for (int i = 0; i < mAdapters.size(); i++) {
            mAdapters.get(i).withFastAdapter(this).setOrder(i);
        }
        cacheSizes();
        return this;
    }

    /**
     * Tries to get an adapter by a specific order
     *
     * @param order the order (position) to search the adapter at
     * @return the IAdapter if found
     */
    @Nullable
    public IAdapter<Item> adapter(int order) {
        if (mAdapters.size() <= order) {
            return null;
        }
        return mAdapters.get(order);
    }

    /**
     * @param extension
     * @return
     */
    public <E extends IAdapterExtension<Item>> FastAdapter<Item> addExtension(E extension) {
        mExtensions.add(extension);
        extension.init(this);
        return this;
    }

    /**
     * @return the AdapterExtensions we provided
     */
    public Set<IAdapterExtension<Item>> getExtensions() {
        return mExtensions;
    }

    /**
     * adds a new event hook for an item
     * NOTE: this has to be called before adding the first items, as this won't be called anymore after the ViewHolders were created
     *
     * @param eventHook the event hook to be added for an item
     * @return this
     * @deprecated please use `withEventHook`
     */
    @Deprecated
    public FastAdapter<Item> withItemEvent(EventHook<Item> eventHook) {
        return withEventHook(eventHook);
    }

    /**
     * @return the eventHooks handled by this FastAdapter
     */
    public List<EventHook<Item>> getEventHooks() {
        return eventHooks;
    }

    /**
     * adds a new event hook for an item
     * NOTE: this has to be called before adding the first items, as this won't be called anymore after the ViewHolders were created
     *
     * @param eventHook the event hook to be added for an item
     * @return this
     */
    public FastAdapter<Item> withEventHook(EventHook<Item> eventHook) {
        if (eventHooks == null) {
            eventHooks = new LinkedList<>();
        }
        eventHooks.add(eventHook);
        return this;
    }

    /**
     * adds new event hooks for an item
     * NOTE: this has to be called before adding the first items, as this won't be called anymore after the ViewHolders were created
     *
     * @param eventHooks the event hooks to be added for an item
     * @return this
     */
    public FastAdapter<Item> withEventHooks(@Nullable Collection<? extends EventHook<Item>> eventHooks) {
        if (eventHooks == null) {
            return this;
        }
        if (this.eventHooks == null) {
            this.eventHooks = new LinkedList<>();
        }
        this.eventHooks.addAll(eventHooks);
        return this;
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
     * @return the set OnClickListener
     */
    public OnClickListener<Item> getOnClickListener() {
        return mOnClickListener;
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
        mSelectExtension.withSelectWithItemUpdate(selectWithItemUpdate);
        return this;
    }

    /**
     * Enable this if you want multiSelection possible in the list
     *
     * @param multiSelect true to enable multiSelect
     * @return this
     */
    public FastAdapter<Item> withMultiSelect(boolean multiSelect) {
        mSelectExtension.withMultiSelect(multiSelect);
        return this;
    }

    /**
     * Disable this if you want the selection on a single tap
     *
     * @param selectOnLongClick false to do select via single click
     * @return this
     */
    public FastAdapter<Item> withSelectOnLongClick(boolean selectOnLongClick) {
        mSelectExtension.withSelectOnLongClick(selectOnLongClick);
        return this;
    }

    /**
     * If false, a user can't deselect an item via click (you can still do this programmatically)
     *
     * @param allowDeselection true if a user can deselect an already selected item via click
     * @return this
     */
    public FastAdapter<Item> withAllowDeselection(boolean allowDeselection) {
        mSelectExtension.withAllowDeselection(allowDeselection);
        return this;
    }

    /**
     * set if no item is selectable
     *
     * @param selectable true if items are selectable
     * @return this
     */
    public FastAdapter<Item> withSelectable(boolean selectable) {
        if (selectable) {
            addExtension(mSelectExtension);
        } else {
            mExtensions.remove(mSelectExtension);
        }
        //TODO revisit this --> false means anyways that it is not in the extension list!
        mSelectExtension.withSelectable(selectable);
        return this;
    }

    /**
     * set to true if you want the FastAdapter to forward all calls from onBindViewHolder(final RecyclerView.ViewHolder holder, int position) to onBindViewHolder(final RecyclerView.ViewHolder holder, int position, List payloads)
     *
     * @param legacyBindViewMode true if you want to activate it (default = false)
     * @return this
     */
    public FastAdapter<Item> withLegacyBindViewMode(boolean legacyBindViewMode) {
        this.mLegacyBindViewMode = legacyBindViewMode;
        return this;
    }

    /**
     * set a listener that get's notified whenever an item is selected or deselected
     *
     * @param selectionListener the listener that will be notified about selection changes
     * @return this
     */
    public FastAdapter<Item> withSelectionListener(ISelectionListener<Item> selectionListener) {
        mSelectExtension.withSelectionListener(selectionListener);
        return this;
    }

    /**
     * @return if items are selectable
     */
    public boolean isSelectable() {
        return mSelectExtension.isSelectable();
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
    public FastAdapter<Item> withSavedInstanceState(@Nullable Bundle savedInstanceState, String prefix) {
        for (IAdapterExtension<Item> ext : mExtensions) {
            ext.withSavedInstanceState(savedInstanceState, prefix);
        }

        return this;
    }

    /**
     * register a new type into the TypeInstances to be able to efficiently create thew ViewHolders
     *
     * @param item an IItem which will be shown in the list
     */
    public void registerTypeInstance(Item item) {
        if (mTypeInstances.indexOfKey(item.getType()) < 0) {
            mTypeInstances.put(item.getType(), item);
            //check if the item implements hookable when its added for the first time
            if (item instanceof IHookable) {
                withEventHooks(((IHookable<Item>) item).getEventHooks());
            }
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
     * clears the internal mapper - be sure, to remap everything before going on
     */
    public void clearTypeInstance() {
        mTypeInstances.clear();
    }

    /**
     * helper method to get the position from a holder
     * overwrite this if you have an adapter adding additional items inbetwean
     *
     * @param holder the viewHolder of the item
     * @return the position of the holder
     */
    public int getHolderAdapterPosition(RecyclerView.ViewHolder holder) {
        return holder.getAdapterPosition();
    }

    /**
     * the ClickEventHook to hook onto the itemView of a viewholder
     */
    private ClickEventHook<Item> fastAdapterViewClickListener = new ClickEventHook<Item>() {
        @Override
        public void onClick(View v, int pos, FastAdapter<Item> fastAdapter, Item item) {
            IAdapter<Item> adapter = fastAdapter.getAdapter(pos);
            if (adapter != null && item != null && item.isEnabled()) {
                boolean consumed = false;
                //on the very first we call the click listener from the item itself (if defined)
                if (item instanceof IClickable && ((IClickable) item).getOnPreItemClickListener() != null) {
                    consumed = ((IClickable<Item>) item).getOnPreItemClickListener().onClick(v, adapter, item, pos);
                }

                //first call the onPreClickListener which would allow to prevent executing of any following code, including selection
                if (!consumed && fastAdapter.mOnPreClickListener != null) {
                    consumed = fastAdapter.mOnPreClickListener.onClick(v, adapter, item, pos);
                }

                // handle our extensions
                for (IAdapterExtension<Item> ext : fastAdapter.mExtensions) {
                    if (!consumed) {
                        consumed = ext.onClick(v, pos, fastAdapter, item);
                    } else {
                        break;
                    }
                }

                //before calling the global adapter onClick listener call the item specific onClickListener
                if (!consumed && item instanceof IClickable && ((IClickable) item).getOnItemClickListener() != null) {
                    consumed = ((IClickable<Item>) item).getOnItemClickListener().onClick(v, adapter, item, pos);
                }

                //call the normal click listener after selection was handlded
                if (!consumed && fastAdapter.mOnClickListener != null) {
                    fastAdapter.mOnClickListener.onClick(v, adapter, item, pos);
                }
            }
        }
    };

    /**
     * the LongClickEventHook to hook onto the itemView of a viewholder
     */
    private LongClickEventHook<Item> fastAdapterViewLongClickListener = new LongClickEventHook<Item>() {
        @Override
        public boolean onLongClick(View v, int pos, FastAdapter<Item> fastAdapter, Item item) {
            boolean consumed = false;
            IAdapter<Item> adapter = fastAdapter.getAdapter(pos);
            if (adapter != null && item != null && item.isEnabled()) {
                //first call the OnPreLongClickListener which would allow to prevent executing of any following code, including selection
                if (fastAdapter.mOnPreLongClickListener != null) {
                    consumed = fastAdapter.mOnPreLongClickListener.onLongClick(v, adapter, item, pos);
                }

                // handle our extensions
                for (IAdapterExtension<Item> ext : fastAdapter.mExtensions) {
                    if (!consumed) {
                        consumed = ext.onLongClick(v, pos, fastAdapter, item);
                    } else {
                        break;
                    }
                }

                //call the normal long click listener after selection was handled
                if (!consumed && fastAdapter.mOnLongClickListener != null) {
                    consumed = fastAdapter.mOnLongClickListener.onLongClick(v, adapter, item, pos);
                }
            }
            return consumed;
        }
    };

    /**
     * the TouchEventHook to hook onto the itemView of a viewholder
     */
    private TouchEventHook<Item> fastAdapterViewTouchListener = new TouchEventHook<Item>() {
        @Override
        public boolean onTouch(View v, MotionEvent event, int position, FastAdapter<Item> fastAdapter, Item item) {
            boolean consumed = false;
            // handle our extensions
            for (IAdapterExtension<Item> ext : fastAdapter.mExtensions) {
                if (!consumed) {
                    consumed = ext.onTouch(v, event, position, fastAdapter, item);
                } else {
                    break;
                }
            }
            if (fastAdapter.mOnTouchListener != null) {
                IAdapter<Item> adapter = fastAdapter.getAdapter(position);
                if (adapter != null) {
                    return fastAdapter.mOnTouchListener.onTouch(v, event, adapter, item, position);
                }
            }
            return consumed;
        }
    };

    /**
     * Creates the ViewHolder by the viewType
     *
     * @param parent   the parent view (the RecyclerView)
     * @param viewType the current viewType which is bound
     * @return the ViewHolder with the bound data
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mVerbose) Log.v(TAG, "onCreateViewHolder: " + viewType);

        final RecyclerView.ViewHolder holder = mOnCreateViewHolderListener.onPreCreateViewHolder(this, parent, viewType);

        //set the adapter
        holder.itemView.setTag(R.id.fastadapter_item_adapter, FastAdapter.this);

        //handle click behavior
        EventHookUtil.attachToView(fastAdapterViewClickListener, holder, holder.itemView);

        //handle long click behavior
        EventHookUtil.attachToView(fastAdapterViewLongClickListener, holder, holder.itemView);

        //handle touch behavior
        EventHookUtil.attachToView(fastAdapterViewTouchListener, holder, holder.itemView);

        return mOnCreateViewHolderListener.onPostCreateViewHolder(this, holder);
    }

    /**
     * Binds the data to the created ViewHolder and sets the listeners to the holder.itemView
     * Note that you should use the `onBindViewHolder(RecyclerView.ViewHolder holder, int position, List payloads`
     * as it allows you to implement a more efficient adapter implementation
     *
     * @param holder   the viewHolder we bind the data on
     * @param position the global position
     */
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (mLegacyBindViewMode) {
            if (mVerbose) {
                Log.v(TAG, "onBindViewHolderLegacy: " + position + "/" + holder.getItemViewType() + " isLegacy: true");
            }
            //set the R.id.fastadapter_item_adapter tag to the adapter so we always have the proper bound adapter available
            holder.itemView.setTag(R.id.fastadapter_item_adapter, this);
            //now we bind the item to this viewHolder
            mOnBindViewHolderListener.onBindViewHolder(holder, position, Collections.EMPTY_LIST);
        }
    }

    /**
     * Binds the data to the created ViewHolder and sets the listeners to the holder.itemView
     *
     * @param holder   the viewHolder we bind the data on
     * @param position the global position
     * @param payloads the payloads for the bindViewHolder event containing data which allows to improve view animating
     */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position, List<Object> payloads) {
        //we do not want the binding to happen twice (the legacyBindViewMode
        if (!mLegacyBindViewMode) {
            if (mVerbose)
                Log.v(TAG, "onBindViewHolder: " + position + "/" + holder.getItemViewType() + " isLegacy: false");
            //set the R.id.fastadapter_item_adapter tag to the adapter so we always have the proper bound adapter available
            holder.itemView.setTag(R.id.fastadapter_item_adapter, this);
            //now we bind the item to this viewHolder
            mOnBindViewHolderListener.onBindViewHolder(holder, position, payloads);
        }
        super.onBindViewHolder(holder, position, payloads);
    }

    /**
     * Unbinds the data to the already existing ViewHolder and removes the listeners from the holder.itemView
     *
     * @param holder the viewHolder we unbind the data from
     */
    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder) {
        if (mVerbose) Log.v(TAG, "onViewRecycled: " + holder.getItemViewType());
        super.onViewRecycled(holder);
        mOnBindViewHolderListener.unBindViewHolder(holder, holder.getAdapterPosition());
    }

    /**
     * is called in onViewDetachedFromWindow when the view is detached from the window
     *
     * @param holder the viewHolder for the view which got detached
     */
    @Override
    public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
        if (mVerbose) Log.v(TAG, "onViewDetachedFromWindow: " + holder.getItemViewType());
        super.onViewDetachedFromWindow(holder);
        mOnBindViewHolderListener.onViewDetachedFromWindow(holder, holder.getAdapterPosition());
    }

    /**
     * is called in onViewAttachedToWindow when the view is detached from the window
     *
     * @param holder the viewHolder for the view which got detached
     */
    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        if (mVerbose) Log.v(TAG, "onViewAttachedToWindow: " + holder.getItemViewType());
        super.onViewAttachedToWindow(holder);
        mOnBindViewHolderListener.onViewAttachedToWindow(holder, holder.getAdapterPosition());
    }

    /**
     * is called when the ViewHolder is in a transient state. return true if you want to reuse
     * that view anyways
     *
     * @param holder the viewHolder for the view which failed to recycle
     * @return true if we want to recycle anyways (false - it get's destroyed)
     */
    @Override
    public boolean onFailedToRecycleView(RecyclerView.ViewHolder holder) {
        if (mVerbose) Log.v(TAG, "onFailedToRecycleView: " + holder.getItemViewType());
        return mOnBindViewHolderListener.onFailedToRecycleView(holder, holder.getAdapterPosition()) || super.onFailedToRecycleView(holder);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        if (mVerbose) Log.v(TAG, "onAttachedToRecyclerView");
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        if (mVerbose) Log.v(TAG, "onDetachedFromRecyclerView");
        super.onDetachedFromRecyclerView(recyclerView);
    }

    /**
     * Searches for the given item and calculates its global position
     *
     * @param item the item which is searched for
     * @return the global position, or -1 if not found
     */
    public int getPosition(Item item) {
        if (item.getIdentifier() == -1) {
            Log.e("FastAdapter", "You have to define an identifier for your item to retrieve the position via this method");
            return -1;
        }
        return getPosition(item.getIdentifier());
    }

    /**
     * Searches for the given item and calculates its global position
     *
     * @param identifier the identifier of an item which is searched for
     * @return the global position, or -1 if not found
     */
    public int getPosition(long identifier) {
        int position = 0;
        for (IAdapter<Item> adapter : mAdapters) {
            if (adapter.getOrder() < 0) {
                continue;
            }

            int relativePosition = adapter.getAdapterPosition(identifier);
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
        int index = floorIndex(mAdapterSizes, position);
        return mAdapterSizes.valueAt(index).getAdapterItem(position - mAdapterSizes.keyAt(index));
    }

    /**
     * Internal method to get the Item as ItemHolder which comes with the relative position within its adapter
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
        int index = floorIndex(mAdapterSizes, position);
        if (index != -1) {
            relativeInfo.item = mAdapterSizes.valueAt(index).getAdapterItem(position - mAdapterSizes.keyAt(index));
            relativeInfo.adapter = mAdapterSizes.valueAt(index);
            relativeInfo.position = position;
        }
        return relativeInfo;
    }

    /**
     * Gets the adapter for the given position
     *
     * @param position the global position
     * @return the adapter responsible for this global position
     */
    @Nullable
    public IAdapter<Item> getAdapter(int position) {
        //if we are out of range just return null
        if (position < 0 || position >= mGlobalSize) {
            return null;
        }
        if (mVerbose) Log.v(TAG, "getAdapter");
        //now get the adapter which is responsible for the given position
        return mAdapterSizes.valueAt(floorIndex(mAdapterSizes, position));
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
        for (int i = 0; i < Math.min(order, mAdapters.size()); i++) {
            size = size + mAdapters.get(i).getAdapterItemCount();
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
        return mAdapterSizes.keyAt(floorIndex(mAdapterSizes, position));
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
        // handle our extensions
        for (IAdapterExtension<Item> ext : mExtensions) {
            ext.saveInstanceState(savedInstanceState, prefix);
        }
        return savedInstanceState;
    }

    /**
     * we cache the sizes of our adapters so get accesses are faster
     */
    protected void cacheSizes() {
        mAdapterSizes.clear();
        int size = 0;

        for (IAdapter<Item> adapter : mAdapters) {
            if (adapter.getAdapterItemCount() > 0) {
                mAdapterSizes.append(size, adapter);
                size = size + adapter.getAdapterItemCount();
            }
        }

        //we also have to add this for the first adapter otherwise the floorIndex method will return the wrong value
        if (size == 0 && mAdapters.size() > 0) {
            mAdapterSizes.append(0, mAdapters.get(0));
        }

        mGlobalSize = size;
    }

    //-------------------------
    //-------------------------
    //Selection stuff
    //-------------------------
    //-------------------------


    /**
     * @return the selectExtension defined for this FastAdaper
     */
    public SelectExtension<Item> getSelectExtension() {
        return mSelectExtension;
    }

    /**
     * @return a set with the global positions of all selected items
     */
    public Set<Integer> getSelections() {
        return mSelectExtension.getSelections();
    }


    /**
     * @return a set with the items which are currently selected
     */
    public Set<Item> getSelectedItems() {
        return mSelectExtension.getSelectedItems();
    }

    /**
     * toggles the selection of the item at the given position
     *
     * @param position the global position
     */
    public void toggleSelection(int position) {
        mSelectExtension.toggleSelection(position);
    }

    /**
     * selects all items at the positions in the iteratable
     *
     * @param positions the global positions to select
     */
    public void select(Iterable<Integer> positions) {
        mSelectExtension.select(positions);
    }

    /**
     * selects an item and remembers its position in the selections list
     *
     * @param position the global position
     */
    public void select(int position) {
        mSelectExtension.select(position, false, false);
    }

    /**
     * selects an item and remembers its position in the selections list
     *
     * @param position  the global position
     * @param fireEvent true if the onClick listener should be called
     */
    public void select(int position, boolean fireEvent) {
        mSelectExtension.select(position, fireEvent, false);
    }

    /**
     * selects an item and remembers its position in the selections list
     *
     * @param position               the global position
     * @param fireEvent              true if the onClick listener should be called
     * @param considerSelectableFlag true if the select method should not select an item if its not selectable
     */
    public void select(int position, boolean fireEvent, boolean considerSelectableFlag) {
        mSelectExtension.select(position, fireEvent, considerSelectableFlag);
    }

    /**
     * deselects all selections
     */
    public void deselect() {
        mSelectExtension.deselect();
    }

    /**
     * select all items
     */
    public void select() {
        mSelectExtension.select(false);
    }

    /**
     * select all items
     *
     * @param considerSelectableFlag true if the select method should not select an item if its not selectable
     */
    public void select(boolean considerSelectableFlag) {
        mSelectExtension.select(considerSelectableFlag);
    }

    /**
     * deselects all items at the positions in the iteratable
     *
     * @param positions the global positions to deselect
     */
    public void deselect(Iterable<Integer> positions) {
        mSelectExtension.deselect(positions);
    }

    /**
     * deselects an item and removes its position in the selections list
     *
     * @param position the global position
     */
    public void deselect(int position) {
        mSelectExtension.deselect(position, null);
    }

    /**
     * deselects an item and removes its position in the selections list
     * also takes an iterator to remove items from the map
     *
     * @param position the global position
     * @param entries  the iterator which is used to deselect all
     */
    public void deselect(int position, Iterator<Integer> entries) {
        mSelectExtension.deselect(position, entries);
    }

    /**
     * deletes all current selected items
     *
     * @return a list of the IItem elements which were deleted
     */
    public List<Item> deleteAllSelectedItems() {
        return mSelectExtension.deleteAllSelectedItems();
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
        // handle our extensions
        for (IAdapterExtension<Item> ext : mExtensions) {
            ext.notifyAdapterDataSetChanged();
        }
        cacheSizes();
        notifyDataSetChanged();
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
        // handle our extensions
        for (IAdapterExtension<Item> ext : mExtensions) {
            ext.notifyAdapterItemRangeInserted(position, itemCount);
        }
        cacheSizes();
        notifyItemRangeInserted(position, itemCount);
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
        // handle our extensions
        for (IAdapterExtension<Item> ext : mExtensions) {
            ext.notifyAdapterItemRangeRemoved(position, itemCount);
        }

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
        // handle our extensions
        for (IAdapterExtension<Item> ext : mExtensions) {
            ext.notifyAdapterItemMoved(fromPosition, toPosition);
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
        // handle our extensions
        for (IAdapterExtension<Item> ext : mExtensions) {
            ext.notifyAdapterItemRangeChanged(position, itemCount, payload);
        }
        if (payload == null) {
            notifyItemRangeChanged(position, itemCount);
        } else {
            notifyItemRangeChanged(position, itemCount, payload);
        }
    }

    /**
     * an internal class to return the IItem and relativePosition and its adapter at once. used to save one iteration inside the getInternalItem method
     */
    public static class RelativeInfo<Item extends IItem> {
        public IAdapter<Item> adapter = null;
        public Item item = null;
        public int position = -1;
    }
}
