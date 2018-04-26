package com.mikepenz.fastadapter.app;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.app.adapters.MopubFastItemAdapter;
import com.mikepenz.fastadapter.app.items.LetterItem;
import com.mikepenz.fastadapter.listeners.OnClickListener;
import com.mopub.nativeads.MoPubRecyclerAdapter;
import com.mopub.nativeads.MoPubStaticNativeAdRenderer;
import com.mopub.nativeads.ViewBinder;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MopubAdsActivity extends AppCompatActivity implements OnClickListener<LetterItem> {

    private MopubFastItemAdapter<LetterItem> mAdapter;


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

        mAdapter = new MopubFastItemAdapter<>();
        mAdapter.withOnClickListener(this);

        for (int i = 65; i <= 90; i++) {
            mAdapter.add(new LetterItem(String.valueOf((char) i)));
        }

        ViewBinder viewBinder = new ViewBinder.Builder(R.layout.native_ad_item)
                .iconImageId(R.id.native_icon_image)
                .titleId(R.id.native_title)
                .textId(R.id.native_text)
                .callToActionId(R.id.native_cta)
                .privacyInformationIconImageId(R.id.native_privacy_information_icon_image)
                .build();

        MoPubRecyclerAdapter adapter = new MoPubRecyclerAdapter(this, mAdapter);
        adapter.registerAdRenderer(new MoPubStaticNativeAdRenderer(viewBinder));
        adapter.loadAds("76a3fefaced247959582d2d2df6f4757");

        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        mRecyclerView.setAdapter(adapter);

        //provide the mopub adapter
        mAdapter.withMoPubAdAdapter(adapter);

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
