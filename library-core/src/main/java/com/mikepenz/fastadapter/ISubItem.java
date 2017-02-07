package com.mikepenz.fastadapter;

/**
 * Created by mikepenz on 30.12.15.
 */
public interface ISubItem<T, S extends IItem & IExpandable> {
    /**
     * use this method to get the parent of this sub item
     * the parent should also contain this sub item in its sub items list
     *
     * @return the parent of this sub item
     */
    S getParent();

    /**
     * use this method to set the parent of this sub item
     * make sure, that you add this item to the parents sub items list as well
     *
     * @param parent the parent of this sub item
     */
    T withParent(S parent);
}
