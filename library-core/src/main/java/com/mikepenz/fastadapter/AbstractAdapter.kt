package com.mikepenz.fastadapter

/**
 * Created by mikepenz on 27.12.15.
 */
abstract class AbstractAdapter<Item : GenericItem> : IAdapter<Item> {
    override var fastAdapter: IFastAdapter<Item>? = null
    /**
     * returs the position of this Adapter in the FastAdapter
     *
     * @return the position of this Adapter in the FastAdapter
     */
    /**
     * sets the position of this Adapter in the FastAdapter
     * @param order the position of this Adapter in the FastAdapter
     */
    override var order = -1

    /**
     * internal mapper to remember and add possible types for the RecyclerView
     *
     * @param items
     */
    override fun mapPossibleTypes(items: Iterable<Item>?) {
        fastAdapter?.let { fastAdapter ->
            if (items != null) {
                for (item in items) {
                    fastAdapter.registerTypeInstance(item)
                }
            }
        }
    }
}
