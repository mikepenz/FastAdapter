package com.mikepenz.fastadapter

/**
 * Created by mikepenz on 27.12.15.
 */
abstract class AbstractAdapter<Item : GenericItem> : IAdapter<Item> {
    override var fastAdapter: FastAdapter<Item>? = null

    /** The position of this Adapter in the FastAdapter */
    override var order: Int = -1

    /** Internal mapper to remember and add possible types for the RecyclerView */
    override fun mapPossibleTypes(items: Iterable<Item>?) {
        fastAdapter?.let { fastAdapter ->
            items?.mapNotNull { (it as? IItemVHFactory<*> ?: it.factory)?.let { fact -> it to fact } }?.forEach { (item, factory) ->
                fastAdapter.registerItemFactory(item.type, factory)
            }
        }
    }
}
