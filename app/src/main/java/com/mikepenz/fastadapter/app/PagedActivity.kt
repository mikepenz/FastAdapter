package com.mikepenz.fastadapter.app

import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.paging.PagedList
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.app.items.expandable.SimpleSubExpandableItem
import com.mikepenz.fastadapter.app.paged.Coupon
import com.mikepenz.fastadapter.app.paged.CouponViewModel
import com.mikepenz.fastadapter.paged.PagedModelAdapter
import com.mikepenz.itemanimators.SlideDownAlphaAnimator
import kotlinx.android.synthetic.main.activity_main.*


class PagedActivity : AppCompatActivity() {

    //save our FastAdapter
    private lateinit var mFastAdapter: FastAdapter<SimpleSubExpandableItem>
    //save our FastAdapter
    private lateinit var mItemAdapter: PagedModelAdapter<Coupon, SimpleSubExpandableItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        findViewById<View>(android.R.id.content).systemUiVisibility = findViewById<View>(android.R.id.content).systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        //create the activity
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        // Handle Toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.title = ""

        val asyncDifferConfig = AsyncDifferConfig.Builder<Coupon>(object : DiffUtil.ItemCallback<Coupon>() {
            override fun areItemsTheSame(oldItem: Coupon, newItem: Coupon): Boolean {
                return oldItem._id == newItem._id;
            }

            override fun areContentsTheSame(oldItem: Coupon, newItem: Coupon): Boolean {
                return oldItem == newItem;
            }
        }).build()

        //create our ItemAdapter which will host our items
        mItemAdapter = PagedModelAdapter<Coupon, SimpleSubExpandableItem>(asyncDifferConfig) {
            SimpleSubExpandableItem().withName(it.offer)
        }

        //create our FastAdapter which will manage everything
        mFastAdapter = FastAdapter.with(listOf(mItemAdapter))

        mFastAdapter.registerTypeInstance(SimpleSubExpandableItem())

        //configure our fastAdapter
        //rv.setLayoutManager(new GridLayoutManager(this, 3));
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = mFastAdapter
        rv.itemAnimator = SlideDownAlphaAnimator().apply {
            addDuration = 500
            removeDuration = 500
        }

        val viewModel = ViewModelProviders.of(this,
                CouponViewModel.CouponViewModelFactory(this.application))
                .get(CouponViewModel::class.java)

        //listen to data changes and pass it to adapter for displaying in recycler view
        viewModel.couponList.observe(this, Observer<PagedList<Coupon>> { t -> mItemAdapter.submitList(t!!) })

        //if we do this. the first added items will be animated :D
        Handler().postDelayed({
            //restore selections (this has to be done after the items were added
            mFastAdapter.withSavedInstanceState(savedInstanceState)
        }, 50)
    }


    override fun onSaveInstanceState(_outState: Bundle) {
        var outState = _outState
        //add the values which need to be saved from the adapter to the bundle
        outState = mFastAdapter.saveInstanceState(outState)
        super.onSaveInstanceState(outState)
    }
}
