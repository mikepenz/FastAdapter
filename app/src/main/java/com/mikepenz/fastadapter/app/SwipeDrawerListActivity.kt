package com.mikepenz.fastadapter.app

import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.fastadapter.adapters.FastItemAdapter
import com.mikepenz.fastadapter.app.adapters.IDraggableViewHolder
import com.mikepenz.fastadapter.app.items.SwipeableDrawerItem
import com.mikepenz.fastadapter.drag.ItemTouchCallback
import com.mikepenz.fastadapter.drag.SimpleDragCallback
import com.mikepenz.fastadapter.swipe_drag.SimpleSwipeDrawerDragCallback
import com.mikepenz.fastadapter.utils.DragDropUtil
import com.mikepenz.iconics.IconicsDrawable
import com.mikepenz.iconics.typeface.library.materialdesigniconic.MaterialDesignIconic
import com.mikepenz.iconics.utils.actionBar
import com.mikepenz.iconics.utils.colorInt
import io.reactivex.functions.Consumer
import kotlinx.android.synthetic.main.activity_sample.*
import java.util.*

class SwipeDrawerListActivity : AppCompatActivity(), ItemTouchCallback {

    //save our FastAdapter
    private lateinit var fastItemDrawerAdapter: FastItemAdapter<SwipeableDrawerItem>

    //drag & drop
    private lateinit var touchCallback: SimpleDragCallback
    private lateinit var touchHelper: ItemTouchHelper


    private fun delete(item: SwipeableDrawerItem) {
        item.deleteAction = null
        val position12 = fastItemDrawerAdapter.getAdapterPosition(item)
        if (position12 != RecyclerView.NO_POSITION) {
            //this sample uses a filter. If a filter is used we should use the methods provided by the filter (to make sure filter and normal state is updated)
            fastItemDrawerAdapter.itemFilter.remove(position12)
            Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show()
        }
    }

    private fun archive(item: SwipeableDrawerItem) {
        item.archiveAction = null
        val position12 = fastItemDrawerAdapter.getAdapterPosition(item)
        if (position12 != RecyclerView.NO_POSITION) {
            // Do something intelligent here
            Toast.makeText(this, "Archived", Toast.LENGTH_SHORT).show()
        }
    }

    private fun share(item: SwipeableDrawerItem) {
        item.shareAction = null
        val position12 = fastItemDrawerAdapter.getAdapterPosition(item)
        if (position12 != RecyclerView.NO_POSITION) {
            // Do something intelligent here
            Toast.makeText(this, "Shared", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sample)

        // Handle Toolbar
        setSupportActionBar(toolbar)

        //create our FastAdapter which will manage everything
        fastItemDrawerAdapter = FastItemAdapter()
        //configure the itemAdapter
        fastItemDrawerAdapter.itemFilter.filterPredicate = { item: SwipeableDrawerItem, constraint: CharSequence? ->
            item.name?.textString.toString().contains(constraint.toString(), ignoreCase = true)
        }

        //get our recyclerView and do basic setup
        rv.layoutManager = LinearLayoutManager(this)
        rv.itemAnimator = DefaultItemAnimator()
        rv.adapter = fastItemDrawerAdapter

        //fill with some sample data
        var x = 0
        val items = ArrayList<SwipeableDrawerItem>()
        for (s in ALPHABET) {
            val count = Random().nextInt(20)
            for (i in 1..count) {
                val swipeableItem = SwipeableDrawerItem().withName("$s Test $x")
                swipeableItem.identifier = (100 + x).toLong()
                swipeableItem.withIsSwipeable(i % 5 != 0)
                swipeableItem.withIsDraggable(i % 5 != 0)
                swipeableItem.deleteAction = Consumer { item -> delete(item) }
                swipeableItem.archiveAction = Consumer { item -> archive(item) }
                swipeableItem.shareAction = Consumer { item -> share(item) }
                items.add(swipeableItem)
                x++
            }
        }
        fastItemDrawerAdapter.add(items)


        //add drag and drop for item
        //and add swipe as well
        touchCallback = SimpleSwipeDrawerDragCallback(
                this,
                ItemTouchHelper.LEFT)
                .withNotifyAllDrops(true)
                .withSwipeLeft(80) // Width of delete button
                .withSwipeRight(160) // Width of archive and share buttons
                .withSensitivity(10f)

        touchHelper = ItemTouchHelper(touchCallback) // Create ItemTouchHelper and pass with parameter the SimpleDragCallback
        touchHelper.attachToRecyclerView(rv) // Attach ItemTouchHelper to RecyclerView

        //restore selections (this has to be done after the items were added)
        fastItemDrawerAdapter.withSavedInstanceState(savedInstanceState)

        //set the back arrow in the toolbar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(false)
    }

    override fun onSaveInstanceState(_outState: Bundle) {
        var outState = _outState
        //add the values which need to be saved from the adapter to the bundle
        outState = fastItemDrawerAdapter.saveInstanceState(outState)
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


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu items for use in the action bar
        val inflater = menuInflater
        inflater.inflate(R.menu.search, menu)

        //search icon
        menu.findItem(R.id.search).icon = IconicsDrawable(this, MaterialDesignIconic.Icon.gmi_search).apply { colorInt = Color.BLACK; actionBar() }

        val searchView = menu.findItem(R.id.search).actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(s: String): Boolean {
                touchCallback.setIsDragEnabled(false)
                fastItemDrawerAdapter.filter(s)
                return true
            }


            override fun onQueryTextChange(s: String): Boolean {
                fastItemDrawerAdapter.filter(s)
                touchCallback.setIsDragEnabled(TextUtils.isEmpty(s))
                return true
            }
        })

        return super.onCreateOptionsMenu(menu)
    }

    override fun itemTouchOnMove(oldPosition: Int, newPosition: Int): Boolean {
        DragDropUtil.onMove(fastItemDrawerAdapter.itemAdapter, oldPosition, newPosition)  // change position
        return true
    }

    override fun itemTouchDropped(oldPosition: Int, newPosition: Int) {
        val vh: RecyclerView.ViewHolder? = rv.findViewHolderForAdapterPosition(newPosition)
        if (vh is IDraggableViewHolder) {
            (vh as IDraggableViewHolder).onDropped()
        }
        // save the new item order, i.e. in your database
    }

    override fun itemTouchStartDrag(viewHolder: RecyclerView.ViewHolder) {
        if (viewHolder is IDraggableViewHolder) {
            (viewHolder as IDraggableViewHolder).onDragged()
        }
    }

    companion object {
        private val ALPHABET = arrayOf("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z")
    }
}
