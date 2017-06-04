package com.mikepenz.fastadapter_extensions.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.listeners.OnLongClickListener;
import com.mikepenz.fastadapter.listeners.OnTouchListener;

import java.util.List;

/**
 * Created by fabianterhorst on 04.07.16.
 */

public class FastAdapterBottomSheetDialog<Item extends IItem> extends BottomSheetDialog {

    private RecyclerView mRecyclerView;

    private FastAdapter<Item> mFastAdapter;
    private ItemAdapter<Item> mItemAdapter;

    public FastAdapterBottomSheetDialog(Context context) {
        super(context);
        this.mRecyclerView = createRecyclerView();
    }

    public FastAdapterBottomSheetDialog(Context context, int theme) {
        super(context, theme);
        this.mRecyclerView = createRecyclerView();
    }

    /**
     * Create the RecyclerView and set it as the dialog view.
     *
     * @return the created RecyclerView
     */
    private RecyclerView createRecyclerView() {
        RecyclerView recyclerView = new RecyclerView(getContext());
        RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        recyclerView.setLayoutParams(params);
        setContentView(recyclerView);
        return recyclerView;
    }

    public FastAdapterBottomSheetDialog<Item> withFastItemAdapter(@NonNull FastAdapter<Item> fastAdapter, @NonNull ItemAdapter<Item> itemAdapter) {
        this.mFastAdapter = fastAdapter;
        this.mItemAdapter = itemAdapter;
        mRecyclerView.setAdapter(mFastAdapter);
        return this;
    }

    private void initAdapterIfNeeded() {
        if (mFastAdapter == null || mRecyclerView.getAdapter() == null) {
            mItemAdapter = ItemAdapter.items();
            mFastAdapter = FastAdapter.with(mItemAdapter);
            mRecyclerView.setAdapter(mFastAdapter);
        }
    }

    public FastAdapterBottomSheetDialog<Item> withItems(@NonNull List<Item> items) {
        initAdapterIfNeeded();
        mItemAdapter.set(items);
        return this;
    }

    public FastAdapterBottomSheetDialog<Item> withItems(@NonNull Item... items) {
        initAdapterIfNeeded();
        mItemAdapter.add(items);
        return this;
    }

    public FastAdapterBottomSheetDialog<Item> withAdapter(FastAdapter<Item> adapter) {
        this.mRecyclerView.setAdapter(adapter);
        return this;
    }

    /**
     * Set the {@link RecyclerView.LayoutManager} that the RecyclerView will use.
     *
     * @param layoutManager LayoutManager to use
     */
    public FastAdapterBottomSheetDialog<Item> withLayoutManager(RecyclerView.LayoutManager layoutManager) {
        this.mRecyclerView.setLayoutManager(layoutManager);
        return this;
    }

    /**
     * Add a listener that will be notified of any changes in scroll state or position of the
     * RecyclerView.
     *
     * @param listener listener to set or null to clear
     */
    public FastAdapterBottomSheetDialog<Item> withOnScrollListener(RecyclerView.OnScrollListener listener) {
        mRecyclerView.addOnScrollListener(listener);
        return this;
    }

    /**
     * Start the dialog and display it on screen.  The window is placed in the
     * application layer and opaque.  Note that you should not override this
     * method to do initialization when the dialog is shown, instead implement
     * that in {@link #onStart}.
     */
    public void show() {
        if (mRecyclerView.getLayoutManager() == null) {
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        }
        initAdapterIfNeeded();
        super.show();
    }

    @NonNull
    public RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    /**
     * Define the OnClickListener which will be used for a single item
     *
     * @param onClickListener the OnClickListener which will be used for a single item
     * @return this
     */
    public FastAdapterBottomSheetDialog<Item> withOnClickListener(com.mikepenz.fastadapter.listeners.OnClickListener<Item> onClickListener) {
        this.mFastAdapter.withOnClickListener(onClickListener);
        return this;
    }

    /**
     * Define the OnPreClickListener which will be used for a single item and is called after all internal methods are done
     *
     * @param onPreClickListener the OnPreClickListener which will be called after a single item was clicked and all internal methods are done
     * @return this
     */
    public FastAdapterBottomSheetDialog<Item> withOnPreClickListener(com.mikepenz.fastadapter.listeners.OnClickListener<Item> onPreClickListener) {
        this.mFastAdapter.withOnPreClickListener(onPreClickListener);
        return this;
    }

