package com.mikepenz.fastadapter;

import com.mikepenz.fastadapter.utils.DefaultIdDistributorImpl;

import java.util.List;

/**
 * Created by fabianterhorst on 16.07.17.
 */
public interface IIdDistributor<Identifiable extends IIdentifyable> {

    IIdDistributor<? extends IIdentifyable> DEFAULT = new DefaultIdDistributorImpl<>();

    List<Identifiable> checkIds(List<Identifiable> items);

    Identifiable[] checkIds(Identifiable... items);

    Identifiable checkId(Identifiable item);

    long nextId(Identifiable item);
}
