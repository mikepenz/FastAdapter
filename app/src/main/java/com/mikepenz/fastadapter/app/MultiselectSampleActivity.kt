package com.mikepenz.fastadapter.app

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.mikepenz.aboutlibraries.util.getThemeColor
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.IAdapter
import com.mikepenz.fastadapter.ISelectionListener
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.mikepenz.fastadapter.app.items.SimpleItem
import com.mikepenz.fastadapter.helpers.ActionModeHelper
import com.mikepenz.fastadapter.helpers.UndoHelper
import com.mikepenz.fastadapter.select.SelectExtension
import com.mikepenz.fastadapter.select.getSelectExtension
import com.mikepenz.itemanimators.SlideDownAlphaAnimator
import kotlinx.android.synthetic.main.activity_sample.*
import java.util.*

class MultiselectSampleActivity : AppCompatActivity() {
    //save our FastAdapter
    private lateinit var mFastAdapter: FastAdapter<SimpleItem>
    private lateinit var mUndoHelper: UndoHelper<*>
    private lateinit var mActionModeHelper: ActionModeHelper<SimpleItem>
    private lateinit var selectExtension: SelectExtension<SimpleItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sample)

        // Handle Toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setTitle(R.string.sample_multi_select)

        //create our adapters
        val headerAdapter = ItemAdapter<SimpleItem>()
        val itemAdapter = ItemAdapter<SimpleItem>()

        //create our FastAdapter
        mFastAdapter = FastAdapter.with(listOf(headerAdapter, itemAdapter))

        //configure our mFastAdapter
        //as we provide id's for the items we want the hasStableIds enabled to speed up things
        mFastAdapter.setHasStableIds(true)
        selectExtension = mFastAdapter.getSelectExtension()
        selectExtension.apply {
            isSelectable = true
            multiSelect = true
            selectOnLongClick = true
            selectionListener = object : ISelectionListener<SimpleItem> {
                override fun onSelectionChanged(item: SimpleItem, selected: Boolean) {
                    Log.i("FastAdapter", "SelectedCount: " + selectExtension.selections.size + " ItemsCount: " + selectExtension.selectedItems.size)
                }
            }
        }

        mFastAdapter.onPreClickListener = { _: View?, _: IAdapter<SimpleItem>, item: SimpleItem, _: Int ->
            //we handle the default onClick behavior for the actionMode. This will return null if it didn't do anything and you can handle a normal onClick
            val res = mActionModeHelper.onClick(item)
            res ?: false
        }

        mFastAdapter.onClickListener = { v: View?, _: IAdapter<SimpleItem>, _: SimpleItem, _: Int ->
            if (v != null) {
                Toast.makeText(v.context, "SelectedCount: " + selectExtension.selections.size + " ItemsCount: " + selectExtension.selectedItems.size, Toast.LENGTH_SHORT).show()
            }
            false
        }

        mFastAdapter.onPreLongClickListener = { _: View, _: IAdapter<SimpleItem>, _: SimpleItem, position: Int ->
            val actionMode = mActionModeHelper.onLongClick(this@MultiselectSampleActivity, position)
            if (actionMode != null) {
                //we want color our CAB
                findViewById<View>(R.id.action_mode_bar).setBackgroundColor(this@MultiselectSampleActivity.getThemeColor(R.attr.colorPrimary, R.color.colorPrimary))
            }
            //if we have no actionMode we do not consume the event
            actionMode != null
        }

        //
        mUndoHelper = UndoHelper(mFastAdapter, object : UndoHelper.UndoListener<SimpleItem> {
            override fun commitRemove(positions: Set<Int>, removed: ArrayList<FastAdapter.RelativeInfo<SimpleItem>>) {
                Log.e("UndoHelper", "Positions: " + positions.toString() + " Removed: " + removed.size)
            }
        })

        //we init our ActionModeHelper
        mActionModeHelper = ActionModeHelper(mFastAdapter, R.menu.cab, ActionBarCallBack())

        //get our recyclerView and do basic setup
        rv.layoutManager = LinearLayoutManager(this)
        rv.itemAnimator = SlideDownAlphaAnimator()
        rv.adapter = mFastAdapter

        //fill with some sample data
        val simpleItem = SimpleItem()
        simpleItem
                .withName("Header")
        simpleItem.identifier = 2
        simpleItem.isSelectable = false
        headerAdapter.add(simpleItem)
        val items = ArrayList<SimpleItem>()
        for (i in 1..100) {
            val item = SimpleItem()
            item.withName("Test $i")
            item.identifier = (100 + i).toLong()
            items.add(item)
        }
        itemAdapter.add(items)

        //restore selections (this has to be done after the items were added
        mFastAdapter.withSavedInstanceState(savedInstanceState)

        //set the back arrow in the toolbar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(false)

        //inform that longClick is required
        Toast.makeText(this, "LongClick to enable Multi-Selection", Toast.LENGTH_LONG).show()
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
            mUndoHelper.remove(findViewById(android.R.id.content), "Item removed", "Undo", Snackbar.LENGTH_LONG, selectExtension.selections)
            //as we no longer have a selection so the actionMode can be finished
            mode.finish()
            //we consume the event
            return true
        }

        override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
            return true
        }

        override fun onDestroyActionMode(mode: ActionMode) {}

        override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
            return false
        }
    }
}
