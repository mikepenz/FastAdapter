package com.mikepenz.fastadapter_extensions.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.mikepenz.fastadapter.AbstractAdapter;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;

import java.util.List;

/**
 * Created by fabianterhorst on 04.07.16.
 */

public class FastAdapterBottomSheetDialog<Item extends IItem> extends BottomSheetDialog {

    private RecyclerView mRecyclerView;

    private FastItemAdapter<Item> mFastItemAdapter;

    public FastAdapterBottomSheetDialog(Context context) {
        super(context);
        init();
    }

    public FastAdapterBottomSheetDialog(Context context, int theme) {
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
        setContentView(mRecyclerView);
    }

    /**
     * Set the title text for this dialog's window.
     *
     * @param title The text to display in the title.
     */
    public FastAdapterBottomSheetDialog<Item> withTitle(String title) {
        setTitle(title);
        return this;
    }

    /**
     * Set the title text for this dialog's window.
     *
     * @param titleRes The resource id of the text to display in the title.
     */
    public FastAdapterBottomSheetDialog<Item> withTitle(@StringRes int titleRes) {
        setTitle(titleRes);
        return this;
    }

    public FastAdapterBottomSheetDialog<Item> withFastItemAdapter(@NonNull FastItemAdapter<Item> fastItemAdapter) {
        this.mFastItemAdapter = fastItemAdapter;
        mRecyclerView.setAdapter(mFastItemAdapter);
        return this;
    }

    public FastAdapterBottomSheetDialog<Item> withItems(@NonNull List<Item> items) {
        if (mFastItemAdapter == null) {
            mFastItemAdapter = new FastItemAdapter<>();
            mRecyclerView.setAdapter(mFastItemAdapter);
        }
        mFastItemAdapter.items().set(items);
        return this;
    }

    @SafeVarargs
    public final FastAdapterBottomSheetDialog<Item> withItems(@NonNull Item... items) {
        if (mFastItemAdapter == null) {
            mFastItemAdapter = new FastItemAdapter<>();
            mRecyclerView.setAdapter(mFastItemAdapter);
        }
        mFastItemAdapter.items().add(items);
        return this;
    }

    public FastAdapterBottomSheetDialog<Item> withAdapter(AbstractAdapter<Item> adapter) {
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