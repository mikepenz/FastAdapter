package com.mikepenz.fastadapter;

/**
 * Created by mikepenz on 03.02.15.
 */
public interface IIdentifyable<T> {
    /**
     * set the identifier of this item
     *
     * @param identifier
     * @return
     */
    T withIdentifier(int identifier);

    /**
     * returns the identifier of this item
     *
     * @return
     */
    int getIdentifier();
}
