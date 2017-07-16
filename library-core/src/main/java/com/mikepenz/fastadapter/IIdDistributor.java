package com.mikepenz.fastadapter;

import android.support.annotation.NonNull;

import com.mikepenz.fastadapter.utils.DefaultIdDistributorImpl;

import java.util.List;

/**
 * Created by fabianterhorst on 16.07.17.
 */
public interface IIdDistributor<Identifiable extends IIdentifyable> {

    IIdDistributor<? extends IIdentifyable> DEFAULT = new DefaultIdDistributorImpl<>();

    List<Identifiable> checkIds(@NonNull List<Identifiable> items);

    Identifiable[] checkIds(@NonNull Identifiable... items);

    Identifiable checkId(@NonNull Identifiable item);

    long nextId(Identifiable item);
}
