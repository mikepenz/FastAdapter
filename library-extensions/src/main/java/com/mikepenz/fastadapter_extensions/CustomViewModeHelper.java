package com.mikepenz.fastadapter_extensions;

import android.os.Bundle;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IItem;

/**
 * Created by tvelo on 7/7/2017.
 * based on extension from mikepenz. 
 * intentded to be used with custom view (button or dialog that displays
 * on multi-select mode activation. Implement interface in your fragment
 * or activity and use the onCreateSelectionView() to notify of needed
 * action view. OnSelectionChanged notifies of selection changes.
 * OnDestroySelectionView notifies of deselect. 
 * NOTE it is recommended to:
 * - Use the helper's methods for deselect and show / hide calls for the
 *    custom view. 
 * - Use the count from the OnSelectionChanged method, as the timing of
 *    onClick \ onLongCLick are pre-selection so the FastAdapter won't 
 *    know about the new selection, but the OnSelectionChanged will.
 * TODO: Wire-in sub-selections for expandables.
 */

public class CustomViewModeHelper {
    private static final String ACTION_VIEW_STATE = "VIEW_MODE_ACTION_VIEW_STATE";
    private static final String FIRST_POS_STATE = "VIEW_MODE_FIRST_POS_STATE";
    private static final String RANGE_ENABLED_STATE = "VIEW_MODE_RANGE_ENABLED_STATE";
    private static final String AUTO_DESELECT_ENABLED_STATE = "VIEW_MODE_AUTO_DESELECT_ENABLED_STATE";
    private static final String NEW_RANGE_ON_SELECT_ENABLED_STATE = "VIEW_MODE_NEW_RANGE_ON_SELECT_ENABLED_STATE";
    private final ViewModeInteractionListener viewModeInteractionListener;
    private FastAdapter mFastAdapter;
    private boolean actionViewEnabled = false;
    private boolean mAutoDeselect;
    private boolean rangeEnabled;
    private Integer firstLongClickPos;
    private boolean newRangeOnSelection;

    /**
     * @param fastAdapter                 current fastAdapter
     * @param viewModeInteractionListener listened to trigger custom view display
     */
    public ViewModeHelper(FastAdapter fastAdapter, ViewModeInteractionListener viewModeInteractionListener) {
        this.mFastAdapter = fastAdapter;
        checkActionMode(fastAdapter.getSelectedItems().size());
        this.viewModeInteractionListener = viewModeInteractionListener;
    }

