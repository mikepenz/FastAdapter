package com.mikepenz.fastadapter_extensions.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.mikepenz.fastadapter.AbstractAdapter;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;

import java.util.List;

public class FastAdapterDialog<Item extends IItem> extends AlertDialog {

    private RecyclerView mRecyclerView;

    private FastItemAdapter<Item> mFastItemAdapter;

    public FastAdapterDialog(Context context) {
        super(context);
        init();
    }

    public FastAdapterDialog(Context context, int theme) {
        super(context, theme);
        init();
    }

    public FastItemAdapter<Item> getFastItemAdapter() {
        return mFastItemAdapter;
    }

    public ItemAdapter<Item> items() {
        return mFastItemAdapter.items();
    }

    /**
     * Create the RecyclerView and set it as the dialog view.
     *
     */
    private void init() {
        mRecyclerView = new RecyclerView(getContext());
        RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        mRecyclerView.setLayoutParams(params);
        setView(mRecyclerView);
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

    public FastAdapterDialog<Item> withFastItemAdapter(@NonNull FastItemAdapter<Item> fastItemAdapter) {
        this.mFastItemAdapter = fastItemAdapter;
        mRecyclerView.setAdapter(mFastItemAdapter);
        return this;
    }

    public FastAdapterDialog<Item> withItems(@NonNull List<Item> items) {
        if (mFastItemAdapter == null) {
            mFastItemAdapter = new FastItemAdapter<>();
            mRecyclerView.setAdapter(mFastItemAdapter);
        }
        mFastItemAdapter.items().set(items);
        return this;
    }

    @SafeVarargs
    public final FastAdapterDialog<Item> withItems(@NonNull Item... items) {
        if (mFastItemAdapter == null) {
            mFastItemAdapter = new FastItemAdapter<>();
            mRecyclerView.setAdapter(mFastItemAdapter);
        }
        mFastItemAdapter.items().add(items);
        return this;
    }

    public FastAdapterDialog<Item> withAdapter(AbstractAdapter<Item> adapter) {
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
        if (mFastItemAdapter == null && mRecyclerView.getAdapter() == null) {
            mFastItemAdapter = new FastItemAdapter<>();
            mRecyclerView.setAdapter(mFastItemAdapter);
        }
        super.show();
    }

    @NonNull
    public RecyclerView getRecyclerView() {
        return mRecyclerView;
    }
}
