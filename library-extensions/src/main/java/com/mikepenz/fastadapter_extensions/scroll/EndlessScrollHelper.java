package com.mikepenz.fastadapter_extensions.scroll;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.view.View;

import com.mikepenz.fastadapter.IInterceptor;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.IItemAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.adapters.ModelAdapter;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import static android.support.v7.widget.com_mikepenz_fastadapter_extensions_scroll.postOnRecyclerView;

/**
 * This is an extension of {@link EndlessRecyclerOnScrollListener}, providing a more powerful API
 * for endless scrolling.
 * This class exposes 2 callbacks to separate the loading logic from delivering the results:
 * <ul>
 * <li>{@link OnLoadMoreHandler OnLoadMoreHandler}</li>
 * <li>{@link OnNewItemsListener OnNewItemsListener}</li>
 * </ul>
 * This class also takes care of other various stuffs like:
 * <ul>
 * <li>Ensuring the results are delivered on the RecyclerView's handler &ndash; which also ensures
 * that the results are delivered only when the RecyclerView is attached to the window, see {@link
 * View#post(Runnable)}.</li>
 * <li>Prevention of memory leaks when implemented properly (i.e. {@link OnLoadMoreHandler
 * OnLoadMoreHandler} should be implemented via static classes or lambda expressions).</li>
 * <li>An easier way to deliver results to an {@link #withNewItemsDeliveredTo(IItemAdapter,
 * IInterceptor)  IItemAdapter} or {@link #withNewItemsDeliveredTo(ModelAdapter)
 * ModelAdapter}.</li>
 * </ul>
 * Created by jayson on 3/26/2016.
 */
public class EndlessScrollHelper<Model> extends EndlessRecyclerOnScrollListener {
    private OnLoadMoreHandler<Model> mOnLoadMoreHandler;
    private OnNewItemsListener<Model> mOnNewItemsListener;

    public EndlessScrollHelper() {
    }

    public EndlessScrollHelper(LayoutManager layoutManager) {
        super(layoutManager);
    }

    public EndlessScrollHelper(LayoutManager layoutManager, int visibleThreshold) {
        super(layoutManager, visibleThreshold);
    }

    /**
     * @param layoutManager
     * @param visibleThreshold
     * @param footerAdapter    the itemAdapter used to host Footer items
     */
    public EndlessScrollHelper(LayoutManager layoutManager, int visibleThreshold, ItemAdapter footerAdapter) {
        super(layoutManager, visibleThreshold, footerAdapter);
    }

    public EndlessScrollHelper<Model> addTo(RecyclerView recyclerView) {
        recyclerView.addOnScrollListener(this);
        return this;
    }

    /**
     * A callback interface provided by the {@link EndlessScrollHelper} where
     * {@link #onLoadMore(ResultReceiver, int) onLoadMore()} results are to be delivered.
     * The underlying implementation is safe to use by any background-thread, as long as only 1
     * thread is using it. Results delivered via {@link #deliverNewItems(List)} are automatically
     * dispatched to the RecyclerView's message queue (i.e. to be delivered in the ui thread).
     *
     * @param <Model>
     */
    public interface ResultReceiver<Model> {

        /**
         * @return the current page where the results will be delivered.
         */
        int getReceiverPage();

        /**
         * Delivers the result of an {@link #onLoadMore(ResultReceiver, int) onLoadMore()} for the
         * current {@linkplain #getReceiverPage() page}. This method must be called only once.
         *
         * @param result the result of an {@link #onLoadMore(ResultReceiver, int) onLoadMore()}
         * @return whether results where delivered successfully or not, possibly because the
         * RecyclerView is no longer attached or the {@link EndlessScrollHelper} is no longer
         * in use (and it has been garbage collected).
         * @throws IllegalStateException when more than one results are delivered.
         */
        boolean deliverNewItems(@NonNull List<Model> result);
    }

    public interface OnLoadMoreHandler<Model> {

        /**
         * Handles loading of the specified page and delivers the results to the specified
         * {@link ResultReceiver}.
         *
         * @param out
         * @param currentPage
         */
        void onLoadMore(@NonNull ResultReceiver<Model> out, int currentPage);
    }

    public interface OnNewItemsListener<Model> {

        /**
         * Called on the RecyclerView's message queue to receive the results of a previous
         * {@link #onLoadMore(ResultReceiver, int) onLoadMore()}.
         *
         * @param newItems
         * @param page
         */
        void onNewItems(@NonNull List<Model> newItems, int page);
    }

    /**
     * Define the {@link OnLoadMoreHandler OnLoadMoreHandler} which will be used for loading new
     * items.
     *
     * @param onLoadMoreHandler
     * @return
     */
    public EndlessScrollHelper<Model> withOnLoadMoreHandler(@NonNull OnLoadMoreHandler<Model> onLoadMoreHandler) {
        mOnLoadMoreHandler = onLoadMoreHandler;
        return this;
    }

