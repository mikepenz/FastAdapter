package com.mikepenz.fastadapter.app

import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.mikepenz.fastadapter.adapters.FastItemAdapter
import com.mikepenz.fastadapter.app.items.SimpleItem
import com.mikepenz.fastadapter.diff.FastAdapterDiffUtil
import com.mikepenz.iconics.IconicsColor
import com.mikepenz.iconics.IconicsDrawable
import com.mikepenz.iconics.typeface.library.materialdesigniconic.MaterialDesignIconic
import com.mikepenz.itemanimators.AlphaInAnimator
import com.mikepenz.materialize.MaterializeBuilder
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_sample.*
import java.util.*

/**
 * Created by Aleksander Mielczarek on 07.08.2017.
 */

class DiffUtilActivity : AppCompatActivity() {

    //save our FastAdapter
    private lateinit var fastItemAdapter: FastItemAdapter<SimpleItem>
    private val disposables = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        findViewById<View>(android.R.id.content).systemUiVisibility = findViewById<View>(android.R.id.content).systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sample)

        // Handle Toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setTitle(R.string.sample_diff_util)

        //style our ui
        MaterializeBuilder().withActivity(this).build()

        //create our FastAdapter which will manage everything
        fastItemAdapter = FastItemAdapter()

        //get our recyclerView and do basic setup
        rv.layoutManager = LinearLayoutManager(this)
        rv.itemAnimator = AlphaInAnimator()
        rv.adapter = fastItemAdapter

        //fill with some sample data
        setData()

        //set the back arrow in the toolbar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(false)

        //restore selections (this has to be done after the items were added
        fastItemAdapter.withSavedInstanceState(savedInstanceState)
    }

    override fun onSaveInstanceState(_outState: Bundle) {
        var outState = _outState
        //add the values which need to be saved from the adapter to the bundel
        outState = fastItemAdapter.saveInstanceState(outState)
        super.onSaveInstanceState(outState)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.refresh, menu)
        menu.findItem(R.id.item_refresh).icon = IconicsDrawable(this, MaterialDesignIconic.Icon.gmi_refresh).color(IconicsColor.colorInt(Color.BLACK)).actionBar()
        menu.findItem(R.id.item_refresh_async).icon = IconicsDrawable(this, MaterialDesignIconic.Icon.gmi_refresh_sync).color(IconicsColor.colorInt(Color.BLACK)).actionBar()
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //handle the click on the back arrow click
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
            R.id.item_refresh -> {
                setData()
                Toast.makeText(this, "Refresh synchronous", Toast.LENGTH_SHORT).show()
                return true
            }
            R.id.item_refresh_async -> {
                setDataAsync()
                Toast.makeText(this, "Refresh asynchronous", Toast.LENGTH_SHORT).show()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.clear()
    }

    private fun setData() {
        val items = createData()
        FastAdapterDiffUtil[fastItemAdapter.itemAdapter] = items
    }

    private fun setDataAsync() {
        disposables.add(Single.fromCallable { createData() }
                .map<DiffUtil.DiffResult> { simpleItems -> FastAdapterDiffUtil.calculateDiff(fastItemAdapter.itemAdapter, simpleItems) }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { result -> FastAdapterDiffUtil[fastItemAdapter.itemAdapter] = result })
    }

    private fun createData(): List<SimpleItem> {
        val items = Arrays.asList(
                SimpleItem().withName("Item 1").withIdentifier(1),
                SimpleItem().withName("Item 2").withIdentifier(2),
                SimpleItem().withName("Item 3").withIdentifier(3),
                SimpleItem().withName("Item 4").withIdentifier(4),
                SimpleItem().withName("Item 5").withIdentifier(5),
                SimpleItem().withName("Item 6").withIdentifier(6),
                SimpleItem().withName("Item 7").withIdentifier(7),
                SimpleItem().withName("Item 8").withIdentifier(8),
                SimpleItem().withName("Item 9").withIdentifier(9),
                SimpleItem().withName("Item 10").withIdentifier(10)
        )
        items.shuffle()
        return items
    }
}