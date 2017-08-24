package com.mikepenz.fastadapter;

import com.mikepenz.fastadapter.utils.DefaultTypeInstanceCache;

/**
 * Created by fabianterhorst on 24.08.17.
 */

public interface ITypeInstanceCache<Item extends IItem> {

    ITypeInstanceCache<? extends IItem> DEFAULT = new DefaultTypeInstanceCache<>();

    boolean register(Item item);

    Item get(int type);

    void clear();
}
