package com.mikepenz.fastadapter_extensions;


import android.support.annotation.MenuRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.expandable.ExpandableExtension;
import com.mikepenz.fastadapter_extensions.utilities.SubItemUtil;

/**
 * Created by mikepenz on 02.01.16.
 */
public class ActionModeHelper {
    private FastAdapter mFastAdapter;

    @MenuRes
    private int mCabMenu;

    private ActionMode.Callback mInternalCallback;
    private ActionMode.Callback mCallback;
    private ActionMode mActionMode;
    private ExpandableExtension mExpandableExtension = null;

    private boolean mAutoDeselect = true;

    private ActionModeTitleProvider mTitleProvider;

    public ActionModeHelper(FastAdapter fastAdapter, int cabMenu) {
        this.mFastAdapter = fastAdapter;
        this.mCabMenu = cabMenu;
        this.mInternalCallback = new ActionBarCallBack();
    }

    public ActionModeHelper(FastAdapter fastAdapter, int cabMenu, ActionMode.Callback callback) {
        this.mFastAdapter = fastAdapter;
        this.mCabMenu = cabMenu;
        this.mCallback = callback;
        this.mInternalCallback = new ActionBarCallBack();
    }

    public ActionModeHelper withTitleProvider(ActionModeTitleProvider titleProvider) {
        this.mTitleProvider = titleProvider;
        return this;
    }

    public ActionModeHelper withAutoDeselect(boolean enabled) {
        this.mAutoDeselect = enabled;
        return this;
    }

    public ActionModeHelper withSupportSubItems(ExpandableExtension expandableExtension) {
        this.mExpandableExtension = expandableExtension;
        return this;
    }

    public ActionMode getActionMode() {
        return mActionMode;
    }

    /**
     * convenient method to check if action mode is active or nor
     *
     * @return true, if ActionMode is active, false otherwise
     */
    public boolean isActive() {
        return mActionMode != null;
    }

    /**
     * implements the basic behavior of a CAB and multi select behavior,
     * including logics if the clicked item is collapsible
     *
     * @param item the current item
     * @return null if nothing was done, or a boolean to inform if the event was consumed
     */
    public Boolean onClick(IItem item) {
        return onClick(null, item);
    }

    /**
     * implements the basic behavior of a CAB and multi select behavior,
     * including logics if the clicked item is collapsible
     *
     * @param act  the current Activity
     * @param item the current item
     * @return null if nothing was done, or a boolean to inform if the event was consumed
     */
    public Boolean onClick(AppCompatActivity act, IItem item) {
        //if we are current in CAB mode, and we remove the last selection, we want to finish the actionMode
        if (mActionMode != null && (mExpandableExtension != null ? SubItemUtil.getSelectedItems(mFastAdapter).size() == 1 : mFastAdapter.getSelections().size() == 1) && item.isSelected()) {
            mActionMode.finish();
            mFastAdapter.deselect();
            return true;
        }

        if (mActionMode != null) {
            // calculate the selection count for the action mode
            // because current selection is not reflecting the future state yet!
            int selected = mExpandableExtension != null ? SubItemUtil.getSelectedItems(mFastAdapter).size() : mFastAdapter.getSelections().size();
            if (item.isSelected())
                selected--;
            else if (item.isSelectable())
                selected++;
            checkActionMode(act, selected);
        }

        return null;
    }

    /**
     * implements the basic behavior of a CAB and multi select behavior onLongClick
     *
     * @param act      the current Activity
     * @param position the position of the clicked item
     * @return the initialized ActionMode or null if nothing was done
     */
    public ActionMode onLongClick(AppCompatActivity act, int position) {
        if (mActionMode == null && mFastAdapter.getItem(position).isSelectable()) {
            //may check if actionMode is already displayed
            mActionMode = act.startSupportActionMode(mInternalCallback);
            //we have to select this on our own as we will consume the event
            mFastAdapter.select(position);
            // update title
            checkActionMode(act, 1);
            //we consume this event so the normal onClick isn't called anymore
            return mActionMode;
        }
        return mActionMode;
    }

    /**
     * check if the ActionMode should be shown or not depending on the currently selected items
     * Additionally, it will also update the title in the CAB for you
     *
     * @param act the current Activity
     * @return the initialized ActionMode or null if no ActionMode is active after calling this function
     */
    public ActionMode checkActionMode(AppCompatActivity act) {
        int selected = mExpandableExtension != null ? SubItemUtil.getSelectedItems(mFastAdapter).size() : mFastAdapter.getSelections().size();
        return checkActionMode(act, selected);
    }

    /**
     * reset any active action mode if it is active, useful, to avoid leaking the activity if this helper class is retained
     */
    public void reset() {
        if (mActionMode != null) {
            mActionMode.finish();
            mActionMode = null;
        }
    }

    private ActionMode checkActionMode(AppCompatActivity act, int selected) {
        if (selected == 0) {
            if (mActionMode != null) {
                mActionMode.finish();
                mActionMode = null;
            }
        } else if (mActionMode == null) {
            if (act != null) // without an activity, we cannot start the action mode
                mActionMode = act.startSupportActionMode(mInternalCallback);
        }
        updateTitle(selected);
        return mActionMode;
    }

    /**
     * updates the title to reflect the current selected items or to show a user defined title
     *
     * @param selected number of selected items
     */
    private void updateTitle(int selected) {
        if (mActionMode != null) {
            if (mTitleProvider != null)
                mActionMode.setTitle(mTitleProvider.getTitle(selected));
            else
                mActionMode.setTitle(String.valueOf(selected));
        }
    }

    /**
     * Our ActionBarCallBack to showcase the CAB
     */
    private class ActionBarCallBack implements ActionMode.Callback {

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            boolean consumed = false;
            if (mCallback != null) {
                consumed = mCallback.onActionItemClicked(mode, item);
            }

            if (!consumed) {
                if (mExpandableExtension != null) {
                    SubItemUtil.deleteSelected(mFastAdapter, mExpandableExtension, true, false);
                }
                else {
                    mFastAdapter.deleteAllSelectedItems();
                }
                //finish the actionMode
                mode.finish();
            }
            return consumed;
        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(mCabMenu, menu);

            //as we are now in the actionMode a single click is fine for multiSelection
            mFastAdapter.withSelectOnLongClick(false);

            return mCallback == null || mCallback.onCreateActionMode(mode, menu);
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;

            //after we are done with the actionMode we fallback to longClick for multiselect
            mFastAdapter.withSelectOnLongClick(true);

            //actionMode end. deselect everything
            if (mAutoDeselect)
                mFastAdapter.deselect();

            if (mCallback != null) {
                //we notify the provided callback
                mCallback.onDestroyActionMode(mode);
            }
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return mCallback != null && mCallback.onPrepareActionMode(mode, menu);
        }
    }

    // --------------------------
    // Interfaces
    // --------------------------

    public interface ActionModeTitleProvider {
        String getTitle(int selected);
    }
}
