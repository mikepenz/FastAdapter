package com.mikepenz.fastadapter.utils;

import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Created by mikepenz on 31.12.15.
 */
public class AdapterUtil {

    /**
     * internal method to handle the selections if items are added / removed
     *
     * @param positions     the positions map which should be adjusted
     * @param startPosition the global index of the first element modified
     * @param endPosition   the global index up to which the modification changed the indices (should be MAX_INT if we check til the end)
     * @param adjustBy      the value by which the data was shifted
     * @return the adjusted set
     */
    public static Set<Integer> adjustPosition(Set<Integer> positions, int startPosition, int endPosition, int adjustBy) {
        Set<Integer> newPositions = new TreeSet<>();

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
    public static SortedMap<Integer, Integer> adjustPosition(Map<Integer, Integer> positions, int startPosition, int endPosition, int adjustBy) {
        SortedMap<Integer, Integer> newPositions = new TreeMap<>();

        for (Map.Entry<Integer, Integer> entry : positions.entrySet()) {
            int position = entry.getKey();

            //if our current position is not within the bounds to check for we can add it
            if (position < startPosition || position > endPosition) {
                newPositions.put(position, entry.getValue());
            } else if (adjustBy > 0) {
                //if we added items and we are within the bounds we can simply add the adjustBy to our entry
                newPositions.put(position + adjustBy, entry.getValue());
            } else if (adjustBy < 0) {
                //if we removed items and we are within the bounds we have to check if the item was removed
                //adjustBy is negative in this case
                if (position > startPosition + adjustBy && position <= startPosition) {
                    ;//we are within the removed items range we don't add this item anymore
                } else {
                    //otherwise we adjust our position
                    newPositions.put(position + adjustBy, entry.getValue());
                }
            }
        }

        return newPositions;
    }
}
