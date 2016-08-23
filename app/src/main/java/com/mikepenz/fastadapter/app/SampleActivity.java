package com.mikepenz.fastadapter.app;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.app.dummy.ImageDummyData;
import com.mikepenz.fastadapter.app.items.SimpleImageItem;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.itemanimators.SlideDownAlphaAnimator;
import com.mikepenz.material_design_iconic_typeface_library.MaterialDesignIconic;

import java.util.List;

public class SampleActivity extends AppCompatActivity {

    //our rv
    RecyclerView mRecyclerView;
    //save our FastAdapter
    private FastAdapter<SimpleImageItem> mFastAdapter;
    //save our FastAdapter
    private ItemAdapter<SimpleImageItem> mItemAdapter;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        findViewById(android.R.id.content).setSystemUiVisibility(findViewById(android.R.id.content).getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        //create the activity
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // Handle Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");


        //create our FastAdapter which will manage everything
        mFastAdapter = new FastAdapter<>();
        mFastAdapter.withSelectable(true);
        mFastAdapter.withMultiSelect(true);
        mFastAdapter.withSelectOnLongClick(false);
        //create our ItemAdapter which will host our items
        mItemAdapter = new ItemAdapter<>();

        //configure our fastAdapter
        //get our recyclerView and do basic setup
        mRecyclerView = (RecyclerView) findViewById(R.id.rv);
        //mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mItemAdapter.wrap(mFastAdapter));
        mRecyclerView.setItemAnimator(new SlideDownAlphaAnimator());
        mRecyclerView.getItemAnimator().setAddDuration(500);
        mRecyclerView.getItemAnimator().setRemoveDuration(500);

        //if we do this. the first added items will be animated :D
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //add some dummy data
                mItemAdapter.add(ImageDummyData.getSimpleImageItems());

                //restore selections (this has to be done after the items were added
                mFastAdapter.withSavedInstanceState(savedInstanceState);
            }
        }, 50);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        menu.findItem(R.id.item_add).setIcon(new IconicsDrawable(this, MaterialDesignIconic.Icon.gmi_plus_square).color(Color.BLACK).actionBar());
        menu.findItem(R.id.item_delete).setIcon(new IconicsDrawable(this, MaterialDesignIconic.Icon.gmi_minus_square).color(Color.BLACK).actionBar());
        menu.findItem(R.id.item_change).setIcon(new IconicsDrawable(this, MaterialDesignIconic.Icon.gmi_settings_square).color(Color.BLACK).actionBar());
        menu.findItem(R.id.item_move).setIcon(new IconicsDrawable(this, MaterialDesignIconic.Icon.gmi_format_valign_bottom).color(Color.BLACK).actionBar());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //find out the current visible position
        int firstVisiblePosition = 0;
        if (mRecyclerView.getLayoutManager() instanceof LinearLayoutManager) {
            firstVisiblePosition = ((LinearLayoutManager) mRecyclerView.getLayoutManager()).findFirstVisibleItemPosition();
        } else if (mRecyclerView.getLayoutManager() instanceof GridLayoutManager) {
            firstVisiblePosition = ((GridLayoutManager) mRecyclerView.getLayoutManager()).findFirstVisibleItemPosition();
        }

        //handle the menu item click
        switch (item.getItemId()) {
            case R.id.item_add:
                mItemAdapter.add(firstVisiblePosition + 1, ImageDummyData.getDummyItem());
                return true;
            case R.id.item_change:
                for (Integer pos : mFastAdapter.getSelections()) {
                    SimpleImageItem i = mItemAdapter.getItem(pos);
                    i.withName("CHANGED");
                    i.withDescription("This item was modified");
                    mItemAdapter.set(pos, i);
                }
                return true;
            case R.id.item_move:
                List items = mItemAdapter.getAdapterItems();
                if (items.size() > firstVisiblePosition + 3) {
                    mItemAdapter.move(firstVisiblePosition + 1, firstVisiblePosition + 3);
                }
                return true;
            case R.id.item_delete:
                mFastAdapter.deleteAllSelectedItems();
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //add the values which need to be saved from the adapter to the bundel
        outState = mFastAdapter.saveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }
}
