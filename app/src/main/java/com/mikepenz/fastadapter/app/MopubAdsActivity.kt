package com.mikepenz.fastadapter.app

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import butterknife.ButterKnife
import com.mikepenz.fastadapter.app.adapters.MopubFastItemAdapter
import com.mikepenz.fastadapter.app.items.LetterItem
import com.mopub.nativeads.MoPubRecyclerAdapter
import com.mopub.nativeads.MoPubStaticNativeAdRenderer
import com.mopub.nativeads.ViewBinder
import kotlinx.android.synthetic.main.activity_main.*

class MopubAdsActivity : AppCompatActivity() {

    private lateinit var adapter: MopubFastItemAdapter<LetterItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_sample)

        ButterKnife.bind(this)

        // Handle Toolbar
        setSupportActionBar(toolbar)

        adapter = MopubFastItemAdapter()
        adapter.onClickListener = { _, _, item, position ->
            Toast.makeText(this, "Item pressed " + item.letter + " at position " + position, Toast.LENGTH_SHORT).show()
            false
        }

        for (i in 65..90) {
            adapter.add(LetterItem(i.toChar().toString()))
        }

        val viewBinder = ViewBinder.Builder(R.layout.native_ad_item)
                .iconImageId(R.id.native_icon_image)
                .titleId(R.id.native_title)
                .textId(R.id.native_text)
                .callToActionId(R.id.native_cta)
                .privacyInformationIconImageId(R.id.native_privacy_information_icon_image)
                .build()

        val adapter = MoPubRecyclerAdapter(this, adapter)
        adapter.registerAdRenderer(MoPubStaticNativeAdRenderer(viewBinder))
        adapter.loadAds("76a3fefaced247959582d2d2df6f4757")

        rv.layoutManager = GridLayoutManager(this, 1)
        rv.adapter = adapter

        //provide the mopub adapter
        this.adapter.withMoPubAdAdapter(adapter)

        //restore selections (this has to be done after the items were added
        this.adapter.withSavedInstanceState(savedInstanceState)

        //set the back arrow in the toolbar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(false)
    }

    override fun onSaveInstanceState(_outState: Bundle) {
        var outState = _outState
        //add the values which need to be saved from the adapter to the bundle
        outState = adapter.saveInstanceState(outState)
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
