package com.mikepenz.fastadapter.scroll

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import androidx.recyclerview.widget.com_mikepenz_fastadapter_extensions_scroll.postOnRecyclerView
import com.mikepenz.fastadapter.GenericItem
import com.mikepenz.fastadapter.IItem
import com.mikepenz.fastadapter.IItemAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.mikepenz.fastadapter.adapters.ModelAdapter
import com.mikepenz.fastadapter.scroll.EndlessScrollHelper.OnLoadMoreHandler
import com.mikepenz.fastadapter.scroll.EndlessScrollHelper.OnNewItemsListener
import java.lang.ref.WeakReference

/**
 * This is an extension of [EndlessRecyclerOnScrollListener], providing a more powerful API
 * for endless scrolling.
 * This class exposes 2 callbacks to separate the loading logic from delivering the results:
 *
 *  * [OnLoadMoreHandler]
 *  * [OnNewItemsListener]
 *
 * This class also takes care of other various stuffs like:
 *
 *  * Ensuring the results are delivered on the RecyclerView's handler  which also ensures
 * that the results are delivered only when the RecyclerView is attached to the window, see [View.post].
 *  * Prevention of memory leaks when implemented properly (i.e. [OnLoadMoreHandler] should be implemented via static classes or lambda expressions).
 *  * An easier way to deliver results to an [IItemAdapter][withNewItemsDeliveredTo] or [ModelAdapter][withNewItemsDeliveredTo].
 *
 * Created by jayson on 3/26/2016.
 */
open class EndlessScrollHelper<Model> : EndlessRecyclerOnScrollListener {
    private var onLoadMoreHandler: OnLoadMoreHandler<Model>? = null
    private var onNewItemsListener: OnNewItemsListener<Model>? = null

    constructor()

    constructor(layoutManager: LayoutManager) : super(layoutManager)

    constructor(layoutManager: LayoutManager, visibleThreshold: Int) : super(layoutManager, visibleThreshold)

    /**
     * @param layoutManager
     * @param visibleThreshold
     * @param footerAdapter    the itemAdapter used to host Footer items
     */
    constructor(layoutManager: LayoutManager, visibleThreshold: Int, footerAdapter: ItemAdapter<*>) : super(layoutManager, visibleThreshold, footerAdapter)

    fun addTo(recyclerView: RecyclerView): EndlessScrollHelper<Model> {
        recyclerView.addOnScrollListener(this)
        return this
    }

    /**
     * A callback interface provided by the [EndlessScrollHelper] where [onLoadMore]
     * results are to be delivered.
     * The underlying implementation is safe to use by any background-thread, as long as only 1
     * thread is using it. Results delivered via [deliverNewItems] are automatically
     * dispatched to the RecyclerView's message queue (i.e. to be delivered in the ui thread).
     */
    interface ResultReceiver<Model> {

        /** @return the current page where the results will be delivered. */
        val receiverPage: Int

        /**
         * Delivers the result of an [onLoadMore] for the current [page][receiverPage].
         * This method must be called only once.
         *
         * @param result the result of an [onLoadMore()][onLoadMore]
         * @return whether results where delivered successfully or not, possibly because the
         * RecyclerView is no longer attached or the [EndlessScrollHelper] is no longer
         * in use (and it has been garbage collected).
         * @throws IllegalStateException when more than one results are delivered.
         */
        fun deliverNewItems(result: List<Model>): Boolean
    }

    interface OnLoadMoreHandler<Model> {

        /** Handles loading of the specified page and delivers the results to the specified [ResultReceiver] */
        fun onLoadMore(out: ResultReceiver<Model>, currentPage: Int)
    }

    interface OnNewItemsListener<Model> {

        /** Called on the RecyclerView's message queue to receive the results of a previous [onLoadMore] */
        fun onNewItems(newItems: List<Model>, page: Int)
    }

    /** Define the [OnLoadMoreHandler] which will be used for loading new items. */
    fun withOnLoadMoreHandler(onLoadMoreHandler: OnLoadMoreHandler<Model>): EndlessScrollHelper<Model> {
        this.onLoadMoreHandler = onLoadMoreHandler
        return this
    }

    /** Define the [OnNewItemsListener] which will receive the new items loaded by [onLoadMore] */
    fun withOnNewItemsListener(onNewItemsListener: OnNewItemsListener<Model>): EndlessScrollHelper<Model> {
        this.onNewItemsListener = onNewItemsListener
        return this
    }

    /**
     * Registers an [OnNewItemsListener] that delivers results to the
     * specified [IItemAdapter]. Converting each result to an [IItem] using the given [itemFactory].
     */
    fun <Item : GenericItem> withNewItemsDeliveredTo(itemAdapter: IItemAdapter<*, Item>, itemFactory: (element: Model) -> Item?): EndlessScrollHelper<Model> {
        onNewItemsListener = DeliverToIItemAdapter(itemAdapter, itemFactory)
        return this
    }

    /**
     * Registers an [OnNewItemsListener] that delivers results to the
     * specified [ModelAdapter] through its [ModelAdapter.add] method.
     */
    fun withNewItemsDeliveredTo(modelItemAdapter: ModelAdapter<Model, *>): EndlessScrollHelper<Model> {
        onNewItemsListener = DeliverToModelAdapter(modelItemAdapter)
        return this
    }

