package com.mikepenz.fastadapter.app

import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.mikepenz.fastadapter.adapters.FastItemAdapter
import com.mikepenz.fastadapter.app.items.SimpleItem
import com.mikepenz.fastadapter.diff.FastAdapterDiffUtil
import com.mikepenz.iconics.IconicsDrawable
import com.mikepenz.iconics.typeface.library.materialdesigniconic.MaterialDesignIconic
import com.mikepenz.iconics.utils.actionBar
import com.mikepenz.iconics.utils.colorInt
import com.mikepenz.itemanimators.AlphaInAnimator
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_sample.*

/**
 * Created by Aleksander Mielczarek on 07.08.2017.
 */

class DiffUtilActivity : AppCompatActivity() {

    //save our FastAdapter
    private lateinit var fastItemAdapter: FastItemAdapter<SimpleItem>
    private val disposables = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sample)

        // Handle Toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setTitle(R.string.sample_diff_util)

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
        menu.findItem(R.id.item_refresh).icon = IconicsDrawable(this, MaterialDesignIconic.Icon.gmi_refresh).apply { colorInt = Color.BLACK; actionBar() }
        menu.findItem(R.id.item_refresh_async).icon = IconicsDrawable(this, MaterialDesignIconic.Icon.gmi_refresh_sync).apply { colorInt = Color.BLACK; actionBar() }
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
                .map { simpleItems -> FastAdapterDiffUtil.calculateDiff(fastItemAdapter.itemAdapter, simpleItems) }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { result -> FastAdapterDiffUtil[fastItemAdapter.itemAdapter] = result })
    }

    private fun createData(): List<SimpleItem> {
        val items = mutableListOf<SimpleItem>()
        repeat(100) {
            items.add(SimpleItem().withName("Item ${it + 1}").withIdentifier((it + 1).toLong()))
        }
        items.shuffle()
        return items
    }
}