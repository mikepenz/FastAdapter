package com.mikepenz.fastadapter;

import java.util.List;

/**
 * Created by mikepenz on 30.12.15.
 */
public interface IExpandable<T, Item extends IItem & ISubItem> {
    /**
     * @return true if expanded (opened)
     */
    boolean isExpanded();

    /**
     * use this method to set if the Expandable item is currently expanded
     *
     * @param expanded true if expanded (opened)
     * @return this
     */
    T withIsExpanded(boolean expanded);

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

    /**
     * overwrite this method and return true if the item should auto expand on click, false if you want to disable this
     *
     * @return true if this item should auto expand in the adapter
     */
    boolean isAutoExpanding();
}
