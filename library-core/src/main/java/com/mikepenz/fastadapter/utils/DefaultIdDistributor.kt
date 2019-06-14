package com.mikepenz.fastadapter.utils

import com.mikepenz.fastadapter.IIdDistributor
import com.mikepenz.fastadapter.IIdentifyable

/**
 * Created by mikepenz on 19.09.15.
 */
abstract class DefaultIdDistributor<Identifiable : IIdentifyable> : IIdDistributor<Identifiable> {

    /**
     * set an unique identifier for all identifiables which do not have one set already
     *
     * @param identifiables
     * @return
     */
    override fun checkIds(identifiables: List<Identifiable>): List<Identifiable> {
        var i = 0
        val size = identifiables.size
        while (i < size) {
            checkId(identifiables[i])
            i++
        }
        return identifiables
    }

    /**
     * set an unique identifier for all items which do not have one set already
     *
     * @param identifiables
     * @return
     */
    override fun checkIds(vararg identifiables: Identifiable): Array<out Identifiable> {
        for (identifiable in identifiables) {
            checkId(identifiable)
        }
        return identifiables
    }

    /**
     * set an unique identifier for the identifiable which do not have one set already
     *
     * @param identifiable
     * @return
     */
    override fun checkId(identifiable: Identifiable): Identifiable {
        if (identifiable.identifier == -1L) {
            identifiable.identifier = nextId(identifiable)
        }
        return identifiable
    }
}
