package com.mikepenz.fastadapter

/**
 * Created by mikepenz on 28.10.17.
 */

interface IAdapterNotifier {

    fun notify(
        fastAdapter: FastAdapter<*>,
        newItemsCount: Int,
        previousItemsCount: Int,
        itemsBeforeThisAdapter: Int
    ): Boolean

    companion object {

        val DEFAULT: IAdapterNotifier = object : IAdapterNotifier {
            override fun notify(
                fastAdapter: FastAdapter<*>,
                newItemsCount: Int,
                previousItemsCount: Int,
                itemsBeforeThisAdapter: Int
            ): Boolean {
                //now properly notify the adapter about the changes
                when {
                    newItemsCount > previousItemsCount -> {
                        if (previousItemsCount > 0) {
                            fastAdapter.notifyAdapterItemRangeChanged(
                                itemsBeforeThisAdapter,
                                previousItemsCount
                            )
                        }
                        fastAdapter.notifyAdapterItemRangeInserted(
                            itemsBeforeThisAdapter + previousItemsCount,
                            newItemsCount - previousItemsCount
                        )
                    }
                    newItemsCount > 0 -> {
                        fastAdapter.notifyAdapterItemRangeChanged(itemsBeforeThisAdapter, newItemsCount)
                        if (newItemsCount < previousItemsCount) {
                            fastAdapter.notifyAdapterItemRangeRemoved(
                                itemsBeforeThisAdapter + newItemsCount,
                                previousItemsCount - newItemsCount
                            )
                        }
                    }
                    newItemsCount == 0 -> fastAdapter.notifyAdapterItemRangeRemoved(
                        itemsBeforeThisAdapter,
                        previousItemsCount
                    )
                    else -> //this condition should practically never happen
                        fastAdapter.notifyAdapterDataSetChanged()
                }
                return false
            }
        }

        val LEGACY_DEFAULT: IAdapterNotifier = object : IAdapterNotifier {
            override fun notify(
                fastAdapter: FastAdapter<*>,
                newItemsCount: Int,
                previousItemsCount: Int,
                itemsBeforeThisAdapter: Int
            ): Boolean {
                //now properly notify the adapter about the changes
                when {
                    newItemsCount > previousItemsCount -> {
                        if (previousItemsCount > 0) {
                            fastAdapter.notifyAdapterItemRangeChanged(
                                itemsBeforeThisAdapter,
                                previousItemsCount
                            )
                        }
                        fastAdapter.notifyAdapterItemRangeInserted(
                            itemsBeforeThisAdapter + previousItemsCount,
                            newItemsCount - previousItemsCount
                        )
                    }
                    newItemsCount in 1..(previousItemsCount - 1) -> {
                        fastAdapter.notifyAdapterItemRangeChanged(itemsBeforeThisAdapter, newItemsCount)
                        fastAdapter.notifyAdapterItemRangeRemoved(
                            itemsBeforeThisAdapter + newItemsCount,
                            previousItemsCount - newItemsCount
                        )
                    }
                    newItemsCount == 0 -> fastAdapter.notifyAdapterItemRangeRemoved(
                        itemsBeforeThisAdapter,
                        previousItemsCount
                    )
                    else -> //this condition should practically never happen
                        fastAdapter.notifyAdapterDataSetChanged()
                }
                return false
            }
        }
    }
}
