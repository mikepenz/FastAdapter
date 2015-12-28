package com.mikepenz.fastadapter;

/**
 * Created by mikepenz on 03.02.15.
 */
public interface IIdentifyable<T> {
    T withIdentifier(int identifier);

    int getIdentifier();
}