    /**
     * Define the {@link OnNewItemsListener OnNewItemsListener} which will receive the new items
     * loaded by {@link #onLoadMore(ResultReceiver, int) onLoadMore()}.
     *
     * @param onNewItemsListener
     * @return
     * @see #withNewItemsDeliveredTo(IItemAdapter, IInterceptor) withNewItemsDeliveredTo(IItemAdapter, IInterceptor)
     * @see #withNewItemsDeliveredTo(ModelAdapter) withNewItemsDeliveredTo(ModelAdapter)
     */
    public EndlessScrollHelper<Model> withOnNewItemsListener(@NonNull OnNewItemsListener<Model> onNewItemsListener) {
        mOnNewItemsListener = onNewItemsListener;
        return this;
    }

    /**
     * Registers an {@link OnNewItemsListener OnNewItemsListener} that delivers results to the
     * specified {@link IItemAdapter}. Converting each result to an {@link IItem} using the given
     * {@code itemFactory}.
     *
     * @param itemAdapter
     * @param itemFactory
     * @param <Item>
     * @return
     * @see #withNewItemsDeliveredTo(IItemAdapter, IInterceptor, OnNewItemsListener) withNewItemsDeliveredTo(IItemAdapter, IInterceptor, OnNewItemsListener)
     */
    public <Item extends IItem> EndlessScrollHelper<Model> withNewItemsDeliveredTo(@NonNull IItemAdapter<?, Item> itemAdapter, @NonNull IInterceptor<Model, Item> itemFactory) {
        mOnNewItemsListener = new DeliverToIItemAdapter<>(itemAdapter, itemFactory);
        return this;
    }

    /**
     * Registers an {@link OnNewItemsListener OnNewItemsListener} that delivers results to the
     * specified {@link ModelAdapter} through its {@link ModelAdapter#add} method.
     *
     * @param modelItemAdapter
     * @return
     * @see #withNewItemsDeliveredTo(ModelAdapter, OnNewItemsListener) withNewItemsDeliveredTo(ModelAdapter, OnNewItemsListener)
     */
    public EndlessScrollHelper<Model> withNewItemsDeliveredTo(@NonNull ModelAdapter<Model, ?> modelItemAdapter) {
        mOnNewItemsListener = new DeliverToModelAdapter<>(modelItemAdapter);
        return this;
    }

    /**
     * An overload of {@link #withNewItemsDeliveredTo(IItemAdapter, IInterceptor) withNewItemsDeliveredTo()}
     * that allows additional callbacks.
     *
     * @param itemAdapter
     * @param itemFactory
     * @param extraOnNewItemsListener
     * @param <Item>
     * @return
     */
    public <Item extends IItem> EndlessScrollHelper<Model> withNewItemsDeliveredTo(@NonNull IItemAdapter<?, Item> itemAdapter, @NonNull IInterceptor<Model, Item> itemFactory, @NonNull OnNewItemsListener<Model> extraOnNewItemsListener) {
        mOnNewItemsListener = new DeliverToIItemAdapter2<>(itemAdapter, itemFactory, extraOnNewItemsListener);
        return this;
    }

    /**
     * An overload of {@link #withNewItemsDeliveredTo(ModelAdapter) withNewItemsDeliveredTo()}
     * that allows additional callbacks.
     *
     * @param modelItemAdapter
     * @param extraOnNewItemsListener
     * @return
     */
    public EndlessScrollHelper<Model> withNewItemsDeliveredTo(@NonNull ModelAdapter<Model, ?> modelItemAdapter, @NonNull OnNewItemsListener<Model> extraOnNewItemsListener) {
        mOnNewItemsListener = new DeliverToModelAdapter2<>(modelItemAdapter, extraOnNewItemsListener);
        return this;
    }

    //-------------------------
    //-------------------------
    //Override-able methods
    //-------------------------
    //-------------------------

    /**
     * The default implementation takes care of calling the previously set
     * {@link OnLoadMoreHandler OnLoadMoreHandler}.
     *
     * @param out
     * @param currentPage
     * @see #withOnLoadMoreHandler(OnLoadMoreHandler) withOnLoadMoreHandler(OnLoadMoreHandler)
     */
    protected void onLoadMore(@NonNull ResultReceiver<Model> out, int currentPage) {
        OnLoadMoreHandler<Model> loadMoreHandler = this.mOnLoadMoreHandler;
        try {
            loadMoreHandler.onLoadMore(out, currentPage);
        } catch (NullPointerException npe) {
            // Lazy null checking! If this was our npe, then throw with an appropriate message.
            throw loadMoreHandler != null ? npe
                    : new NullPointerException("You must provide an `OnLoadMoreHandler`");
        }
    }

    /**
     * The default implementation takes care of calling the previously set
     * {@link OnNewItemsListener OnNewItemsListener}.
     *
     * @param newItems
     * @param page
     * @see #withOnNewItemsListener(OnNewItemsListener) withOnNewItemsListener(OnNewItemsListener)
     */
    protected void onNewItems(@NonNull List<Model> newItems, int page) {
        OnNewItemsListener<Model> onNewItemsListener = this.mOnNewItemsListener;
        try {
            onNewItemsListener.onNewItems(newItems, page);
        } catch (NullPointerException npe) {
            // Lazy null checking! If this was our npe, then throw with an appropriate message.
            throw onNewItemsListener != null ? npe
                    : new NullPointerException("You must provide an `OnNewItemsListener`");
        }
    }

