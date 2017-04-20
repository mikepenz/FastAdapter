package com.mikepenz.fastadapter.utils;

import android.support.annotation.Nullable;

/**
 * Created by Ahmed Ragab on 24/01/16.
 */
public interface Function<Input, Output> {

    @Nullable
    Output apply(Input input);

}
