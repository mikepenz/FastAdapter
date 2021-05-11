package com.mikepenz.fastadapter.app

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.app.databinding.ActivitySampleBinding
import com.mikepenz.fastadapter.app.items.SimpleImageItem
import com.mikepenz.fastadapter.app.paged.DemoEntity
import com.mikepenz.fastadapter.app.paged.DemoEntityViewModel
import com.mikepenz.fastadapter.paged.PagedModelAdapter
import com.mikepenz.fastadapter.select.getSelectExtension
import com.mikepenz.iconics.IconicsDrawable
import com.mikepenz.iconics.typeface.library.materialdesigniconic.MaterialDesignIconic
import com.mikepenz.iconics.utils.actionBar
import com.mikepenz.iconics.utils.colorInt


class PagedActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySampleBinding

    //save our FastAdapter
    private lateinit var mFastAdapter: FastAdapter<SimpleImageItem>

    //save our FastAdapter
    private lateinit var mItemAdapter: PagedModelAdapter<DemoEntity, SimpleImageItem>

    private lateinit var viewModel: DemoEntityViewModel

    private var iteration = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        //create the activity
        super.onCreate(savedInstanceState)
        binding = ActivitySampleBinding.inflate(layoutInflater).also {
            setContentView(it.root)
        }

        // Handle Toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setTitle(R.string.sample_paged_list)

        val asyncDifferConfig =
            AsyncDifferConfig.Builder(object : DiffUtil.ItemCallback<DemoEntity>() {
                override fun areItemsTheSame(oldItem: DemoEntity, newItem: DemoEntity): Boolean {
                    return oldItem.identifier == newItem.identifier
                }

                override fun areContentsTheSame(oldItem: DemoEntity, newItem: DemoEntity): Boolean {
                    return oldItem.data1 == newItem.data1
                }
/*
                override fun getChangePayload(oldItem: DemoEntity, newItem: DemoEntity): Any? {
                    return
                }

 */
            }).build()

        //create our ItemAdapter which will host our items
        mItemAdapter = PagedModelAdapter<DemoEntity, SimpleImageItem>(
            asyncDifferConfig,
            { SimpleImageItem().setPlaceholder() }) {
            SimpleImageItem().apply {
                identifier = it.identifier.toLong()
                isSelectable = true
                withImage("https://raw.githubusercontent.com/mikepenz/earthview-wallpapers/develop/thumb/yang_zhuo_yong_cuo,_tibet-china-63.jpg")
                withName(it.data1 ?: "")
                withDescription(it.data2 ?: "")
            }
        }

        //create our FastAdapter which will manage everything
        mFastAdapter = FastAdapter.with(listOf(mItemAdapter))

        //
        val selectExtension = mFastAdapter.getSelectExtension()
        selectExtension.isSelectable = true

        //configure our fastAdapter
        //rv.setLayoutManager(new GridLayoutManager(this, 3));
        binding.rv.layoutManager = LinearLayoutManager(this)
        binding.rv.adapter = mFastAdapter

        viewModel = ViewModelProvider(
            this,
            DemoEntityViewModel.DemoEntityViewModelFactory(this.application)
        )
            .get(DemoEntityViewModel::class.java)

        //listen to data changes and pass it to adapter for displaying in recycler view
        viewModel.demoEntitiesList.observe(this, { t -> mItemAdapter.submitList(t!!) })

        //if we do this. the first added items will be animated :D
        Handler().postDelayed({
            //restore selections (this has to be done after the items were added
            mFastAdapter.withSavedInstanceState(savedInstanceState)
        }, 50)

        //set the back arrow in the toolbar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(false)
    }


    override fun onSaveInstanceState(_outState: Bundle) {
        var outState = _outState
        //add the values which need to be saved from the adapter to the bundle
        outState = mFastAdapter.saveInstanceState(outState)
        super.onSaveInstanceState(outState)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.refresh, menu)
        menu.findItem(R.id.item_refresh).icon =
            IconicsDrawable(this, MaterialDesignIconic.Icon.gmi_refresh).apply {
                colorInt = Color.BLACK; actionBar()
            }
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
                viewModel.updateEntities(++iteration)
                Toast.makeText(this, "Refresh synchronous", Toast.LENGTH_SHORT).show()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
