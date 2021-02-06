package com.mikepenz.fastadapter.ui.dialog

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.GenericItem
import com.mikepenz.fastadapter.adapters.ItemAdapter

/**
 * Created by fabianterhorst on 04.07.16.
 */

class FastAdapterBottomSheetDialog<Item : GenericItem> : BottomSheetDialog {

    var recyclerView: RecyclerView? = null
        private set

    var fastAdapter: FastAdapter<Item>? = null

    lateinit var itemAdapter: ItemAdapter<Item>

    constructor(context: Context) : super(context) {
        this.recyclerView = createRecyclerView()
    }

    constructor(context: Context, theme: Int) : super(context, theme) {
        this.recyclerView = createRecyclerView()
    }

    /**
     * Create the RecyclerView and set it as the dialog view.
     *
     * @return the created RecyclerView
     */
    private fun createRecyclerView(): RecyclerView {
        val recyclerView = RecyclerView(context)
        val params = RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        )
        recyclerView.layoutParams = params
        setContentView(recyclerView)
        return recyclerView
    }

    fun withFastItemAdapter(
            fastAdapter: FastAdapter<Item>,
            itemAdapter: ItemAdapter<Item>
    ): FastAdapterBottomSheetDialog<Item> {
        this.fastAdapter = fastAdapter
        this.itemAdapter = itemAdapter
        recyclerView?.adapter = this.fastAdapter
        return this
    }

    private fun initAdapterIfNeeded() {
        if (fastAdapter == null || recyclerView?.adapter == null) {
            itemAdapter = ItemAdapter.items()
            fastAdapter = FastAdapter.with(itemAdapter)
            recyclerView?.adapter = fastAdapter
        }
    }

    fun withItems(items: List<Item>): FastAdapterBottomSheetDialog<Item> {
        initAdapterIfNeeded()
        itemAdapter.set(items)
        return this
    }

    fun withItems(vararg items: Item): FastAdapterBottomSheetDialog<Item> {
        initAdapterIfNeeded()
        itemAdapter.add(*items)
        return this
    }

    fun withAdapter(adapter: FastAdapter<Item>): FastAdapterBottomSheetDialog<Item> {
        this.recyclerView?.adapter = adapter
        return this
    }

    /**
     * Set the [RecyclerView.LayoutManager] that the RecyclerView will use.
     *
     * @param layoutManager LayoutManager to use
     */
    fun withLayoutManager(layoutManager: RecyclerView.LayoutManager): FastAdapterBottomSheetDialog<Item> {
        this.recyclerView?.layoutManager = layoutManager
        return this
    }

    /**
     * Add a listener that will be notified of any changes in scroll state or position of the
     * RecyclerView.
     *
     * @param listener listener to set or null to clear
     */
    fun withOnScrollListener(listener: RecyclerView.OnScrollListener): FastAdapterBottomSheetDialog<Item> {
        recyclerView?.addOnScrollListener(listener)
        return this
    }

    /**
     * Start the dialog and display it on screen.  The window is placed in the
     * application layer and opaque.  Note that you should not override this
     * method to do initialization when the dialog is shown, instead implement
     * that in [onStart].
     */
    override fun show() {
        if (recyclerView?.layoutManager == null) {
            recyclerView?.layoutManager = LinearLayoutManager(context)
        }
        initAdapterIfNeeded()
        super.show()
    }
}