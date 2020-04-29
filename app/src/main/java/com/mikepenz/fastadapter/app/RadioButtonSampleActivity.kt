package com.mikepenz.fastadapter.app

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.mikepenz.fastadapter.IAdapter
import com.mikepenz.fastadapter.adapters.FastItemAdapter
import com.mikepenz.fastadapter.app.items.RadioButtonSampleItem
import com.mikepenz.fastadapter.select.SelectExtension
import com.mikepenz.fastadapter.select.getSelectExtension
import kotlinx.android.synthetic.main.activity_sample.*
import java.util.*

class RadioButtonSampleActivity : AppCompatActivity() {

    //save our FastAdapter
    private lateinit var fastItemAdapter: FastItemAdapter<RadioButtonSampleItem>
    private lateinit var selectExtension: SelectExtension<RadioButtonSampleItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sample)

        // Handle Toolbar
        setSupportActionBar(toolbar)

        //create our FastAdapter which will manage everything
        fastItemAdapter = FastItemAdapter()
        selectExtension = fastItemAdapter.getSelectExtension()
        selectExtension.isSelectable = true

        //configure our fastAdapter
        fastItemAdapter.onClickListener = { v: View?, _: IAdapter<RadioButtonSampleItem>, item: RadioButtonSampleItem, _: Int ->
            v?.let {
                Toast.makeText(v.context, item.name?.getText(v.context), Toast.LENGTH_LONG).show()
            }
            false
        }
        fastItemAdapter.onPreClickListener = { _: View?, _: IAdapter<RadioButtonSampleItem>, _: RadioButtonSampleItem, _: Int ->
            true // consume otherwise radio/checkbox will be deselected
        }

        fastItemAdapter.addEventHook(RadioButtonSampleItem.RadioButtonClickEvent())

        //get our recyclerView and do basic setup
        rv.layoutManager = LinearLayoutManager(this)
        rv.itemAnimator = DefaultItemAnimator()
        rv.adapter = fastItemAdapter

        //fill with some sample data
        var x = 0
        val items = ArrayList<RadioButtonSampleItem>()
        for (s in ALPHABET) {
            val count = Random().nextInt(20)
            for (i in 1..count) {
                val item = RadioButtonSampleItem().withName("$s Test $x")
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

    override fun onSaveInstanceState(_outState: Bundle) {
        var outState = _outState
        //add the values which need to be saved from the adapter to the bundle
        outState = fastItemAdapter.saveInstanceState(outState)
        super.onSaveInstanceState(outState)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //handle the click on the back arrow click
        return when (item.itemId) {
            android.R.id.home -> {
                Toast.makeText(applicationContext, "selections = " + selectExtension.selections, Toast.LENGTH_LONG).show()
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
