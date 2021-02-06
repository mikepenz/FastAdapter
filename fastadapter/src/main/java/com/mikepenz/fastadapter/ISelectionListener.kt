package com.mikepenz.fastadapter

/**
 * Created by flisar on 21.09.2016.
 */

interface ISelectionListener<Item : GenericItem> {
    /**
     * Is called, whenever the provided item is selected or deselected
     *
     * @param item the item who's selection state changed
     * @param selected the new selection state of the item
     */
    fun onSelectionChanged(item: Item, selected: Boolean)
}
