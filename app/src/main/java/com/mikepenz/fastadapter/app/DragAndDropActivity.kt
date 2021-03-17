package com.mikepenz.fastadapter.app

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.GenericItem
import com.mikepenz.fastadapter.IAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter.Companion.items
import com.mikepenz.fastadapter.app.databinding.ActivitySampleBinding
import com.mikepenz.fastadapter.app.items.DragHandleTouchEvent
import com.mikepenz.fastadapter.app.items.DraggableSingleLineItem
import com.mikepenz.fastadapter.app.items.SectionHeaderItem
import com.mikepenz.fastadapter.app.items.SmallIconSingleLineItem
import com.mikepenz.fastadapter.app.view.DraggableFrameLayout
import com.mikepenz.fastadapter.app.view.RecyclerViewBackgroundDrawable
import com.mikepenz.fastadapter.drag.ItemTouchCallback
import com.mikepenz.fastadapter.drag.SimpleDragCallback
import com.mikepenz.fastadapter.utils.DragDropUtil
import com.mikepenz.iconics.IconicsDrawable
import com.mikepenz.iconics.typeface.library.materialdesigniconic.MaterialDesignIconic

private const val STATE_LIST_ORDER = "com.mikepenz.fastadapter.app.LIST_ORDER"

class DragAndDropActivity : AppCompatActivity(), ItemTouchCallback {
    private lateinit var binding: ActivitySampleBinding

    private lateinit var fastAdapter: FastAdapter<GenericItem>
    private lateinit var itemAdapter: ItemAdapter<GenericItem>

    private lateinit var touchCallback: SimpleDragCallback
    private lateinit var touchHelper: ItemTouchHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySampleBinding.inflate(layoutInflater).also {
            setContentView(it.root)
        }

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(false)

        // Create empty ItemAdapter
        itemAdapter = items()

        // Create FastAdapter instance that will manage the whole list
        fastAdapter = FastAdapter.with(itemAdapter).apply {
            // Add an event hook that manages touching the drag handle
            addEventHook(
                DragHandleTouchEvent { position ->
                    binding.rv.findViewHolderForAdapterPosition(position)?.let { viewHolder ->
                        // Start dragging
                        touchHelper.startDrag(viewHolder)
                    }
                }
            )
        }

        // Handle clicks on our list items
        fastAdapter.onClickListener = { v: View?, _: IAdapter<GenericItem>, item: GenericItem, _: Int ->
            if (v != null) {
                // Perform an action depending on the type of the item
                val message = when (item) {
                    is SmallIconSingleLineItem -> item.name.getText(v.context)
                    is DraggableSingleLineItem -> item.name.getText(v.context)
                    is SectionHeaderItem -> null
                    else -> "Unknown item type: $item"
                }
                if (message != null) {
                    Toast.makeText(v.context, message, Toast.LENGTH_SHORT).show()
                }
            }
            false
        }

        // Set up our RecyclerView
        binding.rv.layoutManager = LinearLayoutManager(this)
        binding.rv.itemAnimator = DefaultItemAnimator()
        binding.rv.adapter = fastAdapter

        // Set a custom background on the RecyclerView. It avoids filling the area without list items at the bottom of
        // the RecyclerView with our background color.
        val recyclerViewBackgroundColor = ResourcesCompat.getColor(resources, R.color.behindRecyclerView, theme)
        RecyclerViewBackgroundDrawable(recyclerViewBackgroundColor).attachTo(binding.rv)

        // Create our list and set it on the adapter
        val items = buildSampleItemList(savedInstanceState)
        itemAdapter.add(items)

        // Add drag and drop functionality to the RecyclerView
        touchCallback = SimpleDragCallback(itemTouchCallback = this).apply {
            // Disable drag & drop on long-press
            isDragEnabled = false
        }
        touchHelper = ItemTouchHelper(touchCallback)
        touchHelper.attachToRecyclerView(binding.rv)

        // Restore the adapter state (this has to be done after adding the items)
        fastAdapter.withSavedInstanceState(savedInstanceState)
    }

    private fun buildSampleItemList(savedInstanceState: Bundle?): ArrayList<GenericItem> {
        val items = ArrayList<GenericItem>()
        items.add(
            SmallIconSingleLineItem(
                IconicsDrawable(this, MaterialDesignIconic.Icon.gmi_settings),
                getString(R.string.list_item_general_settings)
            )
        )

        items.add(SectionHeaderItem(getString(R.string.list_item_accounts_section)))

        val accountIcon = IconicsDrawable(this, MaterialDesignIconic.Icon.gmi_account)

        val accountItems = (1..5).map { i ->
            val name = getString(R.string.list_item_account, i)
            DraggableSingleLineItem(accountIcon, name).apply {
                identifier = (100 + i).toLong()
            }
        }

        // Use the saved state (if available) to sort the list of accounts
        val sortedAccountItems = if (savedInstanceState != null) {
            val listOrder = savedInstanceState.getLongArray(STATE_LIST_ORDER) ?: error("Missing saved state")
            val orderById = listOrder.withIndex().associate { it.value to it.index }
            accountItems.sortedBy { orderById[it.identifier] }
        } else {
            accountItems
        }

        items.addAll(sortedAccountItems)

        items.add(SectionHeaderItem(getString(R.string.list_item_misc_section)))
        items.add(SmallIconSingleLineItem(
            IconicsDrawable(this, MaterialDesignIconic.Icon.gmi_info),
            getString(R.string.list_item_about)
        ))
        items.add(
            SmallIconSingleLineItem(
                IconicsDrawable(this, MaterialDesignIconic.Icon.gmi_code),
                getString(R.string.list_item_licenses)
            )
        )
        return items
    }

    override fun onSaveInstanceState(outState: Bundle) {
        // Save the adapter's state
        val newOutState = fastAdapter.saveInstanceState(outState)

        // Save the current account order
        val itemIdentifiers = itemAdapter.adapterItems
            .filterIsInstance<DraggableSingleLineItem>()
            .map { it.identifier }
            .toLongArray()
        newOutState.putLongArray(STATE_LIST_ORDER, itemIdentifiers)

        super.onSaveInstanceState(newOutState)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun itemTouchStartDrag(viewHolder: RecyclerView.ViewHolder) {
        // Add visual highlight to the dragged item
        (viewHolder.itemView as DraggableFrameLayout).isDragged = true
    }

    override fun itemTouchStopDrag(viewHolder: RecyclerView.ViewHolder) {
        // Remove visual highlight from the dropped item
        (viewHolder.itemView as DraggableFrameLayout).isDragged = false
    }

    override fun itemTouchOnMove(oldPosition: Int, newPosition: Int): Boolean {
        // Determine the "drop area"
        val firstDropPosition = itemAdapter.adapterItems.indexOfFirst { it is DraggableSingleLineItem }
        val lastDropPosition = itemAdapter.adapterItems.indexOfLast { it is DraggableSingleLineItem }

        // Only move the item if the new position is inside the "drop area"
        return if (newPosition in firstDropPosition..lastDropPosition) {
            // Change the item's position in the adapter
            DragDropUtil.onMove(itemAdapter, oldPosition, newPosition)
            true
        } else {
            false
        }
    }

    override fun itemTouchDropped(oldPosition: Int, newPosition: Int) {
        // Save the new item order, e.g. in your database
    }
}
