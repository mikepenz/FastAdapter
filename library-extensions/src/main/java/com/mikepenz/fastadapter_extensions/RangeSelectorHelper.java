package com.mikepenz.fastadapter_extensions;

import android.os.Bundle;

import com.mikepenz.fastadapter.IExpandable;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.mikepenz.fastadapter_extensions.utilities.SubItemUtil;

/**
 * Created by Michael on 15.09.2016.
 */
public class RangeSelectorHelper {

    protected static final String BUNDLE_LAST_LONG_PRESS = "bundle_last_long_press";

    private FastItemAdapter mFastAdapter;
    private ActionModeHelper mActionModeHelper;
    private boolean mSupportSubItems = false;
    private Object mPayload = null;

    private Integer mLastLongPressIndex;

    public RangeSelectorHelper(FastItemAdapter adapter) {
        mFastAdapter = adapter;
    }

    /**
     * set the ActionModeHelper, if you want to notify it after a range was selected
     * so that it can update the ActionMode title
     *
     * @param actionModeHelper the action mode helper that should be used
     * @return this, for supporting function call chaining
     */
    public RangeSelectorHelper withActionModeHelper(ActionModeHelper actionModeHelper) {
        mActionModeHelper = actionModeHelper;
        return this;
    }

    /**
     * enable this, if you want the range selector to correclty handle sub items as well
     *
     * @param supportSubItems true, if sub items are supported, false otherwise
     * @return this, for supporting function call chaining
     */
    public RangeSelectorHelper withSupportSubItems(boolean supportSubItems) {
        this.mSupportSubItems = supportSubItems;
        return this;
    }

    /**
     * the provided payload will be passed to the adapters notify function, if one is provided
     *
     * @param payload the paylaod that should be passed to the adapter on selection state change
     * @return this, for supporting function call chaining
     */
    public RangeSelectorHelper withPayload(Object payload) {
        mPayload = payload;
        return this;
    }

    /**
     * resets the last long pressed index, we only want to respect two consecutive long clicks for selecting a range of items
     */
    public void onClick() {
        reset();
    }

    /**
     * resets the last long pressed index, we only want to respect two consecutive long presses for selecting a range of items
     */
    public void reset() {
        mLastLongPressIndex = null;
    }

    /**
     * will take care to save the long pressed index
     * or to select all items in the range between the current long pressed item and the last long pressed item
     *
     * @param index the index of the long pressed item
     * @return true, if the long press was handled
     */
    public boolean onLongClick(int index) {
        return onLongClick(index, true);
    }

    /**
     * will take care to save the long pressed index
     * or to select all items in the range between the current long pressed item and the last long pressed item
     *
     * @param index the index of the long pressed item
     * @param selectItem true, if the item at the index should be selected, false if this was already done outside of this helper or is not desired
     * @return true, if the long press was handled
     */
    public boolean onLongClick(int index, boolean selectItem) {
        if (mLastLongPressIndex == null) {
            // we only consider long presses on not selected items
            if (mFastAdapter.getAdapterItem(index).isSelectable()) {
                mLastLongPressIndex = index;
                // we select this item as well
                if (selectItem)
                    mFastAdapter.select(index);
                if (mActionModeHelper != null)
                    mActionModeHelper.checkActionMode(null); // works with null as well, as the ActionMode is active for sure!
                return true;
            }
        } else if (mLastLongPressIndex != index) {
            // select all items in the range between the two long clicks
            selectRange(mLastLongPressIndex, index, true);
            // reset the index
            mLastLongPressIndex = null;
        }
        return false;
    }

    /**
     * selects all items in a range, from and to indizes are inclusive
     *
     * @param from the from index
     * @param to the to index
     * @param select true, if the provided range should be selected, false otherwise
     */
    public <T extends IItem & IExpandable> void selectRange(int from, int to, boolean select) {
        selectRange(from, to, select, false);
    }

    /**
     * selects all items in a range, from and to indizes are inclusive
     *
     * @param from the from index
     * @param to the to index
     * @param select true, if the provided range should be selected, false otherwise
     * @param skipHeaders true, if you do not want to process headers, false otherwise
     */
    public <T extends IItem & IExpandable> void selectRange(int from, int to, boolean select, boolean skipHeaders) {
        if (from > to) {
            int temp = from;
            from = to;
            to = temp;
        }

        IItem item;
        for (int i = from; i <= to; i++) {
            item = mFastAdapter.getAdapterItem(i);
            if (item.isSelectable()) {
                if (select) {
                    mFastAdapter.select(i);
                } else {
                    mFastAdapter.deselect(i);
                }
            }
            if (mSupportSubItems && !skipHeaders) {
                // if a group is collapsed, select all sub items
                if (item instanceof IExpandable && !((IExpandable)item).isExpanded()) {
                    SubItemUtil.selectAllSubItems(mFastAdapter, (T) mFastAdapter.getAdapterItem(i), select, true, mPayload);
                }
            }
        }

        if (mActionModeHelper != null) {
            mActionModeHelper.checkActionMode(null); // works with null as well, as the ActionMode is active for sure!
        }
    }

    /**
     * add the values to the bundle for saveInstanceState
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down then this Bundle contains the data it most
     *                           recently supplied in Note: Otherwise it is null.
     * @return the passed bundle with the newly added data
     */
    public Bundle saveInstanceState(Bundle savedInstanceState) {
        return saveInstanceState(savedInstanceState, "");
    }

    /**
     * add the values to the bundle for saveInstanceState
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down then this Bundle contains the data it most
     *                           recently supplied in Note: Otherwise it is null.
     * @param prefix             a prefix added to the savedInstance key so we can store multiple states
     * @return the passed bundle with the newly added data
     */
    public Bundle saveInstanceState(Bundle savedInstanceState, String prefix) {
        if (savedInstanceState != null && mLastLongPressIndex != null)
            savedInstanceState.putInt(BUNDLE_LAST_LONG_PRESS, mLastLongPressIndex);
        return savedInstanceState;
    }

    /**
     * restore the index of the last long pressed index
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down then this Bundle contains the data it most
     *                           recently supplied in Note: Otherwise it is null.
     * @return this
     */
    public RangeSelectorHelper withSavedInstanceState(Bundle savedInstanceState) {
        return withSavedInstanceState(savedInstanceState, "");
    }

    /**
     * restore the index of the last long pressed index
     * IMPORTANT! Call this method only after all items where added to the adapters again. Otherwise it may select wrong items!
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down then this Bundle contains the data it most
     *                           recently supplied in Note: Otherwise it is null.
     * @param prefix             a prefix added to the savedInstance key so we can store multiple states
     * @return this
     */
    public RangeSelectorHelper withSavedInstanceState(Bundle savedInstanceState, String prefix) {
        if (savedInstanceState != null && savedInstanceState.containsKey(BUNDLE_LAST_LONG_PRESS + prefix))
            mLastLongPressIndex = savedInstanceState.getInt(BUNDLE_LAST_LONG_PRESS + prefix);
        return this;
    }
}