    /**
     * Define the OnLongClickListener which will be used for a single item
     *
     * @param onLongClickListener the OnLongClickListener which will be used for a single item
     * @return this
     */
    public FastAdapterBottomSheetDialog<Item> withOnLongClickListener(OnLongClickListener<Item> onLongClickListener) {
        this.mFastAdapter.withOnLongClickListener(onLongClickListener);
        return this;
    }

    /**
     * Define the OnLongClickListener which will be used for a single item and is called after all internal methods are done
     *
     * @param onPreLongClickListener the OnLongClickListener which will be called after a single item was clicked and all internal methods are done
     * @return this
     */
    public FastAdapterBottomSheetDialog<Item> withOnPreLongClickListener(OnLongClickListener<Item> onPreLongClickListener) {
        this.mFastAdapter.withOnPreLongClickListener(onPreLongClickListener);
        return this;
    }

    /**
     * Define the TouchListener which will be used for a single item
     *
     * @param onTouchListener the TouchListener which will be used for a single item
     * @return this
     */
    public FastAdapterBottomSheetDialog<Item> withOnTouchListener(OnTouchListener<Item> onTouchListener) {
        this.mFastAdapter.withOnTouchListener(onTouchListener);
        return this;
    }

    /**
     * set a new list of items and apply it to the existing list (clear - add) for this adapter
     *
     * @param items the new items to set
     */
    public FastAdapterBottomSheetDialog<Item> set(List<Item> items) {
        mItemAdapter.set(items);
        return this;
    }

    /**
     * sets a complete new list of items onto this adapter, using the new list. Calls notifyDataSetChanged
     *
     * @param items the new items to set
     */
    public FastAdapterBottomSheetDialog<Item> setNewList(List<Item> items) {
        mItemAdapter.setNewList(items);
        return this;
    }

    /**
     * add an array of items to the end of the existing items
     *
     * @param items the items to add
     */
    @SafeVarargs
    public final FastAdapterBottomSheetDialog<Item> add(Item... items) {
        mItemAdapter.add(items);
        return this;
    }

    /**
     * add a list of items to the end of the existing items
     *
     * @param items the items to add
     */
    public FastAdapterBottomSheetDialog<Item> add(List<Item> items) {
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
    public final FastAdapterBottomSheetDialog<Item> add(int position, Item... items) {
        mItemAdapter.add(position, items);
        return this;
    }

    /**
     * add a list of items at the given position within the existing items
     *
     * @param position the global position
     * @param items    the items to add
     */
    public FastAdapterBottomSheetDialog<Item> add(int position, List<Item> items) {
        mItemAdapter.add(position, items);
        return this;
    }

    /**
     * sets an item at the given position, overwriting the previous item
     *
     * @param position the global position
     * @param item     the item to set
     */
    public FastAdapterBottomSheetDialog<Item> set(int position, Item item) {
        mItemAdapter.set(position, item);
        return this;
    }

    /**
     * add an item at the end of the existing items
     *
     * @param item the item to add
     */
    public FastAdapterBottomSheetDialog<Item> add(Item item) {
        mItemAdapter.add(item);
        return this;
    }

    /**
     * add an item at the given position within the existing icons
     *
     * @param position the global position
     * @param item     the item to add
     */
    public FastAdapterBottomSheetDialog<Item> add(int position, Item item) {
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
    public FastAdapterBottomSheetDialog<Item> move(int fromPosition, int toPosition) {
        mItemAdapter.move(fromPosition, toPosition);
        return this;
    }

    /**
     * removes an item at the given position within the existing icons
     *
     * @param position the global position
     */
    public FastAdapterBottomSheetDialog<Item> remove(int position) {
        mItemAdapter.remove(position);
        return this;
    }

    /**
     * removes a range of items starting with the given position within the existing icons
     *
     * @param position  the global position
     * @param itemCount the count of items removed
     */
    public FastAdapterBottomSheetDialog<Item> removeItemRange(int position, int itemCount) {
        mItemAdapter.removeRange(position, itemCount);
        return this;
    }

    /**
     * removes all items of this adapter
     */
    public FastAdapterBottomSheetDialog<Item> clear() {
        mItemAdapter.clear();
        return this;
    }
}