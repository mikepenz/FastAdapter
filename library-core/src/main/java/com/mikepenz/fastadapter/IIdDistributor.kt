package com.mikepenz.fastadapter

import com.mikepenz.fastadapter.utils.DefaultIdDistributorImpl

/**
 * Created by fabianterhorst on 16.07.17.
 */
interface IIdDistributor<Identifiable : IIdentifyable> {

    fun checkIds(items: List<Identifiable>): List<Identifiable>

    fun checkIds(vararg items: Identifiable): Array<out Identifiable>

    fun checkId(item: Identifiable): Identifiable

    fun nextId(item: Identifiable): Long

    companion object {

        val DEFAULT: IIdDistributor<out IIdentifyable> =
            DefaultIdDistributorImpl<IIdentifyable>()
    }
}
