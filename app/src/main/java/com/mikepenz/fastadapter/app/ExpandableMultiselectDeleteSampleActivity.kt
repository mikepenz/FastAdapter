package com.mikepenz.fastadapter.app

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode
import androidx.recyclerview.widget.LinearLayoutManager
import com.michaelflisar.dragselectrecyclerview.DragSelectTouchListener
import com.mikepenz.aboutlibraries.util.getThemeColor
import com.mikepenz.fastadapter.*
import com.mikepenz.fastadapter.adapters.FastItemAdapter
import com.mikepenz.fastadapter.adapters.GenericFastItemAdapter
import com.mikepenz.fastadapter.app.items.HeaderSelectionItem
import com.mikepenz.fastadapter.app.items.expandable.SimpleSubItem
import com.mikepenz.fastadapter.expandable.ExpandableExtension
import com.mikepenz.fastadapter.expandable.getExpandableExtension
import com.mikepenz.fastadapter.helpers.ActionModeHelper
import com.mikepenz.fastadapter.helpers.RangeSelectorHelper
import com.mikepenz.fastadapter.select.SelectExtension
import com.mikepenz.fastadapter.select.getSelectExtension
import com.mikepenz.fastadapter.utils.SubItemUtil
import com.mikepenz.itemanimators.SlideDownAlphaAnimator
import kotlinx.android.synthetic.main.activity_sample.*
import java.util.*

