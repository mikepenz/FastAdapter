package com.mikepenz.fastadapter

/**
 * Created by fabianterhorst on 24.08.17.
 */

interface ITypeInstanceCache<Item : GenericItem> {

    fun register(item: Item): Boolean

    operator fun get(type: Int): Item

    fun clear()
}
