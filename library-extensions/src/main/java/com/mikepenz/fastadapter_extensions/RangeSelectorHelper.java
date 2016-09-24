package com.mikepenz.fastadapter_extensions;

import android.os.Bundle;

import com.mikepenz.fastadapter.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.utils.AdapterUtil;

import java.util.ArrayList;

/**
 * Created by Michael on 15.09.2016.
 */
public class RangeSelectorHelper {

    protected static final String BUNDLE_LAST_LONG_PRESS = "bundle_last_long_press";

    private FastItemAdapter mFastAdapter;
    private ActionModeHelper mActionModeHelper;

    private Integer mLastLongPressIndex;

    public RangeSelectorHelper(FastItemAdapter adapter) {
        mFastAdapter = adapter;
    }

    /**
     * set the ActionModeHelper, if want to notify it after a range was selected
     * so that it can update the ActionMode title
     */
    public RangeSelectorHelper withActionModeHelper(ActionModeHelper actionModeHelper) {
        mActionModeHelper = actionModeHelper;
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
        if (mLastLongPressIndex == null) {
            // we only consider long presses on not selected items
            if (mFastAdapter.getAdapterItem(index).isSelectable()) {
                mLastLongPressIndex = index;
                // we select this item as well
                mFastAdapter.select(index);
                if (mActionModeHelper != null)
                    mActionModeHelper.checkActionMode(null); // works with null as well, as the ActionMode is active for sure!
                return true;
            }
        } else if (mLastLongPressIndex != index){
            // select all items in the range between the two long clicks
            selectRange(mLastLongPressIndex, index, true);
            // reset the index
            mLastLongPressIndex = null;
        }
        return false;
    }

    private void selectRange(int from, int to, boolean select) {
        if (from == to)
            return;

        if (from > to) {
            int temp = from;
            from = to;
            to = temp;
        }

        for (int i = from; i <= to; i++) {
            if (mFastAdapter.getAdapterItem(i).isSelectable()) {
                if (select)
                    mFastAdapter.select(i);
                else
                    mFastAdapter.deselect(i);
            }
        }

        if (mActionModeHelper != null)
            mActionModeHelper.checkActionMode(null); // works with null as well, as the ActionMode is active for sure!
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
        if (savedInstanceState != null && savedInstanceState.containsKey(BUNDLE_LAST_LONG_PRESS))
            mLastLongPressIndex = savedInstanceState.getInt(BUNDLE_LAST_LONG_PRESS);
        return this;
    }
}
