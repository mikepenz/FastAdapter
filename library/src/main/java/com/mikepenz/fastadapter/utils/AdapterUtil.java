package com.mikepenz.fastadapter.utils;

import android.util.SparseIntArray;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IExpandable;
import com.mikepenz.fastadapter.IItem;

import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Created by mikepenz on 31.12.15.
 */
public class AdapterUtil {

    /**
     * internal method which correctly set the selected state and expandable state on the newly added items
     *
     * @param fastAdapter   the fastAdapter which manages everything
     * @param startPosition the position of the first item to handle
     * @param endPosition   the position of the last item to handle
     */
    public static void handleStates(FastAdapter fastAdapter, int startPosition, int endPosition) {
        for (int i = endPosition; i >= startPosition; i--) {
            IItem updateItem = fastAdapter.getItem(i);
            if (updateItem.isSelected()) {
                fastAdapter.getSelections().add(i);
            } else if (fastAdapter.getSelections().contains(i)) {
                fastAdapter.getSelections().remove(i);
            }
            if (updateItem instanceof IExpandable) {
                if (((IExpandable) updateItem).isExpanded() && fastAdapter.getExpanded().indexOfKey(i) < 0) {
                    fastAdapter.expand(i);
                }
            }
        }
    }

    /**
     * internal method to handle the selections if items are added / removed
     *
     * @param positions     the positions map which should be adjusted
     * @param startPosition the global index of the first element modified
     * @param endPosition   the global index up to which the modification changed the indices (should be MAX_INT if we check til the end)
     * @param adjustBy      the value by which the data was shifted
     * @return the adjusted set
     */
    public static SortedSet<Integer> adjustPosition(Set<Integer> positions, int startPosition, int endPosition, int adjustBy) {
        SortedSet<Integer> newPositions = new TreeSet<>();

        for (Integer entry : positions) {
            int position = entry;

            //if our current position is not within the bounds to check for we can add it
            if (position < startPosition || position > endPosition) {
                newPositions.add(position);
            } else if (adjustBy > 0) {
                //if we added items and we are within the bounds we can simply add the adjustBy to our entry
                newPositions.add(position + adjustBy);
            } else if (adjustBy < 0) {
                //if we removed items and we are within the bounds we have to check if the item was removed
                //adjustBy is negative in this case
                if (position > startPosition + adjustBy && position <= startPosition) {
                    ;//we are within the removed items range we don't add this item anymore
                } else {
                    //otherwise we adjust our position
                    newPositions.add(position + adjustBy);
                }
            }
        }

        return newPositions;
    }

    /**
     * internal method to handle the selections if items are added / removed
     *
     * @param positions     the positions map which should be adjusted
     * @param startPosition the global index of the first element modified
     * @param endPosition   the global index up to which the modification changed the indices (should be MAX_INT if we check til the end)
     * @param adjustBy      the value by which the data was shifted
     * @return the adjusted map
     */
    public static SparseIntArray adjustPosition(SparseIntArray positions, int startPosition, int endPosition, int adjustBy) {
        SparseIntArray newPositions = new SparseIntArray();

        int length = positions.size();
        for (int i = 0; i < length; i++) {
            int position = positions.keyAt(i);

            //if our current position is not within the bounds to check for we can add it
            if (position < startPosition || position > endPosition) {
                newPositions.put(position, positions.valueAt(i));
            } else if (adjustBy > 0) {
                //if we added items and we are within the bounds we can simply add the adjustBy to our entry
                newPositions.put(position + adjustBy, positions.valueAt(i));
            } else if (adjustBy < 0) {
                //if we removed items and we are within the bounds we have to check if the item was removed
                //adjustBy is negative in this case
                if (position > startPosition + adjustBy && position <= startPosition) {
                    ;//we are within the removed items range we don't add this item anymore
                } else {
                    //otherwise we adjust our position
                    newPositions.put(position + adjustBy, positions.valueAt(i));
                }
            }
        }

        return newPositions;
    }
}
