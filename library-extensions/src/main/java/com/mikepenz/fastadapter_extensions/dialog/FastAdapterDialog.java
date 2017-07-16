package com.mikepenz.fastadapter_extensions.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.listeners.OnLongClickListener;
import com.mikepenz.fastadapter.listeners.OnTouchListener;

import java.util.List;

public class FastAdapterDialog<Item extends IItem> extends AlertDialog {

    private RecyclerView mRecyclerView;

    private FastAdapter<Item> mFastAdapter;
    private ItemAdapter<Item> mItemAdapter;

    public FastAdapterDialog(Context context) {
        super(context);
        this.mRecyclerView = createRecyclerView();
    }

    public FastAdapterDialog(Context context, int theme) {
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
        setView(recyclerView);
        return recyclerView;
    }

    /**
     * Set the title text for this dialog's window.
     *
     * @param title The text to display in the title.
     */
    public FastAdapterDialog<Item> withTitle(String title) {
        setTitle(title);
        return this;
    }

    /**
     * Set the title text for this dialog's window.
     *
     * @param titleRes The resource id of the text to display in the title.
     */
    public FastAdapterDialog<Item> withTitle(@StringRes int titleRes) {
        setTitle(titleRes);
        return this;
    }

    public FastAdapterDialog<Item> withFastItemAdapter(@NonNull FastAdapter<Item> fastAdapter, @NonNull ItemAdapter<Item> itemAdapter) {
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

    public FastAdapterDialog<Item> withItems(@NonNull List<Item> items) {
        initAdapterIfNeeded();
        mItemAdapter.set(items);
        return this;
    }

    public FastAdapterDialog<Item> withItems(@NonNull Item... items) {
        initAdapterIfNeeded();
        mItemAdapter.add(items);
        return this;
    }

    public FastAdapterDialog<Item> withAdapter(FastAdapter<Item> adapter) {
        this.mRecyclerView.setAdapter(adapter);
        return this;
    }

    /**
     * Set the {@link RecyclerView.LayoutManager} that the RecyclerView will use.
     *
     * @param layoutManager LayoutManager to use
     */
    public FastAdapterDialog<Item> withLayoutManager(RecyclerView.LayoutManager layoutManager) {
        this.mRecyclerView.setLayoutManager(layoutManager);
        return this;
    }

    /**
     * Add a listener that will be notified of any changes in scroll state or position of the
     * RecyclerView.
     *
     * @param listener listener to set or null to clear
     */
    public FastAdapterDialog<Item> withOnScrollListener(RecyclerView.OnScrollListener listener) {
        mRecyclerView.addOnScrollListener(listener);
        return this;
    }

    /**
     * Set a listener to be invoked when the positive button of the dialog is pressed.
     *
     * @param text     The text to display in the positive button
     * @param listener The {@link DialogInterface.OnClickListener} to use.
     * @return This Builder object to allow for chaining of calls to set methods
     */
    public FastAdapterDialog<Item> withPositiveButton(String text, OnClickListener listener) {
        return withButton(BUTTON_POSITIVE, text, listener);
    }

    /**
     * Set a listener to be invoked when the positive button of the dialog is pressed.
     *
     * @param textRes  The resource id of the text to display in the positive button
     * @param listener The {@link DialogInterface.OnClickListener} to use.
     * @return This Builder object to allow for chaining of calls to set methods
     */
    public FastAdapterDialog<Item> withPositiveButton(@StringRes int textRes, OnClickListener listener) {
        return withButton(BUTTON_POSITIVE, textRes, listener);
    }

    /**
     * Set a listener to be invoked when the negative button of the dialog is pressed.
     *
     * @param text     The text to display in the negative button
     * @param listener The {@link DialogInterface.OnClickListener} to use.
     * @return This Builder object to allow for chaining of calls to set methods
     */
    public FastAdapterDialog<Item> withNegativeButton(String text, OnClickListener listener) {
        return withButton(BUTTON_NEGATIVE, text, listener);
    }

    /**
     * Set a listener to be invoked when the negative button of the dialog is pressed.
     *
     * @param textRes  The resource id of the text to display in the negative button
     * @param listener The {@link DialogInterface.OnClickListener} to use.
     * @return This Builder object to allow for chaining of calls to set methods
     */
    public FastAdapterDialog<Item> withNegativeButton(@StringRes int textRes, OnClickListener listener) {
        return withButton(BUTTON_NEGATIVE, textRes, listener);
    }

    /**
     * Adds a negative button to the dialog. The button click will close the dialog.
     *
     * @param textRes The resource id of the text to display in the negative button
     * @return This Builder object to allow for chaining of calls to set methods
     */
    public FastAdapterDialog<Item> withNegativeButton(@StringRes int textRes) {
        return withButton(BUTTON_NEGATIVE, textRes, null);
    }

    /**
     * Adds a negative button to the dialog. The button click will close the dialog.
     *
     * @param text The text to display in the negative button
     * @return This Builder object to allow for chaining of calls to set methods
     */
    public FastAdapterDialog<Item> withNegativeButton(String text) {
        return withButton(BUTTON_NEGATIVE, text, null);
    }

    /**
     * Set a listener to be invoked when the neutral button of the dialog is pressed.
     *
     * @param text     The text to display in the neutral button
     * @param listener The {@link DialogInterface.OnClickListener} to use.
     * @return This Builder object to allow for chaining of calls to set methods
     */
    public FastAdapterDialog<Item> withNeutralButton(String text, OnClickListener listener) {
        return withButton(BUTTON_NEUTRAL, text, listener);
    }

    /**
     * Set a listener to be invoked when the neutral button of the dialog is pressed.
     *
     * @param textRes  The resource id of the text to display in the neutral button
     * @param listener The {@link DialogInterface.OnClickListener} to use.
     * @return This Builder object to allow for chaining of calls to set methods
     */
    public FastAdapterDialog<Item> withNeutralButton(@StringRes int textRes, OnClickListener listener) {
        return withButton(BUTTON_NEUTRAL, textRes, listener);
    }

    /**
     * Sets a listener to be invoked when the positive button of the dialog is pressed. This method
     * has no effect if called after {@link #show()}.
     *
     * @param whichButton Which button to set the listener on, can be one of
     *                    {@link DialogInterface#BUTTON_POSITIVE},
     *                    {@link DialogInterface#BUTTON_NEGATIVE}, or
     *                    {@link DialogInterface#BUTTON_NEUTRAL}
     * @param text        The text to display in positive button.
     * @param listener    The {@link DialogInterface.OnClickListener} to use.
     */
    public FastAdapterDialog<Item> withButton(int whichButton, String text, OnClickListener listener) {
        setButton(whichButton, text, listener);
        return this;
    }

    /**
     * Sets a listener to be invoked when the positive button of the dialog is pressed. This method
     * has no effect if called after {@link #show()}.
     *
     * @param whichButton Which button to set the listener on, can be one of
     *                    {@link DialogInterface#BUTTON_POSITIVE},
     *                    {@link DialogInterface#BUTTON_NEGATIVE}, or
     *                    {@link DialogInterface#BUTTON_NEUTRAL}
     * @param textRes     The text to display in positive button.
     * @param listener    The {@link DialogInterface.OnClickListener} to use.
     */
    public FastAdapterDialog<Item> withButton(int whichButton, @StringRes int textRes, OnClickListener listener) {
        setButton(whichButton, getContext().getString(textRes), listener);
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
    public FastAdapterDialog<Item> withOnClickListener(com.mikepenz.fastadapter.listeners.OnClickListener<Item> onClickListener) {
        this.mFastAdapter.withOnClickListener(onClickListener);
        return this;
    }

    /**
     * Define the OnPreClickListener which will be used for a single item and is called after all internal methods are done
     *
     * @param onPreClickListener the OnPreClickListener which will be called after a single item was clicked and all internal methods are done
     * @return this
     */
    public FastAdapterDialog<Item> withOnPreClickListener(com.mikepenz.fastadapter.listeners.OnClickListener<Item> onPreClickListener) {
        this.mFastAdapter.withOnPreClickListener(onPreClickListener);
        return this;
    }

    /**
     * Define the OnLongClickListener which will be used for a single item
     *
     * @param onLongClickListener the OnLongClickListener which will be used for a single item
     * @return this
     */
    public FastAdapterDialog<Item> withOnLongClickListener(OnLongClickListener<Item> onLongClickListener) {
        this.mFastAdapter.withOnLongClickListener(onLongClickListener);
        return this;
    }

    /**
     * Define the OnLongClickListener which will be used for a single item and is called after all internal methods are done
     *
     * @param onPreLongClickListener the OnLongClickListener which will be called after a single item was clicked and all internal methods are done
     * @return this
     */
    public FastAdapterDialog<Item> withOnPreLongClickListener(OnLongClickListener<Item> onPreLongClickListener) {
        this.mFastAdapter.withOnPreLongClickListener(onPreLongClickListener);
        return this;
    }

    /**
     * Define the TouchListener which will be used for a single item
     *
     * @param onTouchListener the TouchListener which will be used for a single item
     * @return this
     */
    public FastAdapterDialog<Item> withOnTouchListener(OnTouchListener<Item> onTouchListener) {
        this.mFastAdapter.withOnTouchListener(onTouchListener);
        return this;
    }

    /**
     * set a new list of items and apply it to the existing list (clear - add) for this adapter
     *
     * @param items the new items to set
     */
    public FastAdapterDialog<Item> set(List<Item> items) {
        mItemAdapter.set(items);
        return this;
    }

    /**
     * sets a complete new list of items onto this adapter, using the new list. Calls notifyDataSetChanged
     *
     * @param items the new items to set
     */
    public FastAdapterDialog<Item> setNewList(List<Item> items) {
        mItemAdapter.setNewList(items);
        return this;
    }

    /**
     * add an array of items to the end of the existing items
     *
     * @param items the items to add
     */
    @SafeVarargs
    public final FastAdapterDialog<Item> add(Item... items) {
        mItemAdapter.add(items);
        return this;
    }

    /**
     * add a list of items to the end of the existing items
     *
     * @param items the items to add
     */
    public FastAdapterDialog<Item> add(List<Item> items) {
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
    public final FastAdapterDialog<Item> add(int position, Item... items) {
        mItemAdapter.add(position, items);
        return this;
    }

    /**
     * add a list of items at the given position within the existing items
     *
     * @param position the global position
     * @param items    the items to add
     */
    public FastAdapterDialog<Item> add(int position, List<Item> items) {
        mItemAdapter.add(position, items);
        return this;
    }

    /**
     * sets an item at the given position, overwriting the previous item
     *
     * @param position the global position
     * @param item     the item to set
     */
    public FastAdapterDialog<Item> set(int position, Item item) {
        mItemAdapter.set(position, item);
        return this;
    }

    /**
     * add an item at the end of the existing items
     *
     * @param item the item to add
     */
    public FastAdapterDialog<Item> add(Item item) {
        mItemAdapter.add(item);
        return this;
    }

    /**
     * add an item at the given position within the existing icons
     *
     * @param position the global position
     * @param item     the item to add
     */
    public FastAdapterDialog<Item> add(int position, Item item) {
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
    public FastAdapterDialog<Item> move(int fromPosition, int toPosition) {
        mItemAdapter.move(fromPosition, toPosition);
        return this;
    }

    /**
     * removes an item at the given position within the existing icons
     *
     * @param position the global position
     */
    public FastAdapterDialog<Item> remove(int position) {
        mItemAdapter.remove(position);
        return this;
    }

    /**
     * removes a range of items starting with the given position within the existing icons
     *
     * @param position  the global position
     * @param itemCount the count of items removed
     */
    public FastAdapterDialog<Item> removeItemRange(int position, int itemCount) {
        mItemAdapter.removeRange(position, itemCount);
        return this;
    }

    /**
     * removes all items of this adapter
     */
    public FastAdapterDialog<Item> clear() {
        mItemAdapter.clear();
        return this;
    }
}
