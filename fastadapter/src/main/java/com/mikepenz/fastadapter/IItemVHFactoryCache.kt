package com.mikepenz.fastadapter

/**
 * Defines the factory logic to generate ViewHolders for an item
 */
interface IItemVHFactoryCache<ItemVHFactory : GenericItemVHFactory> {

    fun register(type: Int, item: ItemVHFactory): Boolean

    operator fun get(type: Int): ItemVHFactory

    fun contains(type: Int): Boolean

    fun clear()
}
