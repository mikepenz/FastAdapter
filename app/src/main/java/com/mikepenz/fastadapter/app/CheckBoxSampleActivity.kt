package com.mikepenz.fastadapter.app

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.fastadapter.IAdapter
import com.mikepenz.fastadapter.adapters.FastItemAdapter
import com.mikepenz.fastadapter.app.items.CheckBoxSampleItem
import com.mikepenz.fastadapter.listeners.OnClickListener
import com.mikepenz.fastadapter.select.SelectExtension
import com.mikepenz.materialize.MaterializeBuilder
import kotlinx.android.synthetic.main.activity_sample.*
import java.util.*

class CheckBoxSampleActivity : AppCompatActivity() {

    //save our FastAdapter
    private lateinit var fastItemAdapter: FastItemAdapter<CheckBoxSampleItem>
    private lateinit var selectExtension: SelectExtension<CheckBoxSampleItem>

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
        selectExtension = SelectExtension(fastItemAdapter)
        fastItemAdapter.addExtension(selectExtension)
        selectExtension.isSelectable = true

        //configure our fastAdapter
        fastItemAdapter.onClickListener = object : OnClickListener<CheckBoxSampleItem> {
            override fun onClick(v: View?, adapter: IAdapter<CheckBoxSampleItem>, item: CheckBoxSampleItem, position: Int): Boolean {
                v?.let {
                    Toast.makeText(v.context, item.name?.getText(v.context), Toast.LENGTH_LONG).show()
                }
                return false
            }

        }
        fastItemAdapter.onPreClickListener = object : OnClickListener<CheckBoxSampleItem> {
            override fun onClick(v: View?, adapter: IAdapter<CheckBoxSampleItem>, item: CheckBoxSampleItem, position: Int): Boolean {
                // consume otherwise radio/checkbox will be deselected
                return true
            }
        }

        fastItemAdapter.addEventHook(CheckBoxSampleItem.CheckBoxClickEvent())

        //get our recyclerView and do basic setup
        val recyclerView = findViewById<RecyclerView>(R.id.rv)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.adapter = fastItemAdapter

        //fill with some sample data
        var x = 0
        val items = ArrayList<CheckBoxSampleItem>()
        for (s in ALPHABET) {
            val count = Random().nextInt(20)
            for (i in 1..count) {
                val item = CheckBoxSampleItem().withName("$s Test $x")
                item.identifier = (100 + x).toLong()
                items.add(item)
                x++
            }
        }
        fastItemAdapter.add(items)

        //restore selections (this has to be done after the items were added
        fastItemAdapter.withSavedInstanceState(savedInstanceState)

        //set the back arrow in the toolbar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(false)
    }

    override fun onSaveInstanceState(_outState: Bundle?) {
        var outState = _outState
        //add the values which need to be saved from the adapter to the bundle
        outState = fastItemAdapter.saveInstanceState(outState)
        super.onSaveInstanceState(outState)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //handle the click on the back arrow click
        return when (item.itemId) {
            android.R.id.home -> {
                Toast.makeText(applicationContext, "selections = " + selectExtension!!.selections, Toast.LENGTH_LONG).show()
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        private val ALPHABET = arrayOf("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z")
    }
}
