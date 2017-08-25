package com.mikepenz.fastadapter;

/**
 * Created by fabianterhorst on 24.08.17.
 */

public interface ITypeInstanceCache<Item extends IItem> {

    boolean register(Item item);

    Item get(int type);

    void clear();
}
