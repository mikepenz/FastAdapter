package com.mikepenz.fastadapter.helpers


import android.view.Menu
import android.view.MenuItem
import androidx.annotation.MenuRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.GenericItem
import com.mikepenz.fastadapter.IItem
import com.mikepenz.fastadapter.expandable.ExpandableExtension
import com.mikepenz.fastadapter.select.SelectExtension

/**
 * Created by mikepenz on 02.01.16.
 */
class ActionModeHelper<Item : GenericItem> {
    private var fastAdapter: FastAdapter<Item>
    private var selectExtension: SelectExtension<Item>

    @MenuRes
    private var cabMenu: Int = 0

    private var internalCallback: ActionMode.Callback
    private var callback: ActionMode.Callback? = null
    var actionMode: ActionMode? = null
        private set

    private var autoDeselect = true

    private var titleProvider: ActionModeTitleProvider? = null

    private var actionItemClickedListener: ActionItemClickedListener? = null

    /**
     * Convenient method to check if action mode is active or nor
     *
     * @return true, if ActionMode is active, false otherwise
     */
    val isActive: Boolean
        get() = actionMode != null

    @JvmOverloads
    constructor(fastAdapter: FastAdapter<Item>, cabMenu: Int, actionItemClickedListener: ActionItemClickedListener? = null) {
        this.fastAdapter = fastAdapter
        this.cabMenu = cabMenu
        this.internalCallback = ActionBarCallBack()
        this.actionItemClickedListener = actionItemClickedListener

        val ext: SelectExtension<Item> = fastAdapter.getExtension(SelectExtension::class.java)
                ?: throw IllegalStateException("The provided FastAdapter requires the `SelectExtension` or `withSelectable(true)`")
        this.selectExtension = ext
    }


    constructor(fastAdapter: FastAdapter<Item>, cabMenu: Int, callback: ActionMode.Callback) {
        this.fastAdapter = fastAdapter
        this.cabMenu = cabMenu
        this.callback = callback
        this.internalCallback = ActionBarCallBack()

        val ext: SelectExtension<Item> = fastAdapter.getExtension(SelectExtension::class.java)
                ?: throw IllegalStateException("The provided FastAdapter requires the `SelectExtension` or `withSelectable(true)`")
        this.selectExtension = ext
    }

    fun withTitleProvider(titleProvider: ActionModeTitleProvider): ActionModeHelper<Item> {
        this.titleProvider = titleProvider
        return this
    }

    fun withAutoDeselect(enabled: Boolean): ActionModeHelper<Item> {
        this.autoDeselect = enabled
        return this
    }

    /**
     * No longer needed, the FastAdapter can handle sub items now on its own
     *
     * @param expandableExtension
     * @return
     */
    @Deprecated("")
    fun withSupportSubItems(expandableExtension: ExpandableExtension<*>): ActionModeHelper<Item> {
        return this
    }

    /**
     * Implements the basic behavior of a CAB and multi select behavior,
     * including logics if the clicked item is collapsible
     *
     * @param item the current item
     * @return null if nothing was done, or a boolean to inform if the event was consumed
     */
    fun onClick(item: IItem<*>): Boolean? {
        return onClick(null, item)
    }

    /**
     * Implements the basic behavior of a CAB and multi select behavior,
     * including logics if the clicked item is collapsible
     *
     * @param act  the current Activity
     * @param item the current item
     * @return null if nothing was done, or a boolean to inform if the event was consumed
     */
    fun onClick(act: AppCompatActivity?, item: IItem<*>): Boolean? {
        //if we are current in CAB mode, and we remove the last selection, we want to finish the actionMode
        if (actionMode != null && selectExtension.selectedItems.size == 1 && item.isSelected) {
            actionMode?.finish()
            selectExtension.deselect()
            return true
        }

        if (actionMode != null) {
            // calculate the selection count for the action mode
            // because current selection is not reflecting the future state yet!
            var selected = selectExtension.selectedItems.size
            if (item.isSelected)
                selected--
            else if (item.isSelectable)
                selected++
            checkActionMode(act, selected)
        }

        return null
    }

    /**
     * Implements the basic behavior of a CAB and multi select behavior onLongClick
     *
     * @param act      the current Activity
     * @param position the position of the clicked item
     * @return the initialized ActionMode or null if nothing was done
     */
    fun onLongClick(act: AppCompatActivity, position: Int): ActionMode? {
        if (actionMode == null && fastAdapter.getItem(position)?.isSelectable == true) {
            //may check if actionMode is already displayed
            actionMode = act.startSupportActionMode(internalCallback)
            //we have to select this on our own as we will consume the event
            selectExtension.select(position)
            // update title
            checkActionMode(act, 1)
            //we consume this event so the normal onClick isn't called anymore
            return actionMode
        }
        return actionMode
    }

    /**
     * Check if the ActionMode should be shown or not depending on the currently selected items
     * Additionally, it will also update the title in the CAB for you
     *
     * @param act the current Activity
     * @return the initialized ActionMode or null if no ActionMode is active after calling this function
     */
    fun checkActionMode(act: AppCompatActivity?): ActionMode? {
        val selected = selectExtension.selectedItems.size
        return checkActionMode(act, selected)
    }

    /**
     * Reset any active action mode if it is active, useful, to avoid leaking the activity if this helper class is retained
     */
    fun reset() {
        if (actionMode != null) {
            actionMode?.finish()
            actionMode = null
        }
    }

    private fun checkActionMode(act: AppCompatActivity?, selected: Int): ActionMode? {
        if (selected == 0) {
            if (actionMode != null) {
                actionMode?.finish()
                actionMode = null
            }
        } else if (actionMode == null) {
            if (act != null)
            // without an activity, we cannot start the action mode
                actionMode = act.startSupportActionMode(internalCallback)
        }
        updateTitle(selected)
        return actionMode
    }

    /**
     * Updates the title to reflect the current selected items or to show a user defined title
     *
     * @param selected number of selected items
     */
    private fun updateTitle(selected: Int) {
        if (titleProvider != null)
            actionMode?.title = titleProvider?.getTitle(selected)
        else
            actionMode?.title = selected.toString()
    }

    /**
     * Our ActionBarCallBack to showcase the CAB
     */
    private inner class ActionBarCallBack : ActionMode.Callback {

        override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
            var consumed = callback?.onActionItemClicked(mode, item) ?: false
            if (!consumed) {
                consumed = actionItemClickedListener?.onClick(mode, item) ?: false
            }
            if (!consumed) {
                selectExtension.deleteAllSelectedItems()
                //finish the actionMode
                mode.finish()
            }
            return consumed
        }

        override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
            mode.menuInflater.inflate(cabMenu, menu)

            //as we are now in the actionMode a single click is fine for multiSelection
            selectExtension.selectOnLongClick = false

            return callback?.onCreateActionMode(mode, menu) ?: true
        }

        override fun onDestroyActionMode(mode: ActionMode) {
            actionMode = null

            //after we are done with the actionMode we fallback to longClick for multiselect
            selectExtension.selectOnLongClick = true

            //actionMode end. deselect everything
            if (autoDeselect)
                selectExtension.deselect()

            callback?.onDestroyActionMode(mode)
        }

        override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
            return callback?.onPrepareActionMode(mode, menu) ?: false
        }
    }

    // --------------------------
    // Interfaces
    // --------------------------

    interface ActionModeTitleProvider {
        fun getTitle(selected: Int): String
    }

    interface ActionItemClickedListener {
        fun onClick(mode: ActionMode, item: MenuItem): Boolean
    }
}
