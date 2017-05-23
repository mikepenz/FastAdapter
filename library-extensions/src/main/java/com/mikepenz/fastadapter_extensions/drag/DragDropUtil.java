package com.mikepenz.fastadapter_extensions.drag;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;

import java.util.Collections;

/**
 * Created by flisar on 23.05.2017.
 */

public class DragDropUtil
{
    /*
     * This functions handles the default drag and drop move event
     * It takes care to move all items one by one within the passed in positions
     *
     * @param fastAdapter the adapter
     * @param oldPosition the start position of the move
     * @param newPosition the end position of the move
     */
    public static void onMove(FastItemAdapter fastItemAdapter, int oldPosition, int newPosition) {
        // necessary, because the positions passed to this function may be jumping in case of that the recycler view is scrolled while holding an item outside of the recycler view
        if (oldPosition < newPosition) {
            for (int i = oldPosition; i < newPosition; i++) {
                Collections.swap(fastItemAdapter.getAdapterItems(), i, i + 1);
                fastItemAdapter.notifyAdapterItemMoved(i, i + 1);
            }
        } else {
            for (int i = newPosition; i < oldPosition; i++) {
                Collections.swap(fastItemAdapter.getAdapterItems(), i, i + 1);
                fastItemAdapter.notifyAdapterItemMoved(i, i + 1);
            }
        }
    }
}
