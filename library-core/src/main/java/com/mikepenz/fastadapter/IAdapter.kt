package com.mikepenz.fastadapter

/**
 * Kotlin type alias to simplify usage for an all accepting IAdapter
 */
typealias GenericAdapter = IAdapter<GenericItem>

/**
 * Created by mikepenz on 27.12.15.
 */
interface IAdapter<Item : GenericItem> {

    /** Defines the FastAdapter which manages all the core logic */
    var fastAdapter: FastAdapter<Item>?

    /** Defines the position of this Adapter in the FastAdapter */
    var order: Int

    /** Defines the count of items of THIS adapter */
    val adapterItemCount: Int

    /** Defines the list of defined items within THIS adapter */
    val adapterItems: List<Item>

    /**
     * @param position the relative position
     * @return the item at the given relative position within this adapter
     */
    fun getAdapterItem(position: Int): Item

    /**
     * @param position the relative position
     * @return the item at the given relative position within this adapter if it has been loaded
     */
    fun peekAdapterItem(position: Int): Item? {
        return getAdapterItem(position)
    }

    /**
     * Searches for the given item and calculates its relative position
     *
     * @param item the item which is searched for
     * @return the relative position
     */
    fun getAdapterPosition(item: Item): Int

    /**
     * Searches for the given item and calculates its relative position
     *
     * @param identifier the identifier of an item which is searched for
     * @return the relative position
     */
    fun getAdapterPosition(identifier: Long): Int


    /**
     * Returns the global position based on the relative position given
     *
     * @param position the relative position within this adapter
     * @return the global position used for all methods
     */
    fun getGlobalPosition(position: Int): Int
}
