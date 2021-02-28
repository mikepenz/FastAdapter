package com.mikepenz.fastadapter.utils

import com.mikepenz.fastadapter.IIdentifiable
import java.util.concurrent.atomic.AtomicLong

/**
 * Created by mikepenz on 19.09.15.
 */
class DefaultIdDistributorImpl<Identifiable : IIdentifiable> :
        DefaultIdDistributor<Identifiable>() {
    private val idDistributor = AtomicLong(-2L)

    override fun nextId(identifiable: Identifiable): Long {
        return idDistributor.decrementAndGet()
    }
}
