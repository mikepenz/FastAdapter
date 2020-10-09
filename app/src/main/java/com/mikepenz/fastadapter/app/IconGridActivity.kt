package com.mikepenz.fastadapter.app

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.mikepenz.fastadapter.adapters.FastItemAdapter
import com.mikepenz.fastadapter.adapters.GenericFastItemAdapter
import com.mikepenz.fastadapter.app.binding.BindingIconItem
import com.mikepenz.fastadapter.app.items.expandable.SimpleSubExpandableItem
import com.mikepenz.fastadapter.expandable.getExpandableExtension
import com.mikepenz.iconics.Iconics
import com.mikepenz.itemanimators.SlideDownAlphaAnimator
import kotlinx.android.synthetic.main.activity_sample.*
import java.util.ArrayList
import kotlin.Comparator

class IconGridActivity : AppCompatActivity() {
    //save our FastAdapter
    private lateinit var fastItemAdapter: GenericFastItemAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sample)

        // Handle Toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setTitle(R.string.sample_icon_grid)

        //create our FastAdapter which will manage everything
        fastItemAdapter = FastItemAdapter()

        //we want to have expandables
        val expandableExtension = fastItemAdapter.getExpandableExtension()

        //init our gridLayoutManager and configure RV
        val gridLayoutManager = GridLayoutManager(this, 3)
        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when (fastItemAdapter.getItemViewType(position)) {
                    R.id.fastadapter_expandable_item_id -> 3
                    R.id.fastadapter_icon_item_id -> 1
                    else -> -1
                }
            }
        }

        rv.layoutManager = gridLayoutManager
        rv.itemAnimator = SlideDownAlphaAnimator()
        rv.adapter = fastItemAdapter

        //order fonts by their name
        val mFonts = ArrayList(Iconics.getRegisteredFonts(this))
        mFonts.sortWith(Comparator { object1, object2 -> object1.fontName.compareTo(object2.fontName) })

        //add all icons of all registered Fonts to the list
        val items = ArrayList<SimpleSubExpandableItem>(mFonts.size)
        for ((count, font) in mFonts.withIndex()) {
            //we set the identifier from the count here, as I need a stable ID in the sample to showcase the state restore
            val expandableItem = SimpleSubExpandableItem()
            expandableItem.withName(font.fontName).identifier = count.toLong()

            val icons = ArrayList<BindingIconItem>()
            for (icon in font.icons) {
                val iconItem = BindingIconItem()
                iconItem.withIcon(font.getIcon(icon))
                icons.add(iconItem)
            }
            expandableItem.subItems.addAll(icons)
            items.add(expandableItem)
        }

        //fill with some sample data
        fastItemAdapter.add(items)

        //if first start we want to expand the item with ID 2
        if (savedInstanceState != null) {
            //restore selections (this has to be done after the items were added
            fastItemAdapter.withSavedInstanceState(savedInstanceState)
        } else {
            //expand one item to make sample look a bit more interesting
            expandableExtension.expand(2)
        }

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