class ExpandableMultiselectDeleteSampleActivity : AppCompatActivity() {
    //save our FastAdapter
    private lateinit var fastItemAdapter: GenericFastItemAdapter
    private lateinit var mExpandableExtension: ExpandableExtension<IItem<*>>
    private lateinit var mSelectExtension: SelectExtension<IItem<*>>
    private lateinit var mRangeSelectorHelper: RangeSelectorHelper<*>
    private lateinit var mDragSelectTouchListener: DragSelectTouchListener
    private var mActionModeHelper: ActionModeHelper<GenericItem>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sample)

        setSupportActionBar(toolbar)
        supportActionBar?.setTitle(R.string.sample_collapsible)

        //create our FastAdapter
        fastItemAdapter = FastItemAdapter()
        mExpandableExtension = fastItemAdapter.getExpandableExtension()
        mSelectExtension = fastItemAdapter.getSelectExtension()
        mSelectExtension.isSelectable = true
        mSelectExtension.multiSelect = true
        mSelectExtension.selectOnLongClick = true

        fastItemAdapter.onPreClickListener = { _: View?, _: IAdapter<GenericItem>, item: GenericItem, _: Int ->
            //we handle the default onClick behavior for the actionMode. This will return null if it didn't do anything and you can handle a normal onClick
            val res = mActionModeHelper?.onClick(this@ExpandableMultiselectDeleteSampleActivity, item)
            // in this example, we want to consume a click, if the ActionModeHelper will remove the ActionMode
            // so that the click listener is not fired
            if (res != null && !res) true else res ?: false
        }


        fastItemAdapter.onClickListener = { _: View?, _: IAdapter<GenericItem>, item: GenericItem, _: Int ->
            // check if the actionMode consumes the click. This returns true, if it does, false if not
            if (mActionModeHelper?.isActive == false) {
                Toast.makeText(this@ExpandableMultiselectDeleteSampleActivity, (item as SimpleSubItem).name.toString() + " clicked!", Toast.LENGTH_SHORT).show()
                mRangeSelectorHelper.onClick()
            }
            false
        }

        fastItemAdapter.onPreLongClickListener = { _: View, _: IAdapter<GenericItem>, _: GenericItem, position: Int ->
            val actionModeWasActive = mActionModeHelper?.isActive ?: false
            val actionMode = mActionModeHelper?.onLongClick(this@ExpandableMultiselectDeleteSampleActivity, position)
            mRangeSelectorHelper.onLongClick(position)
            if (actionMode != null) {
                //we want color our CAB
                this@ExpandableMultiselectDeleteSampleActivity.findViewById<View>(R.id.action_mode_bar).setBackgroundColor(this@ExpandableMultiselectDeleteSampleActivity.getThemeColor(R.attr.colorPrimary, R.color.colorPrimary))

                // start the drag selection
                mDragSelectTouchListener.startDragSelection(position)
            }

            //if we have no actionMode we do not consume the event
            actionMode != null && !actionModeWasActive
        }

        // provide a custom title provider that even shows the count of sub items
        mActionModeHelper = ActionModeHelper(fastItemAdapter, R.menu.cab, ActionBarCallBack())
                .withTitleProvider(object : ActionModeHelper.ActionModeTitleProvider {
                    override fun getTitle(selected: Int): String {
                        return selected.toString() + "/" + SubItemUtil.countItems(fastItemAdapter.itemAdapter, false)
                    }
                })

        // this will take care of selecting range of items via long press on the first and afterwards on the last item
        mRangeSelectorHelper = RangeSelectorHelper(fastItemAdapter)
                .withSavedInstanceState(savedInstanceState)
                .withActionModeHelper(mActionModeHelper)

        // setup the drag select listener and add it to the RecyclerView
        mDragSelectTouchListener = DragSelectTouchListener()
                .withSelectListener { start, end, isSelected ->
                    mRangeSelectorHelper.selectRange(start, end, isSelected, true)
                    // we handled the long press, so we reset the range selector
                    mRangeSelectorHelper.reset()
                }
        rv.addOnItemTouchListener(mDragSelectTouchListener)

        // do basic RecyclerView setup
        rv.layoutManager = LinearLayoutManager(this)
        rv.itemAnimator = SlideDownAlphaAnimator()
        rv.adapter = fastItemAdapter

        //fill with some sample data
        val items = ArrayList<GenericItem>()
        for (i in 0..19) {
            if (i % 2 == 0) {
                val expandableItem = HeaderSelectionItem()
                expandableItem.withSubSelectionProvider { SubItemUtil.countSelectedSubItems(fastItemAdapter, expandableItem) }
                expandableItem
                        .withName("Test " + (i + 1))
                        .withDescription("ID: " + (i + 1))
                        .identifier = (i + 1).toLong()
                //.withIsExpanded(true) don't use this in such a setup, use adapter.expand() to expand all items instead

                //add subitems so we can showcase the collapsible functionality
                val subItems = LinkedList<ISubItem<*>>()
                for (ii in 1..5) {
                    val sampleItem = SimpleSubItem()
                    sampleItem
                            .withName("-- Test " + (i + 1) + "." + ii)
                            .withDescription("ID: " + ((i + 1) * 100 + ii))
                            .identifier = ((i + 1) * 100 + ii).toLong()
                    subItems.add(sampleItem)
                }
                expandableItem.subItems = subItems

                items.add(expandableItem)
            } else {
                val sampleItem = SimpleSubItem()
                sampleItem
                        .withName("Test " + (i + 1))
                        .withDescription("ID: " + (i + 1))
                        .identifier = (i + 1).toLong()
                items.add(sampleItem)
            }
        }
        fastItemAdapter.add(items)
        mExpandableExtension.expand()

        mSelectExtension.selectionListener = object : ISelectionListener<IItem<*>> {
            override fun onSelectionChanged(item: IItem<*>, selected: Boolean) {
                if (item is SimpleSubItem) {
                    val headerItem = item.parent
                    if (headerItem != null) {
                        val pos = fastItemAdapter.getAdapterPosition(headerItem)
                        // Important: notify the header directly, not via the notifyadapterItemChanged!
                        // we just want to update the view and we are sure, nothing else has to be done
                        fastItemAdapter.notifyItemChanged(pos)
                    }
                }
            }
        }

        //restore selections (this has to be done after the items were added
        fastItemAdapter.withSavedInstanceState(savedInstanceState)

        //set the back arrow in the toolbar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(false)

        // restore action mode
        if (savedInstanceState != null)
            mActionModeHelper?.checkActionMode(this)
    }

    override fun onSaveInstanceState(_outState: Bundle) {
        var outState = _outState
        //add the values which need to be saved from the adapter to the bundle
        outState = fastItemAdapter.saveInstanceState(outState)
        outState = mRangeSelectorHelper.saveInstanceState(outState)
        super.onSaveInstanceState(outState)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //handle the click on the back arrow click
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    internal inner class ActionBarCallBack : ActionMode.Callback {

        override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
            return true
        }

        override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {

            // delete the selected items with the SubItemUtil to correctly handle sub items
            // this will even delete empty headers if you want to
            SubItemUtil.deleteSelected(fastItemAdapter, mSelectExtension, mExpandableExtension, notifyParent = true, deleteEmptyHeaders = true)
            //as we no longer have a selection so the actionMode can be finished
            mode.finish()
            //we consume the event
            return true
        }

        override fun onDestroyActionMode(mode: ActionMode) {
            // reset the range selector
            mRangeSelectorHelper.reset()
        }

        override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
            return false
        }
    }
}