    //-------------------------
    //-------------------------
    //Internal stuff
    //-------------------------
    //-------------------------

    @Override
    public void onLoadMore(int currentPage) {
        onLoadMore(new ResultReceiverImpl<>(this, currentPage), currentPage);
    }

    private static final class ResultReceiverImpl<Model> extends WeakReference<EndlessScrollHelper<Model>> implements ResultReceiver<Model>, Runnable {
        private final int mReceiverPage;
        private EndlessScrollHelper<Model> mHelperStrongRef;
        private List<Model> mResult;

        ResultReceiverImpl(EndlessScrollHelper<Model> helper, int receiverPage) {
            super(helper); // We use WeakReferences to outer class to avoid memory leaks.
            mReceiverPage = receiverPage;
        }

        @Override
        public int getReceiverPage() {
            return mReceiverPage;
        }

        @Override
        public boolean deliverNewItems(@NonNull List<Model> result) {
            if (mResult != null) // We might also see `null` here if more than 1 thread is modifying this.
                throw new IllegalStateException("`result` already provided!");
            mResult = result;
            mHelperStrongRef = super.get();
            return mHelperStrongRef != null
                    && postOnRecyclerView(mHelperStrongRef.getLayoutManager(), this);
        }

        @Override
        public void run() {
            // At this point, mHelperStrongRef != null
            try {
                if (mHelperStrongRef.getCurrentPage() != mReceiverPage) {
//                    throw new IllegalStateException("Inconsistent state! "
//                            + "Page might have already been loaded! "
//                            + "Or `loadMore(result)` might have been used by more than 1 thread!");
                    return; // Let it fail and possibly load correctly
                }
            } catch (NullPointerException npe) {
                if (mHelperStrongRef == null) {
                    throw new AssertionError(npe);
                }
                throw npe;
            }
            mHelperStrongRef.onNewItems(mResult, mReceiverPage);
        }
    }

    //-----------------------------------------
    //-----------------------------------------
    //`withNewItemsDeliveredTo()` stuff
    //-----------------------------------------
    //-----------------------------------------

    private static class DeliverToIItemAdapter<Model, Item extends IItem> implements OnNewItemsListener<Model> {
        @NonNull
        private final IItemAdapter<?, Item> mItemAdapter;
        @NonNull
        private final IInterceptor<Model, Item> mItemFactory;

        DeliverToIItemAdapter(@NonNull IItemAdapter<?, Item> itemAdapter, @NonNull IInterceptor<Model, Item> itemFactory) {
            mItemAdapter = itemAdapter;
            mItemFactory = itemFactory;
        }

        @Override
        public void onNewItems(@NonNull List<Model> newItems, int page) {
            int size = newItems.size();
            List<Item> iitems = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                iitems.add(mItemFactory.intercept(newItems.get(i)));
            }
            mItemAdapter.addInternal(iitems);
        }
    }

    private static class DeliverToModelAdapter<Model> implements OnNewItemsListener<Model> {
        @NonNull
        private final ModelAdapter<Model, ?> mModelAdapter;

        DeliverToModelAdapter(@NonNull ModelAdapter<Model, ?> modelItemAdapter) {
            mModelAdapter = modelItemAdapter;
        }

        @Override
        public void onNewItems(@NonNull List<Model> newItems, int page) {
            mModelAdapter.add(newItems);
        }
    }

    private static class DeliverToIItemAdapter2<Model, Item extends IItem> extends DeliverToIItemAdapter<Model, Item> {
        @NonNull
        private final OnNewItemsListener<Model> mExtraOnNewItemsListener;

        DeliverToIItemAdapter2(@NonNull IItemAdapter<?, Item> itemAdapter, @NonNull IInterceptor<Model, Item> itemFactory, @NonNull OnNewItemsListener<Model> extraOnNewItemsListener) {
            super(itemAdapter, itemFactory);
            mExtraOnNewItemsListener = extraOnNewItemsListener;
        }

        @Override
        public void onNewItems(@NonNull List<Model> newItems, int page) {
            mExtraOnNewItemsListener.onNewItems(newItems, page);
            super.onNewItems(newItems, page);
        }
    }

    private static class DeliverToModelAdapter2<Model> extends DeliverToModelAdapter<Model> {
        @NonNull
        private final OnNewItemsListener<Model> mExtraOnNewItemsListener;

        DeliverToModelAdapter2(@NonNull ModelAdapter<Model, ?> modelItemAdapter, @NonNull OnNewItemsListener<Model> extraOnNewItemsListener) {
            super(modelItemAdapter);
            mExtraOnNewItemsListener = extraOnNewItemsListener;
        }

        @Override
        public void onNewItems(@NonNull List<Model> newItems, int page) {
            mExtraOnNewItemsListener.onNewItems(newItems, page);
            super.onNewItems(newItems, page);
        }
    }
}
