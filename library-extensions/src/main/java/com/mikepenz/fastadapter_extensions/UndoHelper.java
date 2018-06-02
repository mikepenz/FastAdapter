package com.mikepenz.fastadapter_extensions;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.TextView;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.IItemAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import static java.util.Arrays.asList;

/**
 * Created by mikepenz on 04.01.16.
 */
public class UndoHelper<Item extends IItem> {
    private static final int ACTION_REMOVE = 2;

    private FastAdapter<Item> mAdapter;
    private UndoListener<Item> mUndoListener;
    private History mHistory = null;
    private Snackbar mSnackBar = null;
    private String mSnackbarActionText = "";
    private boolean mAlreadyCommitted;

    private Snackbar.Callback mSnackbarCallback = new Snackbar.Callback() {
        @Override
        public void onShown(Snackbar sb) {
            super.onShown(sb);
            // Reset the flag when a new Snackbar shows up
            mAlreadyCommitted = false;
        }

        @Override
        public void onDismissed(Snackbar transientBottomBar, int event) {
            super.onDismissed(transientBottomBar, event);

            // If the undo button was clicked (DISMISS_EVENT_ACTION) or
            // if a commit was already executed, skip it
            if ((event == Snackbar.Callback.DISMISS_EVENT_ACTION) || mAlreadyCommitted)
                return;

            notifyCommit();
        }
    };

    /**
     * Constructor to create the UndoHelper
     *
     * @param adapter      the root FastAdapter
     * @param undoListener the listener which gets called when an item was really removed
     */
    public UndoHelper(FastAdapter<Item> adapter, UndoListener<Item> undoListener) {
        this.mAdapter = adapter;
        this.mUndoListener = undoListener;
    }

    /**
     * an optional method to add a {@link Snackbar} of your own with custom styling.
     * note that using this method will override your custom action
     *
     * @param snackBar   your own Snackbar
     * @param actionText the text to show for the Undo Action
     */
    public void withSnackBar(@NonNull Snackbar snackBar, String actionText) {
        mSnackBar = snackBar;
        mSnackbarActionText = actionText;

        mSnackBar.addCallback(mSnackbarCallback)
                .setAction(actionText, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                undoChange();
            }
        });
    }

    public @Nullable
    Snackbar getSnackBar() {
        return mSnackBar;
    }

    /**
     * convenience method to be used if you have previously set a {@link Snackbar} with {@link #withSnackBar(Snackbar, String)}
     *
     * @param positions the positions where the items were removed
     * @return the snackbar or null if {@link #withSnackBar(Snackbar, String)} was not previously called
     */
    public @Nullable
    Snackbar remove(Set<Integer> positions) {
        if (mSnackBar == null) {
            return null;
        }

        View snackbarView = mSnackBar.getView();
        TextView snackbarText = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);

        return remove(snackbarView, snackbarText.getText().toString(), mSnackbarActionText, mSnackBar.getDuration(), positions);
    }

    /**
     * removes items from the ItemAdapter.
     * note that the values of "view", "text", "actionText", and "duration"
     * will be ignored if {@link #withSnackBar(Snackbar, String)} was used.
     * if it was not used, a default snackbar will be generated
     *
     * @param view       the view which will host the SnackBar
     * @param text       the text to show on the SnackBar
     * @param actionText the text to show for the Undo Action
     * @param positions  the positions where the items were removed
     * @return the generated Snackbar
     */
    public Snackbar remove(final View view, final String text, final String actionText, @Snackbar.Duration int duration, final Set<Integer> positions) {
        if (mHistory != null) {
            // Set a flag, if remove was called before the Snackbar
            // executed the commit -> Snackbar does not commit the new
            // inserted history
            mAlreadyCommitted = true;
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
        doChange(); // Do not execute when Snackbar shows up, instead change immediately

        mSnackBar = Snackbar.make(view, text, duration).addCallback(mSnackbarCallback);
        mSnackBar.setAction(actionText, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                undoChange();
            }
        });

        mSnackBar.show();
        return mSnackBar;
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
                for (int i = 0, size = mHistory.items.size(); i < size; i++) {
                    FastAdapter.RelativeInfo<Item> relativeInfo = mHistory.items.get(i);
                    if (relativeInfo.adapter instanceof IItemAdapter) {
                        IItemAdapter<?, Item> adapter = (IItemAdapter<?, Item>) relativeInfo.adapter;
                        adapter.addInternal(relativeInfo.position, asList(relativeInfo.item));
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