    /**
     * @param savedInstanceState generated instance state from activity or fragment
     * @return self for builder
     */
    public ViewModeHelper withInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (savedInstanceState.get(ACTION_VIEW_STATE) != null)
                actionViewEnabled = savedInstanceState.getBoolean(ACTION_VIEW_STATE, false);
            if (savedInstanceState.get(FIRST_POS_STATE) != null)
                firstLongClickPos = savedInstanceState.getInt(FIRST_POS_STATE);
            if (savedInstanceState.get(RANGE_ENABLED_STATE) != null)
                rangeEnabled = savedInstanceState.getBoolean(RANGE_ENABLED_STATE, false);
            if (savedInstanceState.get(AUTO_DESELECT_ENABLED_STATE) != null)
                mAutoDeselect = savedInstanceState.getBoolean(AUTO_DESELECT_ENABLED_STATE, false);
            if (savedInstanceState.get(NEW_RANGE_ON_SELECT_ENABLED_STATE) != null)
                newRangeOnSelection = savedInstanceState.getBoolean(NEW_RANGE_ON_SELECT_ENABLED_STATE, false);
        }
        return this;
    }

    /**
     * @param outState Activity or fragment state
     * @return new built state
     */
    public Bundle saveInstanceState(Bundle outState) {
        outState.putBoolean(ACTION_VIEW_STATE, actionViewEnabled);
        if (firstLongClickPos != null)
            outState.putInt(FIRST_POS_STATE, firstLongClickPos);
        outState.putBoolean(AUTO_DESELECT_ENABLED_STATE, mAutoDeselect);
        outState.putBoolean(RANGE_ENABLED_STATE, rangeEnabled);
        outState.putBoolean(NEW_RANGE_ON_SELECT_ENABLED_STATE, newRangeOnSelection);
        return outState;
    }

    /**
     * @param autoDeselect auto-deselect on final
     * @return self for builder
     */
    public ViewModeHelper withAutoDeselect(boolean autoDeselect) {
        this.mAutoDeselect = autoDeselect;
        return this;
    }

    /**
     * @param rangeEnabled enable range selection mode
     * @return self for builder
     */
    public ViewModeHelper withRangeEnabled(boolean rangeEnabled) {
        this.rangeEnabled = rangeEnabled;
        return this;
    }

    /**
     * allow groups of ranges to be selected for each "set" of two long presses
     *
     * @param newRangeOnSelection enable multi-tiered range selection
     * @return self for builder
     */
    public ViewModeHelper withNewRangeOnSelection(boolean newRangeOnSelection) {
        this.newRangeOnSelection = newRangeOnSelection;
        return this;
    }

    /**
     * @param item the current item
     * @return null if nothing was done, or a boolean to inform if the event was consumed
     */
    public Boolean onClick(IItem item) {
        if (mFastAdapter != null) {
            if (actionViewEnabled && mFastAdapter.getSelections().size() == 1 && item.isSelected()) {
                finish();
                return true;
            }
            if (actionViewEnabled) {
                // calculate the selection count for the action mode
                int selected = mFastAdapter.getSelections().size();
                if (item.isSelected())
                    selected--;
                else if (item.isSelectable())
                    selected++;
                checkActionMode(selected);
            }
        }
        return null;
    }

    /**
     * @param position the position of the clicked item
     * @return the consumption of longClick, null if nothing happened from us.
     */
    public Boolean onLongClick(int position) {
        if (!actionViewEnabled && mFastAdapter.getItem(position).isSelectable()) {
            firstLongClickPos = position;
            //we have to select this on our own as we will consume the event
            if (mFastAdapter != null)
                mFastAdapter.select(position);
            // init action view
            //we consume this event so the normal onClick isn't called anymore
            return checkActionMode(1);
        } else if (rangeEnabled && actionViewEnabled && mFastAdapter.getItem(position).isSelectable()) {
            if (firstLongClickPos != null) {
                selectRange(position, firstLongClickPos, true);
                if (newRangeOnSelection)
                    firstLongClickPos = null;
                return checkActionMode(mFastAdapter.getSelections().size());
            } else {
                firstLongClickPos = position;
                if (mFastAdapter != null)
                    mFastAdapter.select(position);
            }
        }
        return null;
    }

    /**
     * @param from   start range
     * @param to     end range
     * @param select select or deselect
     */
    private void selectRange(int from, int to, boolean select) {
        if (from == to)
            return;
        if (from > to) {
            int temp = from;
            from = to;
            to = temp;
        }
        for (int i = from; i <= to; i++) {
            if (mFastAdapter.getItem(i).isSelectable()) {
                if (select && mFastAdapter != null)
                    mFastAdapter.select(i);
                else if (mFastAdapter != null)
                    mFastAdapter.deselect(i);
            }
        }
    }

    /**
     * @param selected number of selected recrods
     * @return boolean as the action mode is enabled.
     */
    private boolean checkActionMode(int selected) {
        if (selected == 0) {
            if (actionViewEnabled)
                finish();
        } else if (!actionViewEnabled)
            showActionView();
        else if (viewModeInteractionListener != null)
            viewModeInteractionListener.onSelectionsChanged(selected);
        return actionViewEnabled;
    }

    /**
     * designate mode as multi-mode and do the needful
     */
    private void showActionView() {
        actionViewEnabled = true;
        //as we are now in the actionMode a single click is fine for multiSelection
        if (mFastAdapter != null)
            mFastAdapter.withSelectOnLongClick(false);
        if (viewModeInteractionListener != null)
            viewModeInteractionListener.onCreateSelectionView();
    }

    /**
     * mark the helper as done, deselect all, and close all custom views.
     */
    private void finish() {
        actionViewEnabled = false;
        //after we are done with the actionMode we fallback to longClick for multi-select
        if (mFastAdapter != null)
            mFastAdapter.withSelectOnLongClick(true);
        //actionMode end. deselect everything
        if (mAutoDeselect && mFastAdapter != null) // just in-case
            mFastAdapter.deselect();
        if (viewModeInteractionListener != null)
            viewModeInteractionListener.onDestroySelectionView();
    }

    /**
     * deselect all and close custom view
     */
    public void deselectAll() {
        if (mFastAdapter != null)
            mFastAdapter.deselect();
        finish();
    }

    /**
     * @return the mode of selection tools
     */
    public boolean getSelectMode() {
        return actionViewEnabled;
    }

    /**
     * call onCreateSelectionView to show the custom view of selection tools if possible
     */
    public void showIfNeeded() {
        if (actionViewEnabled && viewModeInteractionListener != null)
            viewModeInteractionListener.onCreateSelectionView();
    }

    /**
     * Call onDestroySelectionView to hide the custom view of selection tools if possible
     */
    public void hideIfPossible() {
        if (actionViewEnabled && viewModeInteractionListener != null)
            viewModeInteractionListener.onDestroySelectionView();
    }

    /**
     * ViewModeInteractionListener handles calls from the ViewModeHelper to update UI changes for
     * Collection
     */
    public interface ViewModeInteractionListener {
        /**
         * called when custom view is needed for multi-select
         */
        void onCreateSelectionView();

        /**
         * called when selection has changed
         *
         * @param count number of selected records
         */
        void onSelectionsChanged(int count);

        /**
         * called when custom view is to be hidden or destroyed
         */
        void onDestroySelectionView();
    }
}
