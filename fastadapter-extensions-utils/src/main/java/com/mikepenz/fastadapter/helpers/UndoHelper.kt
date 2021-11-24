package com.mikepenz.fastadapter.helpers

import android.view.View
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.GenericItem
import com.mikepenz.fastadapter.IItemAdapter
import com.mikepenz.fastadapter.select.SelectExtension
import java.util.*
import java.util.Arrays.asList

/**
 * Created by mikepenz on 04.01.16.
 *
 * Constructor to create the UndoHelper
 *
 * @param adapter      the root FastAdapter
 * @param undoListener the listener which gets called when an item was really removed
 */
class UndoHelper<Item : GenericItem>(
    private val adapter: FastAdapter<Item>,
    private val undoListener: UndoListener<Item>
) {
    private var history: History? = null
    private var snackBarActionText = ""
    private var alreadyCommitted: Boolean = false

    private val snackBarCallback = object : Snackbar.Callback() {
        override fun onShown(sb: Snackbar?) {
            super.onShown(sb)
            // Reset the flag when a new Snackbar shows up
            alreadyCommitted = false
        }

        override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
            super.onDismissed(transientBottomBar, event)

            // If the undo button was clicked (DISMISS_EVENT_ACTION) or
            // if a commit was already executed, skip it
            if (event == DISMISS_EVENT_ACTION || alreadyCommitted)
                return

            notifyCommit()
        }
    }

    var snackBar: Snackbar? = null
        private set

    /** Constructs the snackbar to be used for the undoHelper */
    private var snackbarBuilder: (() -> Snackbar)? = null

    /**
     * An optional method to add a [Snackbar] of your own with custom styling.
     * note that using this method will override your custom action
     *
     * @param actionText the text to show for the Undo Action
     * @param snackbarBuilder   constructs the snackbar with custom styling
     */
    fun withSnackBar(actionText: String, snackbarBuilder: () -> Snackbar) {
        this.snackBarActionText = actionText
        this.snackbarBuilder = snackbarBuilder
    }

    /**
     * Convenience method to be used if you have previously set a [Snackbar] with [withSnackBar]
     * NOTE: No action will be executed if [withSnackBar] was not previously called.
     *
     * @param positions the positions where the items were removed
     * @return the snackbar or null if [withSnackBar] was not previously called
     */
    fun remove(positions: Set<Int>): Snackbar? {
        val builder = this.snackbarBuilder ?: return null
        return remove(positions, snackBarActionText, builder)
    }

    /**
     * Removes items from the ItemAdapter.
     *
     * Creates a default [Snackbar] with the given information.
     *
     * @param view       the view which will host the SnackBar
     * @param text       the text to show on the SnackBar
     * @param actionText the text to show for the Undo Action
     * @param positions  the positions where the items were removed
     * @return the generated Snackbar
     */
    fun remove(view: View, text: String, actionText: String, @BaseTransientBottomBar.Duration duration: Int, positions: Set<Int>): Snackbar {
        return remove(positions, actionText) {
            Snackbar.make(view, text, duration)
        }
    }

    /**
     * Removes items from the ItemAdapter.
     * Displays the created [Snackbar] via the [snackbarBuilder], and configures the action with the given [actionText]
     *
     * @param positions  the positions where the items were removed
     * @param actionText the text to show for the Undo Action
     * @param snackbarBuilder   constructs the snackbar with custom styling
     * @return the generated Snackbar
     */
    fun remove(positions: Set<Int>, actionText: String, snackbarBuilder: () -> Snackbar): Snackbar {
        if (history != null) {
            // Set a flag, if remove was called before the Snackbar
            // executed the commit -> Snackbar does not commit the new
            // inserted history
            alreadyCommitted = true
            notifyCommit()
        }

        val history = History()
        history.action = ACTION_REMOVE
        for (position in positions) {
            history.items.add(adapter.getRelativeInfo(position))
        }
        history.items.sortWith { lhs, rhs -> Integer.valueOf(lhs.position).compareTo(rhs.position) }

        this.history = history
        doChange() // Do not execute when Snackbar shows up, instead change immediately

        return snackbarBuilder.invoke()
            .addCallback(snackBarCallback)
            .setAction(actionText) { undoChange() }
            .also {
                it.show()
                this.snackBar = it
            }
    }

    private fun notifyCommit() {
        history?.let { mHistory ->
            if (mHistory.action == ACTION_REMOVE) {
                val positions = TreeSet(Comparator<Int> { lhs, rhs -> lhs.compareTo(rhs) })
                for (relativeInfo in mHistory.items) {
                    positions.add(relativeInfo.position)
                }
                undoListener.commitRemove(positions, mHistory.items)
                this.history = null
            }
        }
    }

    private fun doChange() {
        history?.let { mHistory ->
            if (mHistory.action == ACTION_REMOVE) {
                for (i in mHistory.items.indices.reversed()) {
                    val relativeInfo = mHistory.items[i]
                    if (relativeInfo.adapter is IItemAdapter<*, *>) {
                        (relativeInfo.adapter as IItemAdapter<*, *>).remove(relativeInfo.position)
                    }
                }
            }
        }
    }

    private fun undoChange() {
        history?.let { mHistory ->
            if (mHistory.action == ACTION_REMOVE) {
                var i = 0
                val size = mHistory.items.size
                while (i < size) {
                    val relativeInfo = mHistory.items[i]
                    if (relativeInfo.adapter is IItemAdapter<*, *>) {
                        val adapter = relativeInfo.adapter as IItemAdapter<*, Item>?

                        relativeInfo.item?.let {
                            adapter?.addInternal(relativeInfo.position, asList(it))
                            if (relativeInfo.item?.isSelected == true) {
                                val selectExtension = this.adapter.getExtension<SelectExtension<Item>>(SelectExtension::class.java)
                                selectExtension?.select(relativeInfo.position)
                            }
                        }
                    }
                    i++
                }
            }
        }
        history = null
    }

    interface UndoListener<Item : GenericItem> {
        fun commitRemove(positions: Set<Int>, removed: ArrayList<FastAdapter.RelativeInfo<Item>>)
    }

    private inner class History {
        var action: Int = 0
        var items = ArrayList<FastAdapter.RelativeInfo<Item>>()
    }

    companion object {
        private const val ACTION_REMOVE = 2
    }
}
