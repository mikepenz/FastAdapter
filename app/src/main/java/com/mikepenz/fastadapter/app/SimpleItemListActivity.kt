package com.mikepenz.fastadapter.app

import android.graphics.Color
import android.os.Bundle
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
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.IAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter.Companion.items
import com.mikepenz.fastadapter.app.adapters.FastScrollIndicatorAdapter
import com.mikepenz.fastadapter.app.items.SimpleItem
import com.mikepenz.fastadapter.drag.ItemTouchCallback
import com.mikepenz.fastadapter.drag.SimpleDragCallback
import com.mikepenz.fastadapter.listeners.ItemFilterListener
import com.mikepenz.fastadapter.select.getSelectExtension
import com.mikepenz.fastadapter.utils.DragDropUtil
import com.mikepenz.iconics.IconicsDrawable
import com.mikepenz.iconics.typeface.library.materialdesigniconic.MaterialDesignIconic
import com.mikepenz.iconics.utils.actionBar
import com.mikepenz.iconics.utils.colorInt
import kotlinx.android.synthetic.main.activity_sample.*
import java.util.*

class SimpleItemListActivity : AppCompatActivity(), ItemTouchCallback, ItemFilterListener<SimpleItem> {

    //save our FastAdapter
    private lateinit var fastAdapter: FastAdapter<SimpleItem>
    private lateinit var itemAdapter: ItemAdapter<SimpleItem>

    //drag & drop
    private lateinit var touchCallback: SimpleDragCallback
    private lateinit var touchHelper: ItemTouchHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sample)

        // Handle Toolbar
        setSupportActionBar(toolbar)

        //
        val fastScrollIndicatorAdapter = FastScrollIndicatorAdapter<SimpleItem>()
        itemAdapter = items()

        //create our FastAdapter which will manage everything
        fastAdapter = FastAdapter.with(itemAdapter)
        val selectExtension = fastAdapter.getSelectExtension()
        selectExtension.isSelectable = true

        //configure our fastAdapter
        fastAdapter.onClickListener = { v: View?, _: IAdapter<SimpleItem>, item: SimpleItem, _: Int ->
            v?.let {
                Toast.makeText(v.context, item.name?.getText(v.context), Toast.LENGTH_LONG).show()
            }
            false
        }

        //configure the itemAdapter
        itemAdapter.itemFilter.filterPredicate = { item: SimpleItem, constraint: CharSequence? ->
            item.name?.textString.toString().toLowerCase(Locale.getDefault()).contains(constraint.toString().toLowerCase(Locale.getDefault()))
        }

        itemAdapter.itemFilter.itemFilterListener = this

        //get our recyclerView and do basic setup
        rv.layoutManager = LinearLayoutManager(this)
        rv.itemAnimator = DefaultItemAnimator()
        rv.adapter = fastScrollIndicatorAdapter.wrap(fastAdapter)

        //fill with some sample data
        var x = 0
        val items = ArrayList<SimpleItem>()
        for (s in ALPHABET) {
            val count = Random().nextInt(20)
            for (i in 1..count) {
                val item = SimpleItem().withName("$s Test $x")
                item.identifier = (100 + x).toLong()
                items.add(item)
                x++
            }
        }
        itemAdapter.add(items)

        //add drag and drop for item
        touchCallback = SimpleDragCallback(this)
        touchHelper = ItemTouchHelper(touchCallback) // Create ItemTouchHelper and pass with parameter the SimpleDragCallback
        touchHelper.attachToRecyclerView(rv) // Attach ItemTouchHelper to RecyclerView

        //restore selections (this has to be done after the items were added
        fastAdapter.withSavedInstanceState(savedInstanceState)

        //set the back arrow in the toolbar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(false)
    }

    override fun onSaveInstanceState(_outState: Bundle) {
        var outState = _outState
        //add the values which need to be saved from the adapter to the bundle
        outState = fastAdapter.saveInstanceState(outState)
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
                itemAdapter.filter(s)
                return true
            }

            override fun onQueryTextChange(s: String): Boolean {
                itemAdapter.filter(s)
                touchCallback.setIsDragEnabled(TextUtils.isEmpty(s))
                return true
            }
        })

        return super.onCreateOptionsMenu(menu)
    }

    override fun itemTouchOnMove(oldPosition: Int, newPosition: Int): Boolean {
        DragDropUtil.onMove(itemAdapter, oldPosition, newPosition)  // change position
        return true
    }

    override fun itemTouchDropped(oldPosition: Int, newPosition: Int) {
        // save the new item order, i.e. in your database
        // remove visual highlight to dropped item
    }

    override fun itemTouchStartDrag(viewHolder: RecyclerView.ViewHolder) {
        // add visual highlight to dragged item
    }

    override fun itemsFiltered(constraint: CharSequence?, results: List<SimpleItem>?) {
        Toast.makeText(this@SimpleItemListActivity, "filtered items count: " + itemAdapter.adapterItemCount, Toast.LENGTH_SHORT).show()
    }

    override fun onReset() {
    }

    companion object {
        private val ALPHABET = arrayOf("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z")
    }
}
