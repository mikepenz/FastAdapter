package com.mikepenz.fastadapter.helpers

import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.fastadapter.IExpandable
import com.mikepenz.fastadapter.IItem
import com.mikepenz.fastadapter.adapters.FastItemAdapter
import com.mikepenz.fastadapter.select.SelectExtension
import com.mikepenz.fastadapter.utils.SubItemUtil

/**
 * Created by Michael on 15.09.2016.
 */
class RangeSelectorHelper<Item : IItem<out RecyclerView.ViewHolder>>(private val mFastAdapter: FastItemAdapter<Item>) {
    private var mActionModeHelper: ActionModeHelper<*>? = null
    private var mSupportSubItems = false
    private var mPayload: Any? = null

    private var mLastLongPressIndex: Int? = null

    /**
     * set the ActionModeHelper, if you want to notify it after a range was selected
     * so that it can update the ActionMode title
     *
     * @param actionModeHelper the action mode helper that should be used
     * @return this, for supporting function call chaining
     */
    fun withActionModeHelper(actionModeHelper: ActionModeHelper<*>?): RangeSelectorHelper<*> {
        mActionModeHelper = actionModeHelper
        return this
    }

    /**
     * enable this, if you want the range selector to correclty handle sub items as well
     *
     * @param supportSubItems true, if sub items are supported, false otherwise
     * @return this, for supporting function call chaining
     */
    fun withSupportSubItems(supportSubItems: Boolean): RangeSelectorHelper<*> {
        this.mSupportSubItems = supportSubItems
        return this
    }

    /**
     * the provided payload will be passed to the adapters notify function, if one is provided
     *
     * @param payload the paylaod that should be passed to the adapter on selection state change
     * @return this, for supporting function call chaining
     */
    fun withPayload(payload: Any): RangeSelectorHelper<*> {
        mPayload = payload
        return this
    }

    /**
     * resets the last long pressed index, we only want to respect two consecutive long clicks for selecting a range of items
     */
    fun onClick() {
        reset()
    }

    /**
     * resets the last long pressed index, we only want to respect two consecutive long presses for selecting a range of items
     */
    fun reset() {
        mLastLongPressIndex = null
    }

    /**
     * will take care to save the long pressed index
     * or to select all items in the range between the current long pressed item and the last long pressed item
     *
     * @param index      the index of the long pressed item
     * @param selectItem true, if the item at the index should be selected, false if this was already done outside of this helper or is not desired
     * @return true, if the long press was handled
     */
    @JvmOverloads
    fun onLongClick(index: Int, selectItem: Boolean = true): Boolean {
        if (mLastLongPressIndex == null) {
            // we only consider long presses on not selected items
            if (mFastAdapter.getAdapterItem(index).isSelectable) {
                mLastLongPressIndex = index
                // we select this item as well
                if (selectItem) {
                    val selectExtension: SelectExtension<Item>? = mFastAdapter.getExtension(SelectExtension::class.java)
                    selectExtension?.select(index)
                }
                mActionModeHelper?.checkActionMode(null) // works with null as well, as the ActionMode is active for sure!
                return true
            }
        } else if (mLastLongPressIndex != index) {
            mLastLongPressIndex?.let {
                // select all items in the range between the two long clicks
                selectRange(it, index, true)
            }
            // reset the index
            mLastLongPressIndex = null
        }
        return false
    }

    /**
     * selects all items in a range, from and to indizes are inclusive
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
            item = mFastAdapter.getAdapterItem(i)
            if (item.isSelectable) {
                val selectExtension: SelectExtension<Item>? = mFastAdapter.getExtension(SelectExtension::class.java)
                if (selectExtension != null) {
                    if (select) {
                        selectExtension.select(i)
                    } else {
                        selectExtension.deselect(i)
                    }
                }
            }
            if (mSupportSubItems && !skipHeaders) {
                // if a group is collapsed, select all sub items
                if (item is IExpandable<*> && !(item as IExpandable<*>).isExpanded) {
                    SubItemUtil.selectAllSubItems(mFastAdapter, mFastAdapter.getAdapterItem(i), select, true, mPayload)
                }
            }
        }

        mActionModeHelper?.checkActionMode(null) // works with null as well, as the ActionMode is active for sure!
    }

    /**
     * add the values to the bundle for saveInstanceState
     *
     * @param savedInstanceState If the activity is being re-initialized after
     * previously being shut down then this Bundle contains the data it most
     * recently supplied in Note: Otherwise it is null.
     * @param prefix             a prefix added to the savedInstance key so we can store multiple states
     * @return the passed bundle with the newly added data
     */
    @JvmOverloads
    fun saveInstanceState(savedInstanceState: Bundle, prefix: String = ""): Bundle {
        mLastLongPressIndex?.let {
            savedInstanceState.putInt(BUNDLE_LAST_LONG_PRESS, it)
        }
        return savedInstanceState
    }

    /**
     * restore the index of the last long pressed index
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
            mLastLongPressIndex = savedInstanceState.getInt(BUNDLE_LAST_LONG_PRESS + prefix)
        return this
    }

    companion object {

        protected const val BUNDLE_LAST_LONG_PRESS = "bundle_last_long_press"
    }
}