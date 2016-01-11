package com.mikepenz.fastadapter;

import java.util.List;

/**
 * Created by mikepenz on 30.12.15.
 */
public interface IExpandable<T, Item extends IItem> {
    /**
     * @return true if expanded (opened)
     */
    boolean isExpanded();

    /**
     * use this method to set if the Expandable item is currently expanded
     *
     * @param collapsed true if expanded (opened)
     * @return this
     */
    T withIsExpanded(boolean collapsed);

    /**
     * use this method to set the subItems of this item
     *
     * @param subItems the subItems for this Expandable Item
     * @return this
     */
    T withSubItems(List<Item> subItems);

    /**
     * @return the list of subItems
     */
    List<Item> getSubItems();
}
