package com.mikepenz.fastadapter_extensions;


import android.support.annotation.MenuRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IItem;

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

    public ActionMode getActionMode() {
        return mActionMode;
    }

    /**
     * implements the basic behavior of a CAB and multi select behavior,
     * including logics if the clicked item is collapsible
     *
     * @param item the current item
     * @return null if nothing was done, or a boolean to inform if the event was consumed
     */
    public Boolean onClick(IItem item) {
        //if we are current in CAB mode, and we remove the last selection, we want to finish the actionMode
        if (mActionMode != null && mFastAdapter.getSelections().size() == 1 && item.isSelected()) {
            mActionMode.finish();
            return false;
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
            //we consume this event so the normal onClick isn't called anymore
            return mActionMode;
        }
        return mActionMode;
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
                mFastAdapter.deleteAllSelectedItems();
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
}
