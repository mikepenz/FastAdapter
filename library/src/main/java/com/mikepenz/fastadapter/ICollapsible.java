package com.mikepenz.fastadapter;

import java.util.List;

/**
 * Created by mikepenz on 30.12.15.
 */
public interface ICollapsible<T, Item extends IItem> {
    /**
     * @return true if collapsed (closed)
     */
    boolean isCollapsed();

    /**
     * use this method to set if the Collapsible item is currently collapsed (closed)
     *
     * @param collapsed true if collapsed (closed)
     * @return this
     */
    T withCollapsed(boolean collapsed);

    /**
     * use this method to set the subItems of this item
     *
     * @param subItems the subItems for this Collapsible Item
     * @return this
     */
    T withSubItems(List<Item> subItems);

    /**
     * @return the list of subItems
     */
    List<Item> getSubItems();
}
