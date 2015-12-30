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
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.adapters.HeaderAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.app.adapter.StickyHeaderAdapter;
import com.mikepenz.fastadapter.app.items.SampleItem;
import com.mikepenz.materialdrawer.Drawer;
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

    //save our header or result
    private Drawer result = null;
    //save our FastAdapter
    private FastAdapter fastAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);

        // Handle Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.sample_advanced);

        //style our ui
        new MaterializeBuilder().withActivity(this).build();

        //create our FastAdapter
        fastAdapter = new FastAdapter();

        //create our adapters
        final StickyHeaderAdapter stickyHeaderAdapter = new StickyHeaderAdapter();
        final HeaderAdapter headerAdapter = new HeaderAdapter();
        final ItemAdapter itemAdapter = new ItemAdapter();

        //configure our fastAdapter
        //as we provide id's for the items we want the hasStableIds enabled to speed up things
        fastAdapter.setHasStableIds(true);
        fastAdapter.withMultiSelect(true);
        fastAdapter.withMultiSelectOnLongClick(true);
        fastAdapter.withOnLongClickListener(new FastAdapter.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v, int position, int relativePosition, IItem item) {
                //may check if actionMode is already displayed
                startSupportActionMode(new ActionBarCallBack());
                findViewById(R.id.action_mode_bar).setBackgroundColor(UIUtils.getThemeColorFromAttrOrRes(AdvancedSampleActivity.this, R.attr.colorPrimary, R.color.material_drawer_primary));

                //itemAdapter.removeItemRange(relativePosition, 5);
                //itemAdapter.add(position, new PrimaryItem().withName("Awesome :D").withLevel(2).withIdentifier(fastAdapter.getItemCount() + 1000));
                return false;
            }
        });
        fastAdapter.withOnClickListener(new FastAdapter.OnClickListener() {
            @Override
            public boolean onClick(View v, int position, int relativePosition, IItem item) {
                if (item instanceof SampleItem) {
                    if (((SampleItem) item).getSubItems() != null) {
                        fastAdapter.toggleCollapsible(position);
                        return true;
                    }
                }
                //Toast.makeText(v.getContext(), ((SectionItem) item).getName().getText(v.getContext()), Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        //get our recyclerView and do basic setup
        RecyclerView rv = (RecyclerView) findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setItemAnimator(new DefaultItemAnimator());
        rv.setAdapter(stickyHeaderAdapter.wrap(itemAdapter.wrap(headerAdapter.wrap(fastAdapter))));

        final StickyRecyclerHeadersDecoration decoration = new StickyRecyclerHeadersDecoration(stickyHeaderAdapter);
        rv.addItemDecoration(decoration);

        //fill with some sample data
        headerAdapter.add(new SampleItem().withName("Header").withIdentifier(1));
        List<IItem> items = new ArrayList<>();
        for (int i = 1; i <= 100; i++) {
            SampleItem sampleItem = new SampleItem().withName("Test " + i).withHeader(headers[i / 5]).withIdentifier(100 + i);

            if (i % 10 == 0) {
                List<IItem> subItems = new LinkedList<>();
                for (int ii = 1; ii <= 5; ii++) {
                    subItems.add(new SampleItem().withName("-- Test " + ii).withHeader(headers[i / 5]).withIdentifier(1000 + ii));
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
        fastAdapter.withSavedInstanceState(savedInstanceState);

        //set the back arrow in the toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(false);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //add the values which need to be saved from the drawer to the bundle
        outState = result.saveInstanceState(outState);
        //add the values which need to be saved from the adapter to the bundel
        outState = fastAdapter.saveInstanceState(outState);
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
            //at the moment just one item which removes selection
            fastAdapter.deselect();
            //after selection is removed we probably want finish the actionMode
            mode.finish();
            return true;
        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(UIUtils.getThemeColorFromAttrOrRes(AdvancedSampleActivity.this, R.attr.colorPrimaryDark, R.color.material_drawer_primary_dark));
            }
            mode.getMenuInflater().inflate(R.menu.cab, menu);

            //as we are now in the actionMode a single click is fine for multiSelection
            fastAdapter.withMultiSelectOnLongClick(false);
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(Color.TRANSPARENT);
            }

            //after we are done with the actionMode we fallback to longClick for multiselect
            fastAdapter.withMultiSelectOnLongClick(true);
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }
    }
}
