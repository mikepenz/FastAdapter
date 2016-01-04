package com.mikepenz.fastadapter.helpers;

import android.support.design.widget.Snackbar;
import android.view.View;

import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.IItemAdapter;

import java.util.ArrayList;

/**
 * Created by mikepenz on 04.01.16.
 */
public class UndoHelper {
    private static final int ACTION_REMOVE = 2;

    private IItemAdapter mItemAdapter;
    private UndoListener mUndoListener;
    private History mHistory = null;

    /**
     * Constructor to create the UndoHelper
     *
     * @param itemAdapter  the itemAdapter which holds the items which are removed
     * @param undoListener the listener which gets called when an item was really removed
     */
    public UndoHelper(IItemAdapter itemAdapter, UndoListener undoListener) {
        this.mItemAdapter = itemAdapter;
        this.mUndoListener = undoListener;
    }

    /**
     * removes items from the ItemAdapter
     *
     * @param view       the view which will host the SnackBar
     * @param text       the text to show on the SnackBar
     * @param actionText the text to show for the Undo Action
     * @param position   the position where the item was removed
     * @param itemCount  the amount of items which were removed at the given position
     */
    public void remove(View view, String text, String actionText, int position, int itemCount) {
        if (mHistory != null) {
            notifyCommit();
        }

        History history = new History();
        history.position = position;
        history.action = ACTION_REMOVE;
        for (int i = position; i < position + itemCount; i++) {
            history.items.add(mItemAdapter.getAdapterItem(i));
        }
        mHistory = history;

        Snackbar.make(view, text, Snackbar.LENGTH_LONG).setCallback(new Snackbar.Callback() {
            @Override
            public void onDismissed(Snackbar snackbar, int event) {
                super.onDismissed(snackbar, event);

                switch (event) {
                    case Snackbar.Callback.DISMISS_EVENT_ACTION:
                        //we can ignore it
                        break;
                    case Snackbar.Callback.DISMISS_EVENT_TIMEOUT:
                        notifyCommit();
                        break;
                }
            }

            @Override
            public void onShown(Snackbar snackbar) {
                super.onShown(snackbar);
                doChange();
            }
        }).setAction(actionText, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                undoChange();
            }
        }).show();
    }

    private void notifyCommit() {
        if (mHistory.action == ACTION_REMOVE) {
            mUndoListener.commitRemove(mHistory.position, mHistory.items);
            mHistory = null;
        }
    }

    private void doChange() {
        if (mHistory.action == ACTION_REMOVE) {
            if (mHistory.items.size() == 1) {
                mItemAdapter.remove(mHistory.position);
            } else {
                mItemAdapter.removeItemRange(mHistory.position, mHistory.items.size());
            }
        }
    }

    private void undoChange() {
        if (mHistory.action == ACTION_REMOVE) {
            if (mHistory.items.size() == 1) {
                mItemAdapter.add(mHistory.position, mHistory.items.get(0));
            } else {
                mItemAdapter.add(mHistory.position, mHistory.items);
            }
        }
        mHistory = null;
    }

    public interface UndoListener {
        void commitRemove(int position, ArrayList<IItem> removed);
    }

    private class History {
        public int action;
        public int position;
        public ArrayList<IItem> items = new ArrayList<>();
    }
}
