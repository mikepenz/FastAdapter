package com.mikepenz.fastadapter_extensions;

import android.support.design.widget.Snackbar;
import android.view.View;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.IItemAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Created by mikepenz on 04.01.16.
 */
public class UndoHelper<Item extends IItem> {
    private static final int ACTION_REMOVE = 2;

    private FastAdapter<Item> mAdapter;
    private UndoListener mUndoListener;
    private History mHistory = null;

    /**
     * Constructor to create the UndoHelper
     *
     * @param adapter      the root FastAdapter
     * @param undoListener the listener which gets called when an item was really removed
     */
    public UndoHelper(FastAdapter<Item> adapter, UndoListener undoListener) {
        this.mAdapter = adapter;
        this.mUndoListener = undoListener;
    }

    /**
     * removes items from the ItemAdapter
     *
     * @param view       the view which will host the SnackBar
     * @param text       the text to show on the SnackBar
     * @param actionText the text to show for the Undo Action
     * @param positions  the positions where the items were removed
     * @return the generated Snackbar
     */
    public Snackbar remove(final View view, final String text, final String actionText, @Snackbar.Duration int duration, final Set<Integer> positions) {
        if (mHistory != null) {
            notifyCommit();
        }

        History history = new History();
        history.action = ACTION_REMOVE;
        for (int position : positions) {
            history.items.add(mAdapter.getRelativeInfo(position));
        }
        Collections.sort(history.items, new Comparator<FastAdapter.RelativeInfo<Item>>() {
            @Override
            public int compare(FastAdapter.RelativeInfo<Item> lhs, FastAdapter.RelativeInfo<Item> rhs) {
                return Integer.valueOf(lhs.position).compareTo(rhs.position);
            }
        });

        mHistory = history;

        Snackbar snackbar = Snackbar.make(view, text, duration).setCallback(new Snackbar.Callback() {
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
        });
        snackbar.show();
        return snackbar;
    }

    private void notifyCommit() {
        if (mHistory != null) {
            if (mHistory.action == ACTION_REMOVE) {
                SortedSet<Integer> positions = new TreeSet<>(new Comparator<Integer>() {
                    @Override
                    public int compare(Integer lhs, Integer rhs) {
                        return lhs.compareTo(rhs);
                    }
                });
                for (FastAdapter.RelativeInfo<Item> relativeInfo : mHistory.items) {
                    positions.add(relativeInfo.position);
                }

                mUndoListener.commitRemove(positions, mHistory.items);
                mHistory = null;
            }
        }
    }

    private void doChange() {
        if (mHistory != null) {
            if (mHistory.action == ACTION_REMOVE) {
                for (int i = mHistory.items.size() - 1; i >= 0; i--) {
                    FastAdapter.RelativeInfo<Item> relativeInfo = mHistory.items.get(i);
                    if (relativeInfo.adapter instanceof IItemAdapter) {
                        ((IItemAdapter) relativeInfo.adapter).remove(relativeInfo.position);
                    }
                }
            }
        }
    }

    private void undoChange() {
        if (mHistory != null) {
            if (mHistory.action == ACTION_REMOVE) {
                for (int i = 0; i < mHistory.items.size(); i++) {
                    FastAdapter.RelativeInfo<Item> relativeInfo = mHistory.items.get(i);
                    if (relativeInfo.adapter instanceof IItemAdapter) {
                        IItemAdapter<Item> adapter = (IItemAdapter<Item>) relativeInfo.adapter;
                        adapter.add(relativeInfo.position, relativeInfo.item);
                        if (relativeInfo.item.isSelected()) {
                            mAdapter.select(relativeInfo.position);
                        }
                    }
                }
            }
        }
        mHistory = null;
    }

    public interface UndoListener<Item extends IItem> {
        void commitRemove(Set<Integer> positions, ArrayList<FastAdapter.RelativeInfo<Item>> removed);
    }

    private class History {
        public int action;
        public ArrayList<FastAdapter.RelativeInfo<Item>> items = new ArrayList<>();
    }
}
