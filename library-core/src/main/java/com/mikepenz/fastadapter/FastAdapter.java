package com.mikepenz.fastadapter;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.util.ArrayMap;
import android.support.v4.util.Pair;
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
import com.mikepenz.fastadapter.utils.AdapterPredicate;
import com.mikepenz.fastadapter.utils.DefaultTypeInstanceCache;
import com.mikepenz.fastadapter.utils.EventHookUtil;
import com.mikepenz.fastadapter.utils.Triple;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import static com.mikepenz.fastadapter.adapters.ItemAdapter.items;

/**
 * The `FastAdapter` class is the core managing class of the `FastAdapter` library, it handles all `IAdapter` implementations, keeps track of the item types which can be displayed
 * and correctly provides the size and position and identifier information to the {@link RecyclerView}.
 * <p>
 * It also comes with {@link IAdapterExtension} allowing to further modify its behaviour.
 * Additionally it allows to attach various different listener's, and also {@link EventHook}s on per item and view basis.
 * <p>
 * See the sample application for more details
 *
 * @param <Item> Defines the type of items this `FastAdapter` manages (in case of multiple different types, use `IItem`)
 */
public class FastAdapter<Item extends IItem> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "FastAdapter";

    // we remember all adapters
    //priority queue...
    final private ArrayList<IAdapter<Item>> mAdapters = new ArrayList<>();
    // we remember all possible types so we can create a new view efficiently
    private ITypeInstanceCache<Item> mTypeInstanceCache;
    // cache the sizes of the different adapters so we can access the items more performant
    final private SparseArray<IAdapter<Item>> mAdapterSizes = new SparseArray<>();
    // the total size
    private int mGlobalSize = 0;

    // event hooks for the items
    private List<EventHook<Item>> eventHooks;
    // the extensions we support
    final private Map<Class, IAdapterExtension<Item>> mExtensions = new ArrayMap<>();

    //
    private SelectExtension<Item> mSelectExtension = new SelectExtension<>();
    // legacy bindView mode. if activated we will forward onBindView without payloads to the method with payloads
    private boolean mLegacyBindViewMode = false;
    // if set to `false` will not attach any listeners to the list. click events will have to be handled manually
    private boolean mAttachDefaultListeners = true;

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
     * Sets an type instance cache to this fast adapter instance.
     * The cache will manage the type instances to create new views more efficient.
     * Normally an shared cache is used over all adapter instances.
     *
     * @param mTypeInstanceCache a custom `TypeInstanceCache` implementation
     */
    public void setTypeInstanceCache(ITypeInstanceCache<Item> mTypeInstanceCache) {
        this.mTypeInstanceCache = mTypeInstanceCache;
    }

    /**
     * @return the current type instance cache
     */
    public ITypeInstanceCache<Item> getTypeInstanceCache() {
        if (mTypeInstanceCache == null) {
            mTypeInstanceCache = new DefaultTypeInstanceCache<>();
        }
        return mTypeInstanceCache;
    }

    /**
     * creates a new FastAdapter with the provided adapters
     * if adapters is null, a default ItemAdapter is defined
     *
     * @param adapter the adapters which this FastAdapter should use
     * @return a new FastAdapter
     */
    @SuppressWarnings("unchecked")
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
    @SuppressWarnings("unchecked")
    public static <Item extends IItem, A extends IAdapter> FastAdapter<Item> with(@Nullable Collection<A> adapters, @Nullable Collection<IAdapterExtension<Item>> extensions) {
        FastAdapter<Item> fastAdapter = new FastAdapter<>();
        if (adapters == null) {
            fastAdapter.mAdapters.add((IAdapter<Item>) items());
        } else {
            fastAdapter.mAdapters.addAll((Collection<IAdapter<Item>>) adapters);
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
        adapter.withFastAdapter(this);
        adapter.mapPossibleTypes(adapter.getAdapterItems());
        for (int i = 0; i < mAdapters.size(); i++) {
            mAdapters.get(i).setOrder(i);
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
        if (mExtensions.containsKey(extension.getClass())) {
            throw new IllegalStateException("The given extension was already registered with this FastAdapter instance");
        }
        mExtensions.put(extension.getClass(), extension);
        extension.init(this);
        return this;
    }

    /**
     * @return the AdapterExtensions we provided
     */
    public Collection<IAdapterExtension<Item>> getExtensions() {
        return mExtensions.values();
    }

    /**
     * @param clazz the extension class, to retrieve its instance
     * @return the found IAdapterExtension or null if it is not found
     */
    @Nullable
    @SuppressWarnings("unchecked")
    public <T extends IAdapterExtension<Item>> T getExtension(Class<? super T> clazz) {
        return (T) mExtensions.get(clazz);
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
            mExtensions.remove(mSelectExtension.getClass());
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
     * if set to `false` will not attach any listeners to the list. click events will have to be handled manually
     * It is important to remember, if deactivated no listeners won't be attached at a later time either, as the
     * listeners are only attached at ViewHolder creation time.
     *
     * @param mAttachDefaultListeners false if you want no listeners attached to the item (default = true)
     * @return this
     */
    public FastAdapter<Item> withAttachDefaultListeners(boolean mAttachDefaultListeners) {
        this.mAttachDefaultListeners = mAttachDefaultListeners;
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
        for (IAdapterExtension<Item> ext : mExtensions.values()) {
            ext.withSavedInstanceState(savedInstanceState, prefix);
        }

        return this;
    }

    /**
     * register a new type into the TypeInstances to be able to efficiently create thew ViewHolders
     *
     * @param item an IItem which will be shown in the list
     */
    @SuppressWarnings("unchecked")
    public void registerTypeInstance(Item item) {
        if (getTypeInstanceCache().register(item)) {
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
        return getTypeInstanceCache().get(type);
    }

    /**
     * clears the internal mapper - be sure, to remap everything before going on
     */
    public void clearTypeInstance() {
        getTypeInstanceCache().clear();
    }

    /**
     * helper method to get the position from a holder
     * overwrite this if you have an adapter adding additional items inbetwean
     *
     * @param holder the viewHolder of the item
     * @return the position of the holder
     */
    public int getHolderAdapterPosition(@NonNull RecyclerView.ViewHolder holder) {
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
                for (IAdapterExtension<Item> ext : fastAdapter.mExtensions.values()) {
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
                for (IAdapterExtension<Item> ext : fastAdapter.mExtensions.values()) {
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
            for (IAdapterExtension<Item> ext : fastAdapter.mExtensions.values()) {
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
    @SuppressWarnings("unchecked")
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mVerbose) Log.v(TAG, "onCreateViewHolder: " + viewType);

        final RecyclerView.ViewHolder holder = mOnCreateViewHolderListener.onPreCreateViewHolder(this, parent, viewType);

        //set the adapter
        holder.itemView.setTag(R.id.fastadapter_item_adapter, FastAdapter.this);

        if (mAttachDefaultListeners) {
            //handle click behavior
            EventHookUtil.attachToView(fastAdapterViewClickListener, holder, holder.itemView);

            //handle long click behavior
            EventHookUtil.attachToView(fastAdapterViewLongClickListener, holder, holder.itemView);

            //handle touch behavior
            EventHookUtil.attachToView(fastAdapterViewTouchListener, holder, holder.itemView);
        }

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
    @SuppressWarnings("unchecked")
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
     * gets the IItem given an identifier, from all registered adapters
     *
     * @param identifier the identifier of the searched item
     * @return the found Pair&lt;IItem, Integer&gt; (the found item, and it's global position if it is currently displayed) or null
     */
    @SuppressWarnings("unchecked")
    public Pair<Item, Integer> getItemById(final long identifier) {
        if (identifier == -1) {
            return null;
        }
        Triple result = recursive(new AdapterPredicate() {
            @Override
            public boolean apply(@NonNull IAdapter lastParentAdapter, int lastParentPosition, @NonNull IItem item, int position) {
                return item.getIdentifier() == identifier;
            }
        }, true);
        if (result.second == null) {
            return null;
        } else {
            return new Pair(result.second, result.third);
        }
    }

    /**
     * Internal method to get the Item as ItemHolder which comes with the relative position within its adapter
     * Finds the responsible adapter for the given position
     *
     * @param position the global position
     * @return the adapter which is responsible for this position
     */
    public RelativeInfo<Item> getRelativeInfo(int position) {
        if (position < 0 || position >= getItemCount()) {
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
    public Bundle saveInstanceState(@Nullable Bundle savedInstanceState) {
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
    public Bundle saveInstanceState(@Nullable Bundle savedInstanceState, String prefix) {
        // handle our extensions
        for (IAdapterExtension<Item> ext : mExtensions.values()) {
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
    //Convenient getters
    //-------------------------
    //-------------------------

    /**
     * @return the `ClickEventHook` which is attached by default (if not deactivated) via `withAttachDefaultListeners`
     * @see #withAttachDefaultListeners(boolean)
     */
    public ClickEventHook<Item> getViewClickListener() {
        return fastAdapterViewClickListener;
    }

    /**
     * @return the `LongClickEventHook` which is attached by default (if not deactivated) via `withAttachDefaultListeners`
     * @see #withAttachDefaultListeners(boolean)
     */
    public LongClickEventHook<Item> getViewLongClickListener() {
        return fastAdapterViewLongClickListener;
    }

    /**
     * @return the `TouchEventHook` which is attached by default (if not deactivated) via `withAttachDefaultListeners`
     * @see #withAttachDefaultListeners(boolean)
     */
    public TouchEventHook<Item> getViewTouchListener() {
        return fastAdapterViewTouchListener;
    }

    //-------------------------
    //-------------------------
    //Selection stuff
    //-------------------------
    //-------------------------

    /**
     * @return the selectExtension defined for this FastAdaper
     * @deprecated deprecated in favor of {@link #getExtension(Class)}
     */
    @Deprecated
    public SelectExtension<Item> getSelectExtension() {
        return mSelectExtension;
    }

    /**
     * @return a set with the global positions of all selected items
     * @deprecated deprecated in favor of {@link SelectExtension#getSelections()} , Retrieve it via {@link #getExtension(Class)}
     */
    @Deprecated
    public Set<Integer> getSelections() {
        return mSelectExtension.getSelections();
    }


    /**
     * @return a set with the items which are currently selected
     * @deprecated deprecated in favor of {@link SelectExtension#getSelectedItems()} , Retrieve it via {@link #getExtension(Class)}
     */
    @Deprecated
    public Set<Item> getSelectedItems() {
        return mSelectExtension.getSelectedItems();
    }

    /**
     * toggles the selection of the item at the given position
     *
     * @param position the global position
     * @deprecated deprecated in favor of {@link SelectExtension#toggleSelection(int)} , Retrieve it via {@link #getExtension(Class)}
     */
    @Deprecated
    public void toggleSelection(int position) {
        mSelectExtension.toggleSelection(position);
    }

    /**
     * selects all items at the positions in the iteratable
     *
     * @param positions the global positions to select
     * @deprecated deprecated in favor of {@link SelectExtension#select(Iterable)}  , Retrieve it via {@link #getExtension(Class)}
     */
    @Deprecated
    public void select(Iterable<Integer> positions) {
        mSelectExtension.select(positions);
    }

    /**
     * selects an item and remembers its position in the selections list
     *
     * @param position the global position
     * @deprecated deprecated in favor of {@link SelectExtension#select(int)}, Retrieve it via {@link #getExtension(Class)}
     */
    @Deprecated
    public void select(int position) {
        mSelectExtension.select(position, false, false);
    }

    /**
     * selects an item and remembers its position in the selections list
     *
     * @param position  the global position
     * @param fireEvent true if the onClick listener should be called
     * @deprecated deprecated in favor of {@link SelectExtension#select(int, boolean)} , Retrieve it via {@link #getExtension(Class)}
     */
    @Deprecated
    public void select(int position, boolean fireEvent) {
        mSelectExtension.select(position, fireEvent, false);
    }

    /**
     * selects an item and remembers its position in the selections list
     *
     * @param position               the global position
     * @param fireEvent              true if the onClick listener should be called
     * @param considerSelectableFlag true if the select method should not select an item if its not selectable
     * @deprecated deprecated in favor of {@link SelectExtension#select(int, boolean, boolean)} , Retrieve it via {@link #getExtension(Class)}
     */
    @Deprecated
    public void select(int position, boolean fireEvent, boolean considerSelectableFlag) {
        mSelectExtension.select(position, fireEvent, considerSelectableFlag);
    }

    /**
     * deselects all selections
     *
     * @deprecated deprecated in favor of {@link SelectExtension#deselect()} , Retrieve it via {@link #getExtension(Class)}
     */
    @Deprecated
    public void deselect() {
        mSelectExtension.deselect();
    }

    /**
     * select all items
     *
     * @deprecated deprecated in favor of {@link SelectExtension#select()} , Retrieve it via {@link #getExtension(Class)}
     */
    @Deprecated
    public void select() {
        mSelectExtension.select(false);
    }

    /**
     * select all items
     *
     * @param considerSelectableFlag true if the select method should not select an item if its not selectable
     * @deprecated deprecated in favor of {@link SelectExtension#select(boolean)} , Retrieve it via {@link #getExtension(Class)}
     */
    @Deprecated
    public void select(boolean considerSelectableFlag) {
        mSelectExtension.select(considerSelectableFlag);
    }

    /**
     * deselects all items at the positions in the iteratable
     *
     * @param positions the global positions to deselect
     * @deprecated deprecated in favor of {@link SelectExtension#deselect(Iterable)} , Retrieve it via {@link #getExtension(Class)}
     */
    @Deprecated
    public void deselect(Iterable<Integer> positions) {
        mSelectExtension.deselect(positions);
    }

    /**
     * deselects an item and removes its position in the selections list
     *
     * @param position the global position
     * @deprecated deprecated in favor of {@link SelectExtension#deselect(int)} , Retrieve it via {@link #getExtension(Class)}
     */
    @Deprecated
    public void deselect(int position) {
        mSelectExtension.deselect(position);
    }

    /**
     * deselects an item and removes its position in the selections list
     * also takes an iterator to remove items from the map
     *
     * @param position the global position
     * @param entries  the iterator which is used to deselect all
     * @deprecated deprecated in favor of {@link SelectExtension#deselect(int, Iterator)}, Retrieve it via {@link #getExtension(Class)}
     */
    @Deprecated
    public void deselect(int position, Iterator<Integer> entries) {
        mSelectExtension.deselect(position, entries);
    }

    /**
     * deletes all current selected items
     *
     * @return a list of the IItem elements which were deleted
     * @deprecated deprecated in favor of {@link SelectExtension#deleteAllSelectedItems()}, Retrieve it via {@link #getExtension(Class)}
     */
    @Deprecated
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
        for (IAdapterExtension<Item> ext : mExtensions.values()) {
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
        for (IAdapterExtension<Item> ext : mExtensions.values()) {
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
        for (IAdapterExtension<Item> ext : mExtensions.values()) {
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
        for (IAdapterExtension<Item> ext : mExtensions.values()) {
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
    public void notifyAdapterItemChanged(int position, @Nullable Object payload) {
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
    public void notifyAdapterItemRangeChanged(int position, int itemCount, @Nullable Object payload) {
        // handle our extensions
        for (IAdapterExtension<Item> ext : mExtensions.values()) {
            ext.notifyAdapterItemRangeChanged(position, itemCount, payload);
        }
        if (payload == null) {
            notifyItemRangeChanged(position, itemCount);
        } else {
            notifyItemRangeChanged(position, itemCount, payload);
        }
    }

    /**
     * convenient helper method to get the Item from a holder
     *
     * @param holder the ViewHolder for which we want to retrieve the item
     * @return the Item found for this ViewHolder
     */
    @SuppressWarnings("unchecked")
    public static <Item extends IItem> Item getHolderAdapterItem(@Nullable RecyclerView.ViewHolder holder) {
        if (holder != null) {
            Object tag = holder.itemView.getTag(com.mikepenz.fastadapter.R.id.fastadapter_item_adapter);
            if (tag instanceof FastAdapter) {
                FastAdapter fastAdapter = ((FastAdapter) tag);
                int pos = fastAdapter.getHolderAdapterPosition(holder);
                if (pos != RecyclerView.NO_POSITION) {
                    return (Item) fastAdapter.getItem(pos);
                }
            }
        }
        return null;
    }

    /**
     * convenient helper method to get the Item from a holder
     *
     * @param holder   the ViewHolder for which we want to retrieve the item
     * @param position the position for which we want to retrieve the item
     * @return the Item found for the given position and that ViewHolder
     */
    @SuppressWarnings("unchecked")
    public static <Item extends IItem> Item getHolderAdapterItem(@Nullable RecyclerView.ViewHolder holder, int position) {
        if (holder != null) {
            Object tag = holder.itemView.getTag(com.mikepenz.fastadapter.R.id.fastadapter_item_adapter);
            if (tag instanceof FastAdapter) {
                return (Item) ((FastAdapter) tag).getItem(position);
            }
        }
        return null;
    }

    /**
     * util function which recursively iterates over all items and subItems of the given adapter.
     * It executes the given `predicate` on every item and will either stop if that function returns true, or continue (if stopOnMatch is false)
     *
     * @param predicate   the predicate to run on every item, to check for a match or do some changes (e.g. select)
     * @param stopOnMatch defines if we should stop iterating after the first match
     * @return Triple&lt;Boolean, IItem, Integer&gt; The first value is true (it is always not null), the second contains the item and the third the position (if the item is visible) if we had a match, (always false and null and null in case of stopOnMatch == false)
     */
    @NonNull
    public Triple<Boolean, Item, Integer> recursive(AdapterPredicate<Item> predicate, boolean stopOnMatch) {
        return recursive(predicate, 0, stopOnMatch);
    }

    /**
     * util function which recursively iterates over all items and subItems of the given adapter.
     * It executes the given `predicate` on every item and will either stop if that function returns true, or continue (if stopOnMatch is false)
     *
     * @param predicate           the predicate to run on every item, to check for a match or do some changes (e.g. select)
     * @param globalStartPosition the start position at which we star tto recursively iterate over the items. (This will not stop at the end of a sub hierarchy!)
     * @param stopOnMatch         defines if we should stop iterating after the first match
     * @return Triple&lt;Boolean, IItem, Integer&gt; The first value is true (it is always not null), the second contains the item and the third the position (if the item is visible) if we had a match, (always false and null and null in case of stopOnMatch == false)
     */
    @NonNull
    public Triple<Boolean, Item, Integer> recursive(AdapterPredicate<Item> predicate, int globalStartPosition, boolean stopOnMatch) {
        for (int i = globalStartPosition; i < getItemCount(); i++) {
            //retrieve the item + it's adapter
            RelativeInfo<Item> relativeInfo = getRelativeInfo(i);
            Item item = relativeInfo.item;

            if (predicate.apply(relativeInfo.adapter, i, item, i) && stopOnMatch) {
                return new Triple<>(true, item, i);
            }

            if (item instanceof IExpandable) {
                Triple<Boolean, Item, Integer> res = FastAdapter.recursiveSub(relativeInfo.adapter, i, (IExpandable) item, predicate, stopOnMatch);
                if (res.first && stopOnMatch) {
                    return res;
                }
            }
        }

        return new Triple<>(false, null, null);
    }

    /**
     * Util function which recursively iterates over all items of a `IExpandable` parent if and only if it is `expanded` and has `subItems`
     * This is usually only used in
     *
     * @param lastParentAdapter  the last `IAdapter` managing the last (visible) parent item (that might also be a parent of a parent, ..)
     * @param lastParentPosition the global position of the last (visible) parent item, holding this sub item (that might also be a parent of a parent, ..)
     * @param parent             the `IExpandableParent` to start from
     * @param predicate          the predicate to run on every item, to check for a match or do some changes (e.g. select)
     * @param stopOnMatch        defines if we should stop iterating after the first match
     * @param <Item>             the type of the `Item`
     * @return Triple&lt;Boolean, IItem, Integer&gt; The first value is true (it is always not null), the second contains the item and the third the position (if the item is visible) if we had a match, (always false and null and null in case of stopOnMatch == false)
     */
    @SuppressWarnings("unchecked")
    public static <Item extends IItem> Triple<Boolean, Item, Integer> recursiveSub(IAdapter<Item> lastParentAdapter, int lastParentPosition, IExpandable parent, AdapterPredicate<Item> predicate, boolean stopOnMatch) {
        //in case it's expanded it can be selected via the normal way
        if (!parent.isExpanded() && parent.getSubItems() != null) {
            for (int ii = 0; ii < parent.getSubItems().size(); ii++) {
                Item sub = (Item) parent.getSubItems().get(ii);

                if (predicate.apply(lastParentAdapter, lastParentPosition, sub, -1) && stopOnMatch) {
                    return new Triple<>(true, sub, null);
                }

                if (sub instanceof IExpandable) {
                    Triple<Boolean, Item, Integer> res = FastAdapter.recursiveSub(lastParentAdapter, lastParentPosition, (IExpandable) sub, predicate, stopOnMatch);
                    if (res.first) {
                        return res;
                    }
                }
            }
        }
        return new Triple<>(false, null, null);
    }

    /**
     * an internal class to return the IItem and relativePosition and its adapter at once. used to save one iteration inside the getInternalItem method
     */
    public static class RelativeInfo<Item extends IItem> {
        public IAdapter<Item> adapter = null;
        public Item item = null;
        public int position = -1;
    }

    /**
     * A ViewHolder provided from the FastAdapter to allow handling the important event's within the ViewHolder
     * instead of the item
     *
     * @param <Item>
     */
    public static abstract class ViewHolder<Item extends IItem> extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }

        /**
         * binds the data of this item onto the viewHolder
         */
        public abstract void bindView(Item item, List<Object> payloads);

        /**
         * View needs to release resources when its recycled
         */
        public abstract void unbindView(Item item);

        /**
         * View got attached to the window
         */
        public void attachToWindow(Item item) {
        }

        /**
         * View got detached from the window
         */
        public void detachFromWindow(Item item) {
        }

        /**
         * View is in a transient state and could not be recycled
         *
         * @return return true if you want to recycle anyways (after clearing animations or so)
         */
        public boolean failedToRecycle(Item item) {
            return false;
        }
    }
}
