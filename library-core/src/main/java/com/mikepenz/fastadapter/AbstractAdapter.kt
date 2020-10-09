package com.mikepenz.fastadapter

/**
 * Created by mikepenz on 27.12.15.
 */
abstract class AbstractAdapter<Item : GenericItem> : IAdapter<Item> {
    override var fastAdapter: FastAdapter<Item>? = null

    /** The position of this Adapter in the FastAdapter */
    override var order: Int = -1
}