    /**
     * An overload of [withNewItemsDeliveredTo()][withNewItemsDeliveredTo]
     * that allows additional callbacks.
     */
    fun <Item : GenericItem> withNewItemsDeliveredTo(itemAdapter: IItemAdapter<*, Item>, itemFactory: (element: Model) -> Item?, extraOnNewItemsListener: OnNewItemsListener<Model>): EndlessScrollHelper<Model> {
        onNewItemsListener = DeliverToIItemAdapter2(itemAdapter, itemFactory, extraOnNewItemsListener)
        return this
    }

    /**
     * An overload of [withNewItemsDeliveredTo()][withNewItemsDeliveredTo] that allows additional callbacks.
     */
    fun withNewItemsDeliveredTo(modelItemAdapter: ModelAdapter<Model, *>, extraOnNewItemsListener: OnNewItemsListener<Model>): EndlessScrollHelper<Model> {
        onNewItemsListener = DeliverToModelAdapter2(modelItemAdapter, extraOnNewItemsListener)
        return this
    }

    //-------------------------
    //-------------------------
    //Override-able methods
    //-------------------------
    //-------------------------

    /** The default implementation takes care of calling the previously set [OnLoadMoreHandler] */
    protected fun onLoadMore(out: ResultReceiver<Model>, currentPage: Int) {
        val loadMoreHandler = this.onLoadMoreHandler
        try {
            loadMoreHandler?.onLoadMore(out, currentPage)
        } catch (npe: NullPointerException) {
            // Lazy null checking! If this was our npe, then throw with an appropriate message.
            throw if (loadMoreHandler != null)
                npe
            else
                NullPointerException("You must provide an `OnLoadMoreHandler`")
        }
    }

    /** The default implementation takes care of calling the previously set [OnNewItemsListener] */
    protected fun onNewItems(newItems: List<Model>, page: Int) {
        val onNewItemsListener = this.onNewItemsListener
        try {
            onNewItemsListener?.onNewItems(newItems, page)
        } catch (npe: NullPointerException) {
            // Lazy null checking! If this was our npe, then throw with an appropriate message.
            throw if (onNewItemsListener != null)
                npe
            else
                NullPointerException("You must provide an `OnNewItemsListener`")
        }
    }

    //-------------------------
    //-------------------------
    //Internal stuff
    //-------------------------
    //-------------------------

    override fun onLoadMore(currentPage: Int) {
        onLoadMore(ResultReceiverImpl(this, currentPage), currentPage)
    }

    private class ResultReceiverImpl<Model> internal constructor(helper: EndlessScrollHelper<Model>, override val receiverPage: Int)
        // We use WeakReferences to outer class to avoid memory leaks.
        : WeakReference<EndlessScrollHelper<Model>>(helper), ResultReceiver<Model>, Runnable {
        private var helperStrongRef: EndlessScrollHelper<Model>? = null
        private var result: List<Model>? = null

        override fun deliverNewItems(result: List<Model>): Boolean {
            if (this.result != null) {
                // We might also see `null` here if more than 1 thread is modifying this.
                throw IllegalStateException("`result` already provided!")
            }
            this.result = result
            helperStrongRef = super.get()
            helperStrongRef?.let {
                return postOnRecyclerView(it.layoutManager, this)
            }
            return false
        }

        override fun run() {
            // At this point, mHelperStrongRef != null
            try {
                if (helperStrongRef?.currentPage != receiverPage) {
                    //                    throw new IllegalStateException("Inconsistent state! "
                    //                            + "Page might have already been loaded! "
                    //                            + "Or `loadMore(result)` might have been used by more than 1 thread!");
                    return  // Let it fail and possibly load correctly
                }
            } catch (npe: NullPointerException) {
                if (helperStrongRef == null) {
                    throw AssertionError(npe)
                }
                throw npe
            }

            result?.let { helperStrongRef?.onNewItems(it, receiverPage) }
        }
    }

    //-----------------------------------------
    //-----------------------------------------
    //`withNewItemsDeliveredTo()` stuff
    //-----------------------------------------
    //-----------------------------------------

    private open class DeliverToIItemAdapter<Model, Item : GenericItem> internal constructor(private val itemAdapter: IItemAdapter<*, Item>, private val itemFactory: (element: Model) -> Item?) : OnNewItemsListener<Model> {

        override fun onNewItems(newItems: List<Model>, page: Int) {
            val items = newItems.mapNotNull(itemFactory)
            itemAdapter.addInternal(items)
        }
    }

    private open class DeliverToModelAdapter<Model> internal constructor(private val modelAdapter: ModelAdapter<Model, *>) : OnNewItemsListener<Model> {
        override fun onNewItems(newItems: List<Model>, page: Int) {
            modelAdapter.add(newItems)
        }
    }

    private class DeliverToIItemAdapter2<Model, Item : GenericItem> internal constructor(itemAdapter: IItemAdapter<*, Item>, itemFactory: (element: Model) -> Item?, private val extraOnNewItemsListener: OnNewItemsListener<Model>) : DeliverToIItemAdapter<Model, Item>(itemAdapter, itemFactory) {
        override fun onNewItems(newItems: List<Model>, page: Int) {
            extraOnNewItemsListener.onNewItems(newItems, page)
            super.onNewItems(newItems, page)
        }
    }

    private class DeliverToModelAdapter2<Model> internal constructor(modelItemAdapter: ModelAdapter<Model, *>, private val extraOnNewItemsListener: OnNewItemsListener<Model>) : DeliverToModelAdapter<Model>(modelItemAdapter) {
        override fun onNewItems(newItems: List<Model>, page: Int) {
            extraOnNewItemsListener.onNewItems(newItems, page)
            super.onNewItems(newItems, page)
        }
    }
}
