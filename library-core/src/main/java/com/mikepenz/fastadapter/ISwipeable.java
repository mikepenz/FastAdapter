package com.mikepenz.fastadapter;

/**
 * Created by mikepenz on 30.12.15.
 */
public interface ISwipeable<T, Item extends IItem> {
    /**
     * @return true if swipeable
     */
    boolean isSwipeable();

    /**
     * use this method to set if item is swipeable
     *
     * @param swipeable true if swipeable
     * @return this
     */
    T withIsSwipeable(boolean swipeable);
}
