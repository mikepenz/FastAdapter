package com.mikepenz.fastadapter;

import java.util.List;

/**
 * Created by mikepenz on 30.12.15.
 */
public interface ICollapsible<T> {
    /**
     * @return true if collapsed (closed)
     */
    boolean isCollapsed();

    /**
     * use this method to set if the Collapsible item is currently collapsed (closed)
     *
     * @param collapsed true if collapsed (closed)
     * @return
     */
    T withCollapsed(boolean collapsed);

    /**
     * @return the list of subItems
     */
    List<IItem> getSubItems();
}
