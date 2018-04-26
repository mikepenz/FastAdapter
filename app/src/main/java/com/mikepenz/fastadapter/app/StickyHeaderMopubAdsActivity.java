package com.mikepenz.fastadapter.app;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.app.adapters.MopubFastItemAdapter;
import com.mikepenz.fastadapter.app.adapters.StickyHeaderAdapter;
import com.mikepenz.fastadapter.app.helpers.CustomStickyRecyclerHeadersDecoration;
import com.mikepenz.fastadapter.app.items.LetterItem;
import com.mikepenz.fastadapter.app.items.SimpleItem;
import com.mikepenz.fastadapter.listeners.OnClickListener;
import com.mopub.nativeads.MoPubRecyclerAdapter;
import com.mopub.nativeads.MoPubStaticNativeAdRenderer;
import com.mopub.nativeads.ViewBinder;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Gagan on 5/3/2017.
 */

public class StickyHeaderMopubAdsActivity extends AppCompatActivity implements OnClickListener<LetterItem> {

    private MopubFastItemAdapter<IItem> mAdapter;
    private static final String[] headers = new String[]{"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.rv)
    RecyclerView mRecyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        findViewById(android.R.id.content).setSystemUiVisibility(findViewById(android.R.id.content)
                .getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        // Handle Toolbar
        setSupportActionBar(toolbar);

        final StickyHeaderAdapter stickyHeaderAdapter = new StickyHeaderAdapter();
        final ItemAdapter headerAdapter = new ItemAdapter();
        mAdapter = new MopubFastItemAdapter<>();
        mAdapter.addAdapter(0, headerAdapter);

        ViewBinder viewBinder = new ViewBinder.Builder(R.layout.native_ad_item)
                .iconImageId(R.id.native_icon_image)
                .titleId(R.id.native_title)
                .textId(R.id.native_text)
                .callToActionId(R.id.native_cta)
                .privacyInformationIconImageId(R.id.native_privacy_information_icon_image)
                .build();

        MoPubRecyclerAdapter adapter = new MoPubRecyclerAdapter(this, stickyHeaderAdapter.wrap(mAdapter));
        adapter.registerAdRenderer(new MoPubStaticNativeAdRenderer(viewBinder));
        adapter.loadAds("76a3fefaced247959582d2d2df6f4757");

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        //provide the mopub adapter
        mAdapter.withMoPubAdAdapter(adapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(adapter);

        //Note: Only major change to prevent Mopub Ads from pushing items out of the sections,
        //other than CustomHeaderViewCache, CustomStickyRecyclerHeadersDecoration, and HeaderPositionCalculator
        final CustomStickyRecyclerHeadersDecoration decoration = new CustomStickyRecyclerHeadersDecoration(stickyHeaderAdapter, adapter);
        mRecyclerView.addItemDecoration(decoration);

        //fill with some sample data
        List<IItem> items = new ArrayList<>();
        for (int i = 1; i <= 100; i++) {
            items.add(new SimpleItem().withName("Test " + i).withHeader(headers[i / 5]).withIdentifier(100 + i));
        }
        mAdapter.add(items);

        //so the headers are aware of changes
        stickyHeaderAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                decoration.invalidateHeaders();
            }
        });


        //restore selections (this has to be done after the items were added
        mAdapter.withSavedInstanceState(savedInstanceState);

        //set the back arrow in the toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(false);
    }

    @Override
    public boolean onClick(View v, IAdapter<LetterItem> adapter, @NonNull LetterItem item, int position) {
        Toast.makeText(this, "Item pressed " + item.letter + " at position " + position, Toast.LENGTH_SHORT).show();
        return false;
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //add the values which need to be saved from the adapter to the bundle
        outState = mAdapter.saveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //handle the click on the back arrow click
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
