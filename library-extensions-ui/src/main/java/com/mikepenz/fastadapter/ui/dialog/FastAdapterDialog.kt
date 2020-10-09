package com.mikepenz.fastadapter.ui.dialog

import android.content.Context
import android.content.DialogInterface
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.GenericItem
import com.mikepenz.fastadapter.adapters.ItemAdapter

class FastAdapterDialog<Item : GenericItem> : AlertDialog {

    var recyclerView: RecyclerView? = null
        private set

    var fastAdapter: FastAdapter<Item>? = null
    var itemAdapter: ItemAdapter<Item>? = null

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
        setView(recyclerView)
        return recyclerView
    }

    /**
     * Set the title text for this dialog's window.
     *
     * @param title The text to display in the title.
     */
    fun withTitle(title: String): FastAdapterDialog<Item> {
        setTitle(title)
        return this
    }

    /**
     * Set the title text for this dialog's window.
     *
     * @param titleRes The resource id of the text to display in the title.
     */
    fun withTitle(@StringRes titleRes: Int): FastAdapterDialog<Item> {
        setTitle(titleRes)
        return this
    }

    fun withFastItemAdapter(
            fastAdapter: FastAdapter<Item>,
            itemAdapter: ItemAdapter<Item>
    ): FastAdapterDialog<Item> {
        this.fastAdapter = fastAdapter
        this.itemAdapter = itemAdapter
        recyclerView!!.adapter = this.fastAdapter
        return this
    }

    private fun initAdapterIfNeeded() {
        if (fastAdapter == null || recyclerView!!.adapter == null) {
            itemAdapter = ItemAdapter.items()
            fastAdapter = FastAdapter.with(itemAdapter!!)
            recyclerView!!.adapter = fastAdapter
        }
    }

    fun withItems(items: List<Item>): FastAdapterDialog<Item> {
        initAdapterIfNeeded()
        itemAdapter!!.set(items)
        return this
    }

    fun withItems(vararg items: Item): FastAdapterDialog<Item> {
        initAdapterIfNeeded()
        itemAdapter!!.add(*items)
        return this
    }

    fun withAdapter(adapter: FastAdapter<Item>): FastAdapterDialog<Item> {
        this.recyclerView!!.adapter = adapter
        return this
    }

    /**
     * Set the [RecyclerView.LayoutManager] that the RecyclerView will use.
     *
     * @param layoutManager LayoutManager to use
     */
    fun withLayoutManager(layoutManager: RecyclerView.LayoutManager): FastAdapterDialog<Item> {
        this.recyclerView!!.layoutManager = layoutManager
        return this
    }

    /**
     * Add a listener that will be notified of any changes in scroll state or position of the
     * RecyclerView.
     *
     * @param listener listener to set or null to clear
     */
    fun withOnScrollListener(listener: RecyclerView.OnScrollListener): FastAdapterDialog<Item> {
        recyclerView!!.addOnScrollListener(listener)
        return this
    }

    /**
     * Set a listener to be invoked when the positive button of the dialog is pressed.
     *
     * @param text     The text to display in the positive button
     * @param listener The [DialogInterface.OnClickListener] to use.
     * @return This Builder object to allow for chaining of calls to set methods
     */
    fun withPositiveButton(
            text: String,
            listener: DialogInterface.OnClickListener
    ): FastAdapterDialog<Item> {
        return withButton(DialogInterface.BUTTON_POSITIVE, text, listener)
    }

    /**
     * Set a listener to be invoked when the positive button of the dialog is pressed.
     *
     * @param textRes  The resource id of the text to display in the positive button
     * @param listener The [DialogInterface.OnClickListener] to use.
     * @return This Builder object to allow for chaining of calls to set methods
     */
    fun withPositiveButton(@StringRes textRes: Int, listener: DialogInterface.OnClickListener): FastAdapterDialog<Item> {
        return withButton(DialogInterface.BUTTON_POSITIVE, textRes, listener)
    }

    /**
     * Set a listener to be invoked when the negative button of the dialog is pressed.
     *
     * @param text     The text to display in the negative button
     * @param listener The [DialogInterface.OnClickListener] to use.
     * @return This Builder object to allow for chaining of calls to set methods
     */
    fun withNegativeButton(
            text: String,
            listener: DialogInterface.OnClickListener
    ): FastAdapterDialog<Item> {
        return withButton(DialogInterface.BUTTON_NEGATIVE, text, listener)
    }

    /**
     * Set a listener to be invoked when the negative button of the dialog is pressed.
     *
     * @param textRes  The resource id of the text to display in the negative button
     * @param listener The [DialogInterface.OnClickListener] to use.
     * @return This Builder object to allow for chaining of calls to set methods
     */
    fun withNegativeButton(@StringRes textRes: Int, listener: DialogInterface.OnClickListener): FastAdapterDialog<Item> {
        return withButton(DialogInterface.BUTTON_NEGATIVE, textRes, listener)
    }

    /**
     * Adds a negative button to the dialog. The button click will close the dialog.
     *
     * @param textRes The resource id of the text to display in the negative button
     * @return This Builder object to allow for chaining of calls to set methods
     */
    fun withNegativeButton(@StringRes textRes: Int): FastAdapterDialog<Item> {
        return withButton(DialogInterface.BUTTON_NEGATIVE, textRes, null)
    }

    /**
     * Adds a negative button to the dialog. The button click will close the dialog.
     *
     * @param text The text to display in the negative button
     * @return This Builder object to allow for chaining of calls to set methods
     */
    fun withNegativeButton(text: String): FastAdapterDialog<Item> {
        return withButton(DialogInterface.BUTTON_NEGATIVE, text, null)
    }

    /**
     * Set a listener to be invoked when the neutral button of the dialog is pressed.
     *
     * @param text     The text to display in the neutral button
     * @param listener The [DialogInterface.OnClickListener] to use.
     * @return This Builder object to allow for chaining of calls to set methods
     */
    fun withNeutralButton(
            text: String,
            listener: DialogInterface.OnClickListener
    ): FastAdapterDialog<Item> {
        return withButton(DialogInterface.BUTTON_NEUTRAL, text, listener)
    }

    /**
     * Set a listener to be invoked when the neutral button of the dialog is pressed.
     *
     * @param textRes  The resource id of the text to display in the neutral button
     * @param listener The [DialogInterface.OnClickListener] to use.
     * @return This Builder object to allow for chaining of calls to set methods
     */
    fun withNeutralButton(@StringRes textRes: Int, listener: DialogInterface.OnClickListener): FastAdapterDialog<Item> {
        return withButton(DialogInterface.BUTTON_NEUTRAL, textRes, listener)
    }

    /**
     * Sets a listener to be invoked when the positive button of the dialog is pressed. This method
     * has no effect if called after [.show].
     *
     * @param whichButton Which button to set the listener on, can be one of
     * [DialogInterface.BUTTON_POSITIVE],
     * [DialogInterface.BUTTON_NEGATIVE], or
     * [DialogInterface.BUTTON_NEUTRAL]
     * @param text        The text to display in positive button.
     * @param listener    The [DialogInterface.OnClickListener] to use.
     */
    fun withButton(
            whichButton: Int,
            text: String,
            listener: DialogInterface.OnClickListener?
    ): FastAdapterDialog<Item> {
        setButton(whichButton, text, listener)
        return this
    }

    /**
     * Sets a listener to be invoked when the positive button of the dialog is pressed. This method
     * has no effect if called after [.show].
     *
     * @param whichButton Which button to set the listener on, can be one of
     * [DialogInterface.BUTTON_POSITIVE],
     * [DialogInterface.BUTTON_NEGATIVE], or
     * [DialogInterface.BUTTON_NEUTRAL]
     * @param textRes     The text to display in positive button.
     * @param listener    The [DialogInterface.OnClickListener] to use.
     */
    fun withButton(
            whichButton: Int, @StringRes textRes: Int,
            listener: DialogInterface.OnClickListener?
    ): FastAdapterDialog<Item> {
        setButton(whichButton, context.getString(textRes), listener)
        return this
    }

    /**
     * Start the dialog and display it on screen.  The window is placed in the
     * application layer and opaque.  Note that you should not override this
     * method to do initialization when the dialog is shown, instead implement
     * that in [onStart].
     */
    override fun show() {
        if (recyclerView!!.layoutManager == null) {
            recyclerView!!.layoutManager = LinearLayoutManager(context)
        }
        initAdapterIfNeeded()
        super.show()
    }
}
