package com.mikepenz.fastadapter.app

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.MenuItem
import android.view.View
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.IItem
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.mikepenz.fastadapter.app.items.expandable.SimpleSubExpandableItem
import com.mikepenz.fastadapter.app.items.expandable.SimpleSubItem
import com.mikepenz.fastadapter.expandable.getExpandableExtension
import com.mikepenz.fastadapter.select.getSelectExtension
import com.mikepenz.materialize.MaterializeBuilder
import kotlinx.android.synthetic.main.activity_sample.*
import java.util.*
import java.util.concurrent.atomic.AtomicLong

class ExpandableSampleActivity : AppCompatActivity() {
    //save our FastAdapter
    private lateinit var fastAdapter: FastAdapter<IItem<out RecyclerView.ViewHolder>>
    private lateinit var itemAdapter: ItemAdapter<IItem<out RecyclerView.ViewHolder>>

    override fun onCreate(savedInstanceState: Bundle?) {
        findViewById<View>(android.R.id.content).systemUiVisibility = findViewById<View>(android.R.id.content).systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sample)

        // Handle Toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setTitle(R.string.sample_collapsible)

        //style our ui
        MaterializeBuilder().withActivity(this).build()

        //create our FastAdapter
        itemAdapter = ItemAdapter()
        fastAdapter = FastAdapter.with(itemAdapter)

        fastAdapter.getExpandableExtension()
        val selectExtension = fastAdapter.getSelectExtension()
        selectExtension.isSelectable = true
        //expandableExtension.setOnlyOneExpandedItem(true);

        //get our recyclerView and do basic setup
        rv.layoutManager = LinearLayoutManager(this)
        rv.itemAnimator = DefaultItemAnimator()
        rv.adapter = fastAdapter

        //fill with some sample data
        val items = ArrayList<IItem<out RecyclerView.ViewHolder>>()
        val identifier = AtomicLong(1)
        for (i in 1..100) {
            if (i % 3 != 0) {
                val simpleSubItem = SimpleSubItem().withName("Test $i")
                simpleSubItem.identifier = identifier.getAndIncrement()
                items.add(simpleSubItem)
                continue
            }

            val parent = SimpleSubExpandableItem()
            parent.withName("Test $i").identifier = identifier.getAndIncrement()

            val subItems = LinkedList<SimpleSubExpandableItem>()
            for (ii in 1..5) {
                val subItem = SimpleSubExpandableItem()
                subItem.withName("-- SubTest $ii").identifier = identifier.getAndIncrement()

                if (ii % 2 == 0) {
                    continue
                }

                val subSubItems = LinkedList<SimpleSubExpandableItem>()
                for (iii in 1..3) {
                    val subSubItem = SimpleSubExpandableItem()
                    subSubItem.withName("---- SubSubTest $iii").identifier = identifier.getAndIncrement()

                    val subSubSubItems = LinkedList<SimpleSubExpandableItem>()
                    for (iiii in 1..4) {
                        val subSubSubItem = SimpleSubExpandableItem()
                        subSubSubItem.withName("---- SubSubSubTest $iiii").identifier = identifier.getAndIncrement()
                        subSubSubItems.add(subSubSubItem)
                    }
                    subSubItem.subItems = subSubSubItems
                    subSubItems.add(subSubItem)
                }
                subItem.subItems = subSubItems
                subItems.add(subItem)
            }
            parent.subItems = subItems
            items.add(parent)
        }
        itemAdapter.add(items)

        //restore selections (this has to be done after the items were added
        fastAdapter.withSavedInstanceState(savedInstanceState)

        //set the back arrow in the toolbar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(false)
    }

    override fun onSaveInstanceState(_outState: Bundle?) {
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
}
