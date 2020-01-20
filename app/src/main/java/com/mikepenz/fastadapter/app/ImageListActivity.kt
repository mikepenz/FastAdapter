package com.mikepenz.fastadapter.app

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.mikepenz.fastadapter.IAdapter
import com.mikepenz.fastadapter.adapters.FastItemAdapter
import com.mikepenz.fastadapter.app.dummy.ImageDummyData
import com.mikepenz.fastadapter.app.items.ImageItem
import kotlinx.android.synthetic.main.activity_sample.*

class ImageListActivity : AppCompatActivity() {
    //save our FastAdapter
    private lateinit var fastItemAdapter: FastItemAdapter<ImageItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sample)

        // Handle Toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setTitle(R.string.sample_image_list)

        //create our FastAdapter which will manage everything
        fastItemAdapter = FastItemAdapter()

        //configure our fastAdapter
        fastItemAdapter.onClickListener = { v: View?, _: IAdapter<ImageItem>, item: ImageItem, _: Int ->
            if (v != null) {
                Toast.makeText(v.context, item.mName, Toast.LENGTH_SHORT).show()
            }
            false
        }

        //find out how many columns we display
        val columns = resources.getInteger(R.integer.wall_splash_columns)
        if (columns == 1) {
            //linearLayoutManager for one column
            rv.layoutManager = LinearLayoutManager(this)
        } else {
            //gridLayoutManager for more than one column ;)
            rv.layoutManager = GridLayoutManager(this, columns)
        }
        rv.itemAnimator = DefaultItemAnimator()
        rv.adapter = fastItemAdapter

        //fill with some sample data
        fastItemAdapter.add(ImageDummyData.imageItems)

        //restore selections (this has to be done after the items were added
        fastItemAdapter.withSavedInstanceState(savedInstanceState)

        //a custom OnCreateViewHolder listener class which is used to create the viewHolders
        //we define the listener for the imageLovedContainer here for better performance
        //you can also define the listener within the items bindView method but performance is better if you do it like this
        fastItemAdapter.addEventHook(ImageItem.ImageItemHeartClickEvent())

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
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
