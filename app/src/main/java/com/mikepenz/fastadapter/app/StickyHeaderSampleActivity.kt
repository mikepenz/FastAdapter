package com.mikepenz.fastadapter.app

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.mikepenz.fastadapter.app.adapters.StickyHeaderAdapter
import com.mikepenz.fastadapter.app.items.SimpleItem
import com.mikepenz.fastadapter.select.getSelectExtension
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration
import kotlinx.android.synthetic.main.activity_sample.*
import java.util.*

/**
 * This sample showcases compatibility the awesome Sticky-Headers library by timehop
 * https://github.com/timehop/sticky-headers-recyclerview
 */
class StickyHeaderSampleActivity : AppCompatActivity() {

    //save our FastAdapter
    private lateinit var fastAdapter: FastAdapter<SimpleItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sample)

        // Handle Toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setTitle(R.string.sample_sticky_header)

        //create our adapters
        val stickyHeaderAdapter = StickyHeaderAdapter<SimpleItem>()
        val headerAdapter = ItemAdapter<SimpleItem>()
        val itemAdapter = ItemAdapter<SimpleItem>()

        //create our FastAdapter
        fastAdapter = FastAdapter.with(listOf(headerAdapter, itemAdapter))
        val selectExtension = fastAdapter.getSelectExtension()
        selectExtension.isSelectable = true

        //configure our fastAdapter
        //as we provide id's for the items we want the hasStableIds enabled to speed up things
        fastAdapter.setHasStableIds(true)

        //get our recyclerView and do basic setup
        rv.layoutManager = LinearLayoutManager(this)
        rv.itemAnimator = DefaultItemAnimator()
        rv.adapter = stickyHeaderAdapter.wrap(fastAdapter)

        //this adds the Sticky Headers within our list
        val decoration = StickyRecyclerHeadersDecoration(stickyHeaderAdapter)
        rv.addItemDecoration(decoration)

        //fill with some sample data
        val item = SimpleItem().withName("Header")
        item.identifier = 1
        headerAdapter.add(item)
        val items = ArrayList<SimpleItem>()
        for (i in 1..100) {
            val simpleItem = SimpleItem().withName("Test $i").withHeader(headers[i / 5])
            simpleItem.identifier = (100 + i).toLong()
            items.add(simpleItem)
        }
        itemAdapter.add(items)

        //so the headers are aware of changes
        stickyHeaderAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                decoration.invalidateHeaders()
            }
        })

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

    companion object {
        private val headers = arrayOf("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z")
    }
}
