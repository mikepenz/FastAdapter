package com.mikepenz.fastadapter.app

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.fastadapter.IItem
import com.mikepenz.fastadapter.adapters.FastItemAdapter
import com.mikepenz.fastadapter.adapters.GenericFastItemAdapter
import com.mikepenz.fastadapter.adapters.GenericItemAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter.Companion.items
import com.mikepenz.fastadapter.app.items.SimpleItem
import com.mikepenz.fastadapter.drag.ItemTouchCallback
import com.mikepenz.fastadapter.drag.SimpleDragCallback
import com.mikepenz.fastadapter.listeners.ItemFilterListener
import com.mikepenz.fastadapter.scroll.EndlessRecyclerOnScrollListener
import com.mikepenz.fastadapter.select.getSelectExtension
import com.mikepenz.fastadapter.ui.items.ProgressItem
import com.mikepenz.fastadapter.utils.DragDropUtil
import com.mikepenz.iconics.IconicsColor
import com.mikepenz.iconics.IconicsDrawable
import com.mikepenz.iconics.typeface.library.materialdesigniconic.MaterialDesignIconic
import com.mikepenz.materialize.MaterializeBuilder
import kotlinx.android.synthetic.main.activity_sample.*
import java.util.*

class EndlessScrollListActivity : AppCompatActivity(), ItemTouchCallback, ItemFilterListener<IItem<out RecyclerView.ViewHolder>> {

    //save our FastAdapter
    private lateinit var fastItemAdapter: GenericFastItemAdapter
    private lateinit var footerAdapter: GenericItemAdapter

    //drag & drop
    private lateinit var touchCallback: SimpleDragCallback
    private lateinit var touchHelper: ItemTouchHelper

    //endless scroll
    lateinit var endlessRecyclerOnScrollListener: EndlessRecyclerOnScrollListener

    override fun onCreate(savedInstanceState: Bundle?) {
        findViewById<View>(android.R.id.content).systemUiVisibility = findViewById<View>(android.R.id.content).systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sample)

        // Handle Toolbar
        setSupportActionBar(toolbar)

        //style our ui
        MaterializeBuilder().withActivity(this).build()

        //create our FastAdapter which will manage everything
        fastItemAdapter = FastItemAdapter()
        val selectExtension = fastItemAdapter.getSelectExtension()
        selectExtension.isSelectable = true

        //create our FooterAdapter which will manage the progress items
        footerAdapter = items()
        fastItemAdapter.addAdapter(1, footerAdapter)

        //configure our fastAdapter
        fastItemAdapter.onClickListener = { v, _, item, _ ->
            if (v != null && item is SimpleItem) {
                Toast.makeText(v.context, item.name?.getText(v.context), Toast.LENGTH_LONG).show()
            }
            false
        }

        //configure the itemAdapter
        fastItemAdapter.itemFilter.filterPredicate = { item: IItem<out RecyclerView.ViewHolder>, constraint: CharSequence? ->
            if (item is SimpleItem) {
                //return true if we should filter it out
                item.name?.text.toString().toLowerCase().contains(constraint.toString().toLowerCase())
            } else {
                //return false to keep it
                false
            }
        }

        fastItemAdapter.itemFilter.itemFilterListener = this

        //get our recyclerView and do basic setup
        rv.layoutManager = LinearLayoutManager(this)
        rv.itemAnimator = DefaultItemAnimator()
        rv.adapter = fastItemAdapter
        endlessRecyclerOnScrollListener = object : EndlessRecyclerOnScrollListener(footerAdapter) {
            override fun onLoadMore(currentPage: Int) {
                footerAdapter.clear()
                val progressItem = ProgressItem()
                progressItem.isEnabled = false
                footerAdapter.add(progressItem)
                //simulate networking (2 seconds)
                val handler = Handler()
                handler.postDelayed({
                    footerAdapter.clear()
                    for (i in 1..15) {
                        fastItemAdapter.add(fastItemAdapter.adapterItemCount, SimpleItem().withName("Item $i Page $currentPage"))
                    }
                }, 2000)
            }
        }
        rv.addOnScrollListener(endlessRecyclerOnScrollListener)

        //fill with some sample data (load the first page here)
        val items = ArrayList<SimpleItem>()
        for (i in 1..15) {
            items.add(SimpleItem().withName("Item $i Page 0"))
        }
        fastItemAdapter.add(items)

        //add drag and drop for item
        touchCallback = SimpleDragCallback(this)
        touchHelper = ItemTouchHelper(touchCallback) // Create ItemTouchHelper and pass with parameter the SimpleDragCallback
        touchHelper.attachToRecyclerView(rv) // Attach ItemTouchHelper to RecyclerView

        //restore selections (this has to be done after the items were added
        fastItemAdapter.withSavedInstanceState(savedInstanceState)

        //set the back arrow in the toolbar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(false)
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        var outState = outState
        //add the values which need to be saved from the adapter to the bundle
        outState = fastItemAdapter.saveInstanceState(outState)
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
        menu.findItem(R.id.search).icon = IconicsDrawable(this, MaterialDesignIconic.Icon.gmi_search).color(IconicsColor.colorInt(Color.BLACK)).actionBar()

        val searchView = menu.findItem(R.id.search).actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(s: String): Boolean {
                touchCallback.setIsDragEnabled(false)
                fastItemAdapter.filter(s)
                return true
            }


            override fun onQueryTextChange(s: String): Boolean {
                fastItemAdapter.filter(s)
                touchCallback.setIsDragEnabled(TextUtils.isEmpty(s))
                return true
            }
        })
        endlessRecyclerOnScrollListener.enable()

        return super.onCreateOptionsMenu(menu)
    }

    override fun itemTouchOnMove(oldPosition: Int, newPosition: Int): Boolean {
        DragDropUtil.onMove(fastItemAdapter.itemAdapter, oldPosition, newPosition) // change position
        return true
    }

    override fun itemTouchDropped(oldPosition: Int, newPosition: Int) {
        // save the new item order, i.e. in your database
    }

    override fun itemsFiltered(constraint: CharSequence?, results: List<IItem<out RecyclerView.ViewHolder>>?) {
        endlessRecyclerOnScrollListener.disable()
        Toast.makeText(this@EndlessScrollListActivity, "filtered items count: " + fastItemAdapter.itemCount, Toast.LENGTH_SHORT).show()
    }

    override fun onReset() {
        endlessRecyclerOnScrollListener.enable()
    }
}
