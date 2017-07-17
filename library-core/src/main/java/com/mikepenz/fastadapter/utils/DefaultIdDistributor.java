package com.mikepenz.fastadapter.utils;

import com.mikepenz.fastadapter.IIdDistributor;
import com.mikepenz.fastadapter.IIdentifyable;

import java.util.List;

/**
 * Created by mikepenz on 19.09.15.
 */
public abstract class DefaultIdDistributor<Identifiable extends IIdentifyable> implements IIdDistributor<Identifiable> {

    /**
     * set an unique identifier for all items which do not have one set already
     *
     * @param items
     * @return
     */
    @Override
    public List<Identifiable> checkIds(List<Identifiable> items) {
        for (int i = 0, size = items.size(); i < size; i++) {
            checkId(items.get(i));
        }
        return items;
    }

    /**
     * set an unique identifier for all items which do not have one set already
     *
     * @param items
     * @return
     */
    @Override
    public Identifiable[] checkIds(Identifiable... items) {
        for (Identifiable item : items) {
            checkId(item);
        }
        return items;
    }

    /**
     * set an unique identifier for the item which do not have one set already
     *
     * @param item
     * @return
     */
    @Override
    public Identifiable checkId(Identifiable item) {
        if (item.getIdentifier() == -1) {
            item.withIdentifier(nextId(item));
        }
        return item;
    }
}
