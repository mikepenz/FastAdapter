package com.mikepenz.fastadapter

import com.mikepenz.fastadapter.utils.DefaultIdDistributorImpl

/**
 * Created by fabianterhorst on 16.07.17.
 */
interface IIdDistributor<Identifiable : IIdentifyable> {

    fun checkIds(identifiables: List<Identifiable>): List<Identifiable>

    fun checkIds(vararg identifiables: Identifiable): Array<out Identifiable>

    fun checkId(identifiable: Identifiable): Identifiable

    fun nextId(identifiable: Identifiable): Long

    companion object {
        @JvmField
        val DEFAULT: IIdDistributor<out IIdentifyable> = DefaultIdDistributorImpl()
    }
}
