package com.mikepenz.fastadapter;

/**
 * Created by mikepenz on 03.02.15.
 */
public interface IIdentifyable<T> {
    /**
     * set the identifier of this item
     *
     * @param identifier the identifier which will be used (also for getItemId() inside the adapter)
     * @return the item
     */
    T withIdentifier(long identifier);

    /**
     * returns the identifier of this item
     * -1 is the default not set state
     *
     * @return the defined identifier
     */
    long getIdentifier();
}
