package com.mikepenz.fastadapter.app

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Message
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
import com.mikepenz.fastadapter.IAdapter
import com.mikepenz.fastadapter.adapters.FastItemAdapter
import com.mikepenz.fastadapter.app.adapters.IDraggableViewHolder
import com.mikepenz.fastadapter.app.items.SwipeableItem
import com.mikepenz.fastadapter.drag.ItemTouchCallback
import com.mikepenz.fastadapter.drag.SimpleDragCallback
import com.mikepenz.fastadapter.swipe.SimpleSwipeCallback
import com.mikepenz.fastadapter.swipe_drag.SimpleSwipeDragCallback
import com.mikepenz.fastadapter.utils.DragDropUtil
import com.mikepenz.iconics.IconicsDrawable
import com.mikepenz.iconics.typeface.library.materialdesigniconic.MaterialDesignIconic
import com.mikepenz.iconics.utils.actionBar
import com.mikepenz.iconics.utils.colorInt
import com.mikepenz.iconics.utils.sizeDp
import kotlinx.android.synthetic.main.activity_sample.*
import java.util.*

class SwipeListActivity : AppCompatActivity(), ItemTouchCallback, SimpleSwipeCallback.ItemSwipeCallback {

    //save our FastAdapter
    private lateinit var fastItemAdapter: FastItemAdapter<SwipeableItem>

    //drag & drop
    private lateinit var touchCallback: SimpleDragCallback
    private lateinit var touchHelper: ItemTouchHelper

    private val deleteHandler = Handler {
        val item = it.obj as SwipeableItem

        item.swipedAction = null
        val position12 = fastItemAdapter.getAdapterPosition(item)
        if (position12 != RecyclerView.NO_POSITION) {
            //this sample uses a filter. If a filter is used we should use the methods provided by the filter (to make sure filter and normal state is updated)
            fastItemAdapter.itemFilter.remove(position12)
        }

        true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sample)

        // Handle Toolbar
        setSupportActionBar(toolbar)

        //create our FastAdapter which will manage everything
        fastItemAdapter = FastItemAdapter()

        //configure our fastAdapter
        fastItemAdapter.onClickListener = { _: View?, _: IAdapter<SwipeableItem>, item: SwipeableItem, _: Int ->
            Toast.makeText(this@SwipeListActivity, item.name?.getText(this@SwipeListActivity), Toast.LENGTH_LONG).show()
            false
        }

        //configure the itemAdapter
        fastItemAdapter.itemFilter.filterPredicate = { item: SwipeableItem, constraint: CharSequence? ->
            item.name?.textString.toString().contains(constraint.toString(), ignoreCase = true)
        }

        //get our recyclerView and do basic setup
        rv.layoutManager = LinearLayoutManager(this)
        rv.itemAnimator = DefaultItemAnimator()
        rv.adapter = fastItemAdapter

        //fill with some sample data
        var x = 0
        val items = ArrayList<SwipeableItem>()
        for (s in ALPHABET) {
            val count = Random().nextInt(20)
            for (i in 1..count) {
                val swipeableItem = SwipeableItem().withName("$s Test $x")
                swipeableItem.identifier = (100 + x).toLong()
                swipeableItem.withIsSwipeable(i % 5 != 0)
                swipeableItem.withIsDraggable(i % 5 != 0)
                items.add(swipeableItem)
                x++
            }
        }
        fastItemAdapter.add(items)


        //add drag and drop for item
        //and add swipe as well
        val leaveBehindDrawableLeft = IconicsDrawable(this, MaterialDesignIconic.Icon.gmi_delete).apply { colorInt = Color.WHITE; sizeDp = 24 }
        val leaveBehindDrawableRight = IconicsDrawable(this, MaterialDesignIconic.Icon.gmi_archive).apply { colorInt = Color.WHITE; sizeDp = 24 }

        touchCallback = SimpleSwipeDragCallback(
                this,
                this,
                leaveBehindDrawableLeft,
                ItemTouchHelper.LEFT,
                Color.RED
        )
                .withBackgroundSwipeRight(Color.BLUE)
                .withLeaveBehindSwipeRight(leaveBehindDrawableRight)
                .withNotifyAllDrops(true)
                .withSensitivity(10f)
                .withSurfaceThreshold(0.8f)

        touchHelper = ItemTouchHelper(touchCallback) // Create ItemTouchHelper and pass with parameter the SimpleDragCallback
        touchHelper.attachToRecyclerView(rv) // Attach ItemTouchHelper to RecyclerView

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
                fastItemAdapter.filter(s)
                return true
            }


            override fun onQueryTextChange(s: String): Boolean {
                fastItemAdapter.filter(s)
                touchCallback.setIsDragEnabled(TextUtils.isEmpty(s))
                return true
            }
        })

        return super.onCreateOptionsMenu(menu)
    }

    override fun itemTouchOnMove(oldPosition: Int, newPosition: Int): Boolean {
        DragDropUtil.onMove(fastItemAdapter.itemAdapter, oldPosition, newPosition)  // change position
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

    override fun itemSwiped(position: Int, direction: Int) {
        // -- Option 1: Direct action --
        //do something when swiped such as: select, remove, update, ...:
        //A) fastItemAdapter.select(position);
        //B) fastItemAdapter.remove(position);
        //C) update item, set "read" if an email etc

        // -- Option 2: Delayed action --
        val item = fastItemAdapter.getItem(position) ?: return
        item.swipedDirection = direction

        // This can vary depending on direction but remove & archive simulated here both results in
        // removal from list
        val message = Random().nextInt()
        deleteHandler.sendMessageDelayed(Message.obtain().apply { what = message; obj = item }, 3000)

        item.swipedAction = Runnable {
            deleteHandler.removeMessages(message)

            item.swipedDirection = 0
            val position1 = fastItemAdapter.getAdapterPosition(item)
            if (position1 != RecyclerView.NO_POSITION) {
                fastItemAdapter.notifyItemChanged(position1)
            }
        }

        fastItemAdapter.notifyItemChanged(position)
        //TODO can this above be made more generic, along with the support in the item?
    }

    companion object {
        private val ALPHABET = arrayOf("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z")
    }
}
