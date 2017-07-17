package com.mikepenz.fastadapter.utils;

import com.mikepenz.fastadapter.IIdentifyable;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by mikepenz on 19.09.15.
 */
public class DefaultIdDistributorImpl<Identifiable extends IIdentifyable> extends DefaultIdDistributor<Identifiable> {

    private final AtomicLong idDistributor = new AtomicLong(-2L);

    @Override
    public long nextId(Identifiable identifiable) {
        return idDistributor.decrementAndGet();
    }
}
