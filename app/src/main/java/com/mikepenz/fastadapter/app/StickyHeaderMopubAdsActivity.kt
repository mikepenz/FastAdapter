package com.mikepenz.fastadapter.app

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.ButterKnife
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.mikepenz.fastadapter.app.adapters.MopubFastItemAdapter
import com.mikepenz.fastadapter.app.adapters.StickyHeaderAdapter
import com.mikepenz.fastadapter.app.helpers.CustomStickyRecyclerHeadersDecoration
import com.mikepenz.fastadapter.app.items.SimpleItem
import com.mopub.nativeads.MoPubRecyclerAdapter
import com.mopub.nativeads.MoPubStaticNativeAdRenderer
import com.mopub.nativeads.ViewBinder
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

/**
 * Created by Gagan on 5/3/2017.
 */

class StickyHeaderMopubAdsActivity : AppCompatActivity() {

    private lateinit var mAdapter: MopubFastItemAdapter<SimpleItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_sample)

        ButterKnife.bind(this)

        // Handle Toolbar
        setSupportActionBar(toolbar)

        val stickyHeaderAdapter = StickyHeaderAdapter<SimpleItem>()
        val headerAdapter = ItemAdapter<SimpleItem>()
        mAdapter = MopubFastItemAdapter()
        mAdapter.addAdapter(0, headerAdapter)

        val viewBinder = ViewBinder.Builder(R.layout.native_ad_item)
                .iconImageId(R.id.native_icon_image)
                .titleId(R.id.native_title)
                .textId(R.id.native_text)
                .callToActionId(R.id.native_cta)
                .privacyInformationIconImageId(R.id.native_privacy_information_icon_image)
                .build()

        val adapter = MoPubRecyclerAdapter(this, stickyHeaderAdapter.wrap(mAdapter))
        adapter.registerAdRenderer(MoPubStaticNativeAdRenderer(viewBinder))
        adapter.loadAds("76a3fefaced247959582d2d2df6f4757")

        rv.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        //provide the mopub adapter
        mAdapter.withMoPubAdAdapter(adapter)
        rv.itemAnimator = DefaultItemAnimator()
        rv.adapter = adapter

        //Note: Only major change to prevent Mopub Ads from pushing items out of the sections,
        //other than CustomHeaderViewCache, CustomStickyRecyclerHeadersDecoration, and HeaderPositionCalculator
        val decoration = CustomStickyRecyclerHeadersDecoration(stickyHeaderAdapter, adapter)
        rv.addItemDecoration(decoration)

        //fill with some sample data
        val items = ArrayList<SimpleItem>()
        for (i in 1..100) {
            val simpleItem = SimpleItem().withName("Test $i").withHeader(headers[i / 5])
            simpleItem.identifier = (100 + i).toLong()
            items.add(simpleItem)
        }
        mAdapter.add(items)

        //so the headers are aware of changes
        stickyHeaderAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                decoration.invalidateHeaders()
            }
        })

        //restore selections (this has to be done after the items were added
        mAdapter.withSavedInstanceState(savedInstanceState)

        //set the back arrow in the toolbar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(false)
    }

    override fun onSaveInstanceState(_outState: Bundle) {
        var outState = _outState
        //add the values which need to be saved from the adapter to the bundle
        outState = mAdapter.saveInstanceState(outState)
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

    companion object {
        private val headers = arrayOf("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z")
    }
}
