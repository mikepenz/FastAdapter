package com.mikepenz.fastadapter.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.app.items.SampleItem;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import java.util.ArrayList;
import java.util.List;

public class SampleActivity extends AppCompatActivity {
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

        //Create the drawer
        result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withHasStableIds(true)
                .withSavedInstance(savedInstanceState)
                .withShowDrawerOnFirstLaunch(true)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.sample_multi_select).withSelectable(false).withIdentifier(1),
                        new PrimaryDrawerItem().withName(R.string.sample_collapsible).withSelectable(false).withIdentifier(2),
                        new PrimaryDrawerItem().withName(R.string.sample_sticky_header).withSelectable(false).withIdentifier(3),
                        new PrimaryDrawerItem().withName(R.string.sample_advanced).withSelectable(false).withDescription(R.string.sample_advanced_descr).withIdentifier(4)
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        if (drawerItem != null) {
                            Intent intent = null;
                            if (drawerItem.getIdentifier() == 1) {
                                intent = new Intent(SampleActivity.this, MultiselectSampleActivity.class);
                            } else if (drawerItem.getIdentifier() == 2) {
                                intent = new Intent(SampleActivity.this, CollapsibleSampleActivity.class);
                            } else if (drawerItem.getIdentifier() == 3) {
                                intent = new Intent(SampleActivity.this, StickyHeaderSampleActivity.class);
                            } else if (drawerItem.getIdentifier() == 4) {
                                intent = new Intent(SampleActivity.this, AdvancedSampleActivity.class);
                            }
                            if (intent != null) {
                                SampleActivity.this.startActivity(intent);
                            }
                        }
                        return false;
                    }
                })
                .withSelectedItemByPosition(-1)
                .build();

        //create our FastAdapter which will manage everything
        fastAdapter = new FastAdapter();

        //create our ItemAdapter which will host our items
        final ItemAdapter itemAdapter = new ItemAdapter();

        //configure our fastAdapter
        //as we provide id's for the items we want the hasStableIds enabled to speed up things
        fastAdapter.setHasStableIds(true);
        fastAdapter.withOnClickListener(new FastAdapter.OnClickListener() {
            @Override
            public boolean onClick(View v, int position, int relativePosition, IItem item) {
                Toast.makeText(v.getContext(), ((SampleItem) item).name.getText(v.getContext()), Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        //get our recyclerView and do basic setup
        RecyclerView rv = (RecyclerView) findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setItemAnimator(new DefaultItemAnimator());
        rv.setAdapter(itemAdapter.wrap(fastAdapter));

        //fill with some sample data
        List<IItem> items = new ArrayList<>();
        for (int i = 1; i <= 100; i++) {
            items.add(new SampleItem().withName("Test " + i).withIdentifier(100 + i));
        }
        itemAdapter.add(items);

        //init cache with the added items, this is useful for shorter lists with many many different view types (at least 4 or more
        //new RecyclerViewCacheUtil().withCacheSize(2).apply(rv, items);

        //restore selections (this has to be done after the items were added
        fastAdapter.withSavedInstanceState(savedInstanceState);
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
    public void onBackPressed() {
        //handle the back press :D close the drawer first and if the drawer is closed close the activity
        if (result != null && result.isDrawerOpen()) {
            result.closeDrawer();
        } else {
            super.onBackPressed();
        }
    }
}
