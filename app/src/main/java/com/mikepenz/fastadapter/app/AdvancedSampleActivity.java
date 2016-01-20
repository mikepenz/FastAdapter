package com.mikepenz.fastadapter.app;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.adapters.HeaderAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.app.adapter.StickyHeaderAdapter;
import com.mikepenz.fastadapter.app.items.SampleItem;
import com.mikepenz.fastadapter.helpers.ActionModeHelper;
import com.mikepenz.materialize.MaterializeBuilder;
import com.mikepenz.materialize.util.UIUtils;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * This sample showcases compatibility the awesome Sticky-Headers library by timehop
 * https://github.com/timehop/sticky-headers-recyclerview
 */
public class AdvancedSampleActivity extends AppCompatActivity {
    private static final String[] headers = new String[]{"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};

    //save our FastAdapter
    private FastAdapter mFastAdapter;

    private ActionModeHelper mActionModeHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);

        //improve ui
        findViewById(android.R.id.content).setSystemUiVisibility(findViewById(android.R.id.content).getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        // Handle Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.sample_advanced);

        //style our ui
        new MaterializeBuilder().withActivity(this).build();

        //create our FastAdapter
        mFastAdapter = new FastAdapter();

        //we init our ActionModeHelper
        mActionModeHelper = new ActionModeHelper(mFastAdapter, R.menu.cab, new ActionBarCallBack());

        //create our adapters
        final StickyHeaderAdapter stickyHeaderAdapter = new StickyHeaderAdapter();
        ItemAdapter itemAdapter = new ItemAdapter();
        final HeaderAdapter headerAdapter = new HeaderAdapter();

        //configure our mFastAdapter
        //as we provide id's for the items we want the hasStableIds enabled to speed up things
        mFastAdapter.setHasStableIds(true);
        mFastAdapter.withMultiSelect(true);
        mFastAdapter.withSelectOnLongClick(true);
        mFastAdapter.withOnPreClickListener(new FastAdapter.OnClickListener() {
            @Override
            public boolean onClick(View v, IAdapter adapter, IItem item, int position) {
                //we handle the default onClick behavior for the actionMode. This will return null if it didn't do anything and you can handle a normal onClick
                Boolean res = mActionModeHelper.onClick(item, position);
                return res != null ? res : false;
            }
        });

        mFastAdapter.withOnPreLongClickListener(new FastAdapter.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v, IAdapter adapter, IItem item, int position) {
                ActionMode actionMode = mActionModeHelper.onLongClick(AdvancedSampleActivity.this, position);

                if (actionMode != null) {
                    //we want color our CAB
                    findViewById(R.id.action_mode_bar).setBackgroundColor(UIUtils.getThemeColorFromAttrOrRes(AdvancedSampleActivity.this, R.attr.colorPrimary, R.color.material_drawer_primary));
                }

                //if we have no actionMode we do not consume the event
                return actionMode != null;
            }
        });

        //get our recyclerView and do basic setup
        RecyclerView rv = (RecyclerView) findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setItemAnimator(new DefaultItemAnimator());
        rv.setAdapter(stickyHeaderAdapter.wrap(itemAdapter.wrap(headerAdapter.wrap(mFastAdapter))));

        final StickyRecyclerHeadersDecoration decoration = new StickyRecyclerHeadersDecoration(stickyHeaderAdapter);
        rv.addItemDecoration(decoration);

        //fill with some sample data
        headerAdapter.add(new SampleItem().withName("Header").withSelectable(false).withIdentifier(1));
        List<IItem> items = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            SampleItem sampleItem = new SampleItem().withName("Test " + i).withHeader(headers[i / 5]).withIdentifier(100 + i);

            if (i % 10 == 0) {
                List<IItem> subItems = new LinkedList<>();
                for (int ii = 1; ii <= 3; ii++) {
                    SampleItem subItem = new SampleItem().withName("-- SubTest " + ii).withHeader(headers[i / 5]).withIdentifier(1000 + ii);

                    List<IItem> subSubItems = new LinkedList<>();
                    for (int iii = 1; iii <= 3; iii++) {
                        subSubItems.add(new SampleItem().withName("---- SubSubTest " + iii).withHeader(headers[i / 5]).withIdentifier(10000 + iii));
                    }
                    subItem.withSubItems(subSubItems);

                    subItems.add(subItem);
                }
                sampleItem.withSubItems(subItems);
            }
            items.add(sampleItem);
        }
        itemAdapter.add(items);

        //so the headers are aware of changes
        stickyHeaderAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                decoration.invalidateHeaders();
            }
        });

        //init cache with the added items, this is useful for shorter lists with many many different view types (at least 4 or more
        //new RecyclerViewCacheUtil().withCacheSize(2).apply(rv, items);

        //restore selections (this has to be done after the items were added
        mFastAdapter.withSavedInstanceState(savedInstanceState);

        //set the back arrow in the toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(false);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //add the values which need to be saved from the adapter to the bundel
        outState = mFastAdapter.saveInstanceState(outState);
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

    /**
     * Our ActionBarCallBack to showcase the CAB
     */
    class ActionBarCallBack implements ActionMode.Callback {

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            //logic if an item was clicked
            //return false as we want default behavior to go on
            return false;
        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(UIUtils.getThemeColorFromAttrOrRes(AdvancedSampleActivity.this, R.attr.colorPrimaryDark, R.color.material_drawer_primary_dark));
            }
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(Color.TRANSPARENT);
            }
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }
    }
}
