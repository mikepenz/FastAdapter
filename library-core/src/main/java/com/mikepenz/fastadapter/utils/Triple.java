package com.mikepenz.fastadapter.utils;

import android.support.annotation.Nullable;

/**
 * a helper class to provide a triple class, to store 3 values and return them from a method
 *
 * @param <T>
 * @param <U>
 * @param <V>
 */
public class Triple<T, U, V> {
    public final T first;
    @Nullable
    public final U second;
    @Nullable
    public final V third;

    public Triple(T first, @Nullable U second, @Nullable V third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }
}