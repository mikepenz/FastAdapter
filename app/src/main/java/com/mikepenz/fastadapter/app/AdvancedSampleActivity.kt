package com.mikepenz.fastadapter.app

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.aboutlibraries.util.getThemeColor
import com.mikepenz.fastadapter.*
import com.mikepenz.fastadapter.adapters.GenericItemAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter.Companion.items
import com.mikepenz.fastadapter.app.adapters.StickyHeaderAdapter
import com.mikepenz.fastadapter.app.items.SimpleItem
import com.mikepenz.fastadapter.app.items.expandable.SimpleSubExpandableItem
import com.mikepenz.fastadapter.app.items.expandable.SimpleSubItem
import com.mikepenz.fastadapter.expandable.getExpandableExtension
import com.mikepenz.fastadapter.helpers.ActionModeHelper
import com.mikepenz.fastadapter.select.getSelectExtension
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration
import kotlinx.android.synthetic.main.activity_sample.*
import java.util.*
import java.util.concurrent.atomic.AtomicLong

/**
 * This sample showcases compatibility the awesome Sticky-Headers library by timehop
 * https://github.com/timehop/sticky-headers-recyclerview
 */
class AdvancedSampleActivity : AppCompatActivity() {

    //save our FastAdapter
    private lateinit var mFastAdapter: GenericFastAdapter
    private lateinit var mHeaderAdapter: ItemAdapter<SimpleItem>
    private lateinit var mItemAdapter: GenericItemAdapter

    private var mActionModeHelper: ActionModeHelper<GenericItem>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sample)

        // Handle Toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setTitle(R.string.sample_advanced)

        //create our adapters
        mHeaderAdapter = items()
        mItemAdapter = items()
        val stickyHeaderAdapter = StickyHeaderAdapter<GenericItem>()

        //we also want the expandable feature

        //create our FastAdapter
        val adapters: Collection<ItemAdapter<out GenericItem>> = listOf(mHeaderAdapter, mItemAdapter)
        mFastAdapter = FastAdapter.with(adapters)

        mFastAdapter.getExpandableExtension()
        val selectExtension = mFastAdapter.getSelectExtension()

        //configure our mFastAdapter
        //as we provide id's for the items we want the hasStableIds enabled to speed up things
        selectExtension.isSelectable = true
        selectExtension.multiSelect = true
        selectExtension.selectOnLongClick = true

        mFastAdapter.onPreClickListener = { _: View?, _: GenericAdapter, item: GenericItem, _: Int ->
            //we handle the default onClick behavior for the actionMode. This will return null if it didn't do anything and you can handle a normal onClick
            val res = mActionModeHelper?.onClick(item)
            res ?: false
        }

        mFastAdapter.onPreLongClickListener = { _: View, _: GenericAdapter, item: GenericItem, position: Int ->
            //we do not want expandable items to be selected
            if (item is IExpandable<*> && item.subItems.isNotEmpty()) {
                true
            } else {
                //handle the longclick actions
                val actionMode = mActionModeHelper?.onLongClick(this@AdvancedSampleActivity, position)
                if (actionMode != null) {
                    //we want color our CAB
                    findViewById<View>(R.id.action_mode_bar).setBackgroundColor(this@AdvancedSampleActivity.getThemeColor(R.attr.colorPrimary, R.color.colorPrimary))
                }
                //if we have no actionMode we do not consume the event
                actionMode != null
            }
        }

        //we init our ActionModeHelper
        mActionModeHelper = ActionModeHelper(mFastAdapter, R.menu.cab, ActionBarCallBack())

        //get our recyclerView and do basic setup
        rv.layoutManager = LinearLayoutManager(this)
        rv.itemAnimator = DefaultItemAnimator()
        rv.adapter = stickyHeaderAdapter.wrap(mFastAdapter)

        val decoration = StickyRecyclerHeadersDecoration(stickyHeaderAdapter)
        rv.addItemDecoration(decoration)

        //so the headers are aware of changes
        mFastAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                decoration.invalidateHeaders()
            }
        })

        //init cache with the added items, this is useful for shorter lists with many many different view types (at least 4 or more
        //new RecyclerViewCacheUtil().withCacheSize(2).apply(rv, items);

        //set the back arrow in the toolbar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(false)

        //we define the items
        setItems()

        //restore selections (this has to be done after the items were added
        mFastAdapter.withSavedInstanceState(savedInstanceState)
    }

    private fun setItems() {
        val sampleItem = SimpleItem().withName("Header")
        sampleItem.isSelectable = false
        sampleItem.identifier = 1
        mHeaderAdapter.add(sampleItem)
        //fill with some sample data
        val id = AtomicLong(1)
        val items = ArrayList<IItem<*>>()
        val size = 25

        for (i in 1..size) {
            if (i % 6 == 0) {
                val expandableItem = SimpleSubExpandableItem()
                expandableItem.withName("Test " + id.get())
                        .withHeader(headers[i / 5])
                        .identifier = id.getAndIncrement()
                val subItems = LinkedList<SimpleSubExpandableItem>()
                for (ii in 1..3) {
                    val subItem = SimpleSubExpandableItem()
                    subItem.withName("-- SubTest " + id.get())
                            .withHeader(headers[i / 5])
                            .identifier = id.getAndIncrement()

                    val subSubItems = LinkedList<ISubItem<*>>()
                    for (iii in 1..3) {
                        val subSubItem = SimpleSubItem()
                        subSubItem.withName("---- SubSubTest " + id.get())
                                .withHeader(headers[i / 5])
                                .identifier = id.getAndIncrement()
                        subSubItems.add(subSubItem)
                    }
                    subItem.subItems = subSubItems

                    subItems.add(subItem)
                }
                expandableItem.subItems.addAll(subItems)
                items.add(expandableItem)
            } else {
                val simpleSubItem = SimpleSubItem().withName("Test " + id.get()).withHeader(headers[i / 5])
                simpleSubItem.identifier = id.getAndIncrement()
                items.add(simpleSubItem)
            }
        }
        mItemAdapter.set(items)
    }

    override fun onSaveInstanceState(_outState: Bundle) {
        var outState = _outState
        //add the values which need to be saved from the adapter to the bundle
        outState = mFastAdapter.saveInstanceState(outState)
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

    /**
     * Our ActionBarCallBack to showcase the CAB
     */
    internal inner class ActionBarCallBack : ActionMode.Callback {

        override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
            //logic if an item was clicked
            //return false as we want default behavior to go on
            return false
        }

        override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
            return true
        }

        override fun onDestroyActionMode(mode: ActionMode) {}

        override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
            return false
        }
    }

    companion object {
        private val headers = arrayOf("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z")
    }
}
