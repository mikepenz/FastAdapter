package com.mikepenz.fastadapter_extensions.scroll;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.view.View;

import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.IItemAdapter;
import com.mikepenz.fastadapter.adapters.GenericItemAdapter;
import com.mikepenz.fastadapter.items.GenericAbstractItem;
import com.mikepenz.fastadapter.utils.Function;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import static android.support.v7.widget.com_mikepenz_fastadapter_extensions_scroll.postOnRecyclerView;

/**
 * This is an extension of {@link EndlessRecyclerOnScrollListener}, providing a more powerful API
 * for endless scrolling.
 * <p>
 * This class exposes 2 callbacks to separate the loading logic from delivering the results:
 * <ul>
 * <li>{@link OnLoadMoreHandler OnLoadMoreHandler}</li>
 * <li>{@link OnNewItemsListener OnNewItemsListener}</li>
 * </ul>
 * <p>
 * This class also takes care of other various stuffs like:
 * <ul>
 * <li>Ensuring the results are delivered on the RecyclerView's handler &ndash; which also ensures
 * that the results are delivered only when the RecyclerView is attached to the window, see {@link
 * View#post(Runnable)}.</li>
 * <li>Prevention of memory leaks when implemented properly (i.e. {@link OnLoadMoreHandler
 * OnLoadMoreHandler} should be implemented via static classes or lambda expressions).</li>
 * <li>An easier way to deliver results to an {@link #withNewItemsDeliveredTo(IItemAdapter,
 * Function)  IItemAdapter} or {@link #withNewItemsDeliveredTo(GenericItemAdapter)
 * GenericItemAdapter}.</li>
 * </ul>
 * <p>
 * Created by jayson on 3/26/2016.
 */
public class EndlessScrollHelper<Model> extends EndlessRecyclerOnScrollListener {
    private OnLoadMoreHandler<Model> mOnLoadMoreHandler;
    private OnNewItemsListener<Model> mOnNewItemsListener;

    public EndlessScrollHelper(LayoutManager layoutManager) {
        super(layoutManager);
    }

    public EndlessScrollHelper(LayoutManager layoutManager, int visibleThreshold) {
        super(layoutManager, visibleThreshold);
    }

    public interface ResultReceiver<R> {

        int getReceiverPage();

        boolean deliverNewItems(@NonNull List<R> result);
    }

    public interface OnLoadMoreHandler<R> {

        void onLoadMore(ResultReceiver out, int currentPage);
    }

    public interface OnNewItemsListener<R> {

        void onNewItems(List<R> newItems, int page);
    }

    public EndlessScrollHelper<Model> withOnLoadMoreHandler(OnLoadMoreHandler<Model> onLoadMoreHandler) {
        if (onLoadMoreHandler == null) {
            throw new NullPointerException("onLoadMoreHandler == null");
        }
        mOnLoadMoreHandler = onLoadMoreHandler;
        return this;
    }

    public EndlessScrollHelper<Model> withOnNewItemsListener(OnNewItemsListener<Model> onNewItemsListener) {
        if (onNewItemsListener == null) {
            throw new NullPointerException("onNewItemsListener == null");
        }
        mOnNewItemsListener = onNewItemsListener;
        return this;
    }

    public <Item extends IItem> EndlessScrollHelper<Model> withNewItemsDeliveredTo(IItemAdapter<Item> adapter, Function<Model, Item> itemFactory) {
        if (adapter == null) {
            throw new NullPointerException("adapter == null");
        }
        if (itemFactory == null) {
            throw new NullPointerException("itemFactory == null");
        }
        mOnNewItemsListener = new DeliverToIItemAdapter<>(adapter, itemFactory);
        return this;
    }

    public EndlessScrollHelper<Model> withNewItemsDeliveredTo(GenericItemAdapter<Model, ?> adapter) {
        if (adapter == null) {
            throw new NullPointerException("adapter == null");
        }
        mOnNewItemsListener = new DeliverToGenericItemAdapter<>(adapter);
        return this;
    }

    protected void onLoadMore(ResultReceiver out, int currentPage) {
        OnLoadMoreHandler<Model> loadMoreHandler = this.mOnLoadMoreHandler;
        try {
            loadMoreHandler.onLoadMore(out, currentPage);
        } catch (NullPointerException npe) {
            // Lazy null checking!
            if (loadMoreHandler == null) {
                throw new NullPointerException("You must provide an `OnLoadMoreHandler`");
            }
            throw npe;
        }
    }

    protected void onNewItems(List<Model> newItems, int page) {
        OnNewItemsListener<Model> onNewItemsListener = this.mOnNewItemsListener;
        try {
            onNewItemsListener.onNewItems(newItems, page);
        } catch (NullPointerException npe) {
            // Lazy null checking!
            if (onNewItemsListener == null) {
                throw new NullPointerException("You must provide an `OnNewItemsListener`");
            }
            throw npe;
        }
    }

    @Override
    public void onLoadMore(int currentPage) {
        onLoadMore(new ResultReceiverImpl<>(this, currentPage), currentPage);
    }

    private static final class ResultReceiverImpl<R> extends WeakReference<EndlessScrollHelper<R>> implements ResultReceiver<R>, Runnable {
        private final int mReceiverPage;
        private EndlessScrollHelper<R> mHelperStrongRef;
        private List<R> mResult;

        ResultReceiverImpl(EndlessScrollHelper<R> helper, int receiverPage) {
            super(helper); // We use WeakReferences to outer class to avoid memory leaks.
            mReceiverPage = receiverPage;
        }

        @Override
        public int getReceiverPage() {
            return mReceiverPage;
        }

        @Override
        public boolean deliverNewItems(@NonNull List<R> result) {
            if (mResult != null) // We might also see `null` here if more than 1 thread is modifying this.
                throw new IllegalStateException("`result` already provided!");
            mResult = result;
            mHelperStrongRef = super.get();
            return mHelperStrongRef != null
                    && postOnRecyclerView(mHelperStrongRef.mLayoutManager, this);
        }

        @Override
        public void run() {
            // At this point, mHelperStrongRef != null
            try {
                if (mHelperStrongRef.getCurrentPage() != mReceiverPage) {
                    throw new IllegalStateException("Inconsistent state! "
                            + "Page might have already been loaded! "
                            + "Or `loadMore(result)` might have been used by more than 1 thread!");
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

    private static class DeliverToIItemAdapter<Model, Item extends IItem> implements OnNewItemsListener<Model> {
        private final IItemAdapter<Item> mItemAdapter;
        private final Function<Model, Item> mItemFactory;

        DeliverToIItemAdapter(IItemAdapter<Item> itemAdapter, Function<Model, Item> itemFactory) {
            mItemAdapter = itemAdapter;
            mItemFactory = itemFactory;
        }

        @Override
        public void onNewItems(List<Model> newItems, int page) {
            List<Item> iitems = new ArrayList<>(newItems.size());
            for (Model model : newItems) {
                iitems.add(mItemFactory.apply(model));
            }
            mItemAdapter.add(iitems);
        }
    }

    private static class DeliverToGenericItemAdapter<Model, Item extends GenericAbstractItem<Model, Item, ?>> implements OnNewItemsListener<Model> {
        private final GenericItemAdapter<Model, Item> mGenericItemAdapter;

        DeliverToGenericItemAdapter(GenericItemAdapter<Model, Item> itemAdapter) {
            mGenericItemAdapter = itemAdapter;
        }

        @Override
        public void onNewItems(List<Model> newItems, int page) {
            mGenericItemAdapter.addModel(newItems);
        }
    }
}
