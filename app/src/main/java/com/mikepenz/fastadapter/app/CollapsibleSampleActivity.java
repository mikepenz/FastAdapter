package com.mikepenz.fastadapter.app;

import android.os.Bundle;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.IExpandable;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.app.items.ExpandableItem;
import com.mikepenz.fastadapter.app.items.SampleItem;
import com.mikepenz.iconics.context.IconicsLayoutInflater;
import com.mikepenz.itemanimators.SlideDownAlphaAnimator;
import com.mikepenz.materialize.MaterializeBuilder;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class CollapsibleSampleActivity extends AppCompatActivity {
    //save our FastAdapter
    private FastItemAdapter fastItemAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        findViewById(android.R.id.content).setSystemUiVisibility(findViewById(android.R.id.content).getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        //as we use an icon from Android-Iconics via xml we add the IconicsLayoutInflater
        //https://github.com/mikepenz/Android-Iconics
        LayoutInflaterCompat.setFactory(getLayoutInflater(), new IconicsLayoutInflater(getDelegate()));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);

        // Handle Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.sample_collapsible);

        //style our ui
        new MaterializeBuilder().withActivity(this).build();

        //create our FastAdapter
        fastItemAdapter = new FastItemAdapter();

        //configure our fastAdapter
        fastItemAdapter.withOnPreClickListener(new FastAdapter.OnClickListener() {
            @Override
            public boolean onClick(View v, IAdapter adapter, IItem item, int position) {
                if (item instanceof ExpandableItem) {
                    if (((ExpandableItem) item).getSubItems() != null) {
                        if (((IExpandable) item).isExpanded()) {
                            ViewCompat.animate(v.findViewById(R.id.material_drawer_icon)).rotation(90).start();
                        } else {
                            ViewCompat.animate(v.findViewById(R.id.material_drawer_icon)).rotation(0).start();
                        }

                        fastItemAdapter.toggleExpandable(position);
                        return true;
                    }
                }
                return false;
            }
        });

        //get our recyclerView and do basic setup
        RecyclerView rv = (RecyclerView) findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setItemAnimator(new SlideDownAlphaAnimator());
        rv.setAdapter(fastItemAdapter);

        //fill with some sample data
        List<IItem> items = new ArrayList<>();
        for (int i = 1; i <= 100; i++) {
            if (i % 10 == 0) {
                ExpandableItem expandableItem = new ExpandableItem().withName("Test " + i).withIdentifier(100 + 1);

                //add subitems so we can showcase the collapsible functionality
                List<IItem> subItems = new LinkedList<>();
                for (int ii = 1; ii <= 5; ii++) {
                    subItems.add(new SampleItem().withName("-- Test " + ii).withIdentifier(1000 + ii));
                }
                expandableItem.withSubItems(subItems);

                items.add(expandableItem);
            } else {
                items.add(new SampleItem().withName("Test " + i).withIdentifier(100 + i));
            }
        }
        fastItemAdapter.add(items);

        //restore selections (this has to be done after the items were added
        fastItemAdapter.withSavedInstanceState(savedInstanceState);

        //set the back arrow in the toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(false);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //add the values which need to be saved from the adapter to the bundel
        outState = fastItemAdapter.saveInstanceState(outState);
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
