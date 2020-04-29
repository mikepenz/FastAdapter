package com.mikepenz.fastadapter.helpers

import android.os.Bundle
import com.mikepenz.fastadapter.GenericItem
import com.mikepenz.fastadapter.IExpandable
import com.mikepenz.fastadapter.IItem
import com.mikepenz.fastadapter.adapters.FastItemAdapter
import com.mikepenz.fastadapter.select.SelectExtension
import com.mikepenz.fastadapter.utils.SubItemUtil

/**
 * Created by Michael on 15.09.2016.
 */
open class RangeSelectorHelper<Item : GenericItem>(private val fastAdapter: FastItemAdapter<Item>) {
    private var actionModeHelper: ActionModeHelper<*>? = null
    private var supportSubItems = false
    private var payload: Any? = null

    private var lastLongPressIndex: Int? = null

    /**
     * Set the ActionModeHelper, if you want to notify it after a range was selected
     * so that it can update the ActionMode title
     *
     * @param actionModeHelper the action mode helper that should be used
     * @return this, for supporting function call chaining
     */
    fun withActionModeHelper(actionModeHelper: ActionModeHelper<*>?): RangeSelectorHelper<*> {
        this.actionModeHelper = actionModeHelper
        return this
    }

    /**
     * Enable this, if you want the range selector to correclty handle sub items as well
     *
     * @param supportSubItems true, if sub items are supported, false otherwise
     * @return this, for supporting function call chaining
     */
    fun withSupportSubItems(supportSubItems: Boolean): RangeSelectorHelper<*> {
        this.supportSubItems = supportSubItems
        return this
    }

    /**
     * The provided payload will be passed to the adapters notify function, if one is provided
     *
     * @param payload the paylaod that should be passed to the adapter on selection state change
     * @return this, for supporting function call chaining
     */
    fun withPayload(payload: Any): RangeSelectorHelper<*> {
        this.payload = payload
        return this
    }

    /**
     * Resets the last long pressed index, we only want to respect two consecutive long clicks for selecting a range of items
     */
    fun onClick() {
        reset()
    }

    /**
     * Resets the last long pressed index, we only want to respect two consecutive long presses for selecting a range of items
     */
    fun reset() {
        lastLongPressIndex = null
    }

    /**
     * Will take care to save the long pressed index
     * or to select all items in the range between the current long pressed item and the last long pressed item
     *
     * @param index      the index of the long pressed item
     * @param selectItem true, if the item at the index should be selected, false if this was already done outside of this helper or is not desired
     * @return true, if the long press was handled
     */
    @JvmOverloads
    fun onLongClick(index: Int, selectItem: Boolean = true): Boolean {
        if (lastLongPressIndex == null) {
            // we only consider long presses on not selected items
            if (fastAdapter.getAdapterItem(index).isSelectable) {
                lastLongPressIndex = index
                // we select this item as well
                if (selectItem) {
                    val selectExtension: SelectExtension<Item>? = fastAdapter.getExtension(SelectExtension::class.java)
                    selectExtension?.select(index)
                }
                actionModeHelper?.checkActionMode(null) // works with null as well, as the ActionMode is active for sure!
                return true
            }
        } else if (lastLongPressIndex != index) {
            lastLongPressIndex?.let {
                // select all items in the range between the two long clicks
                selectRange(it, index, true)
            }
            // reset the index
            lastLongPressIndex = null
        }
        return false
    }

    /**
     * Selects all items in a range, from and to indizes are inclusive
     *
     * @param from        the from index
     * @param to          the to index
     * @param select      true, if the provided range should be selected, false otherwise
     * @param skipHeaders true, if you do not want to process headers, false otherwise
     */
    @JvmOverloads
    fun selectRange(_from: Int, _to: Int, select: Boolean, skipHeaders: Boolean = false) {
        var from = _from
        var to = _to
        if (from > to) {
            val temp = from
            from = to
            to = temp
        }

        var item: IItem<*>
        for (i in from..to) {
            item = fastAdapter.getAdapterItem(i)
            if (item.isSelectable) {
                val selectExtension: SelectExtension<Item>? = fastAdapter.getExtension(SelectExtension::class.java)
                if (selectExtension != null) {
                    if (select) {
                        selectExtension.select(i)
                    } else {
                        selectExtension.deselect(i)
                    }
                }
            }
            if (supportSubItems && !skipHeaders) {
                // if a group is collapsed, select all sub items
                if (item is IExpandable<*> && !(item as IExpandable<*>).isExpanded) {
                    SubItemUtil.selectAllSubItems(fastAdapter, fastAdapter.getAdapterItem(i), select, true, payload)
                }
            }
        }

        actionModeHelper?.checkActionMode(null) // works with null as well, as the ActionMode is active for sure!
    }

    /**
     * Add the values to the bundle for saveInstanceState
     *
     * @param savedInstanceState If the activity is being re-initialized after
     * previously being shut down then this Bundle contains the data it most
     * recently supplied in Note: Otherwise it is null.
     * @param prefix             a prefix added to the savedInstance key so we can store multiple states
     * @return the passed bundle with the newly added data
     */
    @JvmOverloads
    fun saveInstanceState(savedInstanceState: Bundle, prefix: String = ""): Bundle {
        lastLongPressIndex?.let {
            savedInstanceState.putInt(BUNDLE_LAST_LONG_PRESS, it)
        }
        return savedInstanceState
    }

    /**
     * Restore the index of the last long pressed index
     * IMPORTANT! Call this method only after all items where added to the adapters again. Otherwise it may select wrong items!
     *
     * @param savedInstanceState If the activity is being re-initialized after
     * previously being shut down then this Bundle contains the data it most
     * recently supplied in Note: Otherwise it is null.
     * @param prefix             a prefix added to the savedInstance key so we can store multiple states
     * @return this
     */
    @JvmOverloads
    fun withSavedInstanceState(savedInstanceState: Bundle?, prefix: String = ""): RangeSelectorHelper<*> {
        if (savedInstanceState != null && savedInstanceState.containsKey(BUNDLE_LAST_LONG_PRESS + prefix))
            lastLongPressIndex = savedInstanceState.getInt(BUNDLE_LAST_LONG_PRESS + prefix)
        return this
    }

    companion object {

        protected const val BUNDLE_LAST_LONG_PRESS = "bundle_last_long_press"
    }
}
