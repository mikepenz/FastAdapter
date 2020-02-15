package com.mikepenz.fastadapter

/**
 * Defines the factory logic to generate ViewHolders for an item
 */
interface IItemVHFactoryCache<ItemVHFactory : GenericItemVHFactory> {

    fun register(item: ItemVHFactory): Boolean

    operator fun get(type: Int): ItemVHFactory

    fun clear()
}
