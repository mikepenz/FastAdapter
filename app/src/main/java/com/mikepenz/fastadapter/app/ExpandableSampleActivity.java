package com.mikepenz.fastadapter.app;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.app.items.expandable.SimpleSubExpandableItem;
import com.mikepenz.fastadapter.app.items.expandable.SimpleSubItem;
import com.mikepenz.fastadapter.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.expandable.ExpandableExtension;
import com.mikepenz.fastadapter.select.SelectExtension;
import com.mikepenz.iconics.context.IconicsLayoutInflater;
import com.mikepenz.itemanimators.SlideDownAlphaAnimator;
import com.mikepenz.materialize.MaterializeBuilder;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.LayoutInflaterCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ExpandableSampleActivity extends AppCompatActivity {
    //save our FastAdapter
    private FastItemAdapter<IItem<? extends RecyclerView.ViewHolder>> fastItemAdapter;
    private ExpandableExtension expandableExtension;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        findViewById(android.R.id.content).setSystemUiVisibility(findViewById(android.R.id.content).getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        //as we use an icon from Android-Iconics via xml we add the IconicsLayoutInflater
        //https://github.com/mikepenz/Android-Iconics
        LayoutInflaterCompat.setFactory(getLayoutInflater(), new IconicsLayoutInflater(getDelegate()));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);

        // Handle Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.sample_collapsible);

        //style our ui
        new MaterializeBuilder().withActivity(this).build();

        //create our FastAdapter
        fastItemAdapter = new FastItemAdapter<>();
        SelectExtension selectExtension = new SelectExtension<>(fastItemAdapter);
        fastItemAdapter.addExtension(selectExtension);
        selectExtension.setSelectable(true);
        expandableExtension = fastItemAdapter.getOrCreateExtension(ExpandableExtension.class);
        //expandableExtension.withOnlyOneExpandedItem(true);

        //get our recyclerView and do basic setup
        RecyclerView rv = findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setItemAnimator(new SlideDownAlphaAnimator());
        rv.setAdapter(fastItemAdapter);

        //fill with some sample data
        List<IItem<? extends RecyclerView.ViewHolder>> items = new ArrayList<>();
        AtomicInteger identifier = new AtomicInteger(1);
        for (int i = 1; i <= 100; i++) {
            if (i % 3 != 0) {
                SimpleSubItem simpleSubItem = new SimpleSubItem().withName("Test " + i);
                simpleSubItem.setIdentifier(identifier.getAndIncrement());
                items.add(simpleSubItem);
                continue;
            }

            SimpleSubExpandableItem parent = new SimpleSubExpandableItem();
            parent.withName("Test " + i).setIdentifier(identifier.getAndIncrement());

            List<SimpleSubExpandableItem> subItems = new LinkedList<>();
            for (int ii = 1; ii <= 5; ii++) {
                SimpleSubExpandableItem subItem = new SimpleSubExpandableItem();
                subItem.withName("-- SubTest " + ii).setIdentifier(identifier.getAndIncrement());

                if (ii % 2 == 0) {
                    continue;
                }

                List<SimpleSubExpandableItem> subSubItems = new LinkedList<>();
                for (int iii = 1; iii <= 3; iii++) {
                    SimpleSubExpandableItem subSubItem = new SimpleSubExpandableItem();
                    subSubItem.withName("---- SubSubTest " + iii).setIdentifier(identifier.getAndIncrement());

                    List<SimpleSubExpandableItem> subSubSubItems = new LinkedList<>();
                    for (int iiii = 1; iiii <= 4; iiii++) {
                        SimpleSubExpandableItem subSubSubItem = new SimpleSubExpandableItem();
                        subSubSubItem.withName("---- SubSubSubTest " + iiii).setIdentifier(identifier.getAndIncrement());
                        subSubSubItems.add(subSubSubItem);
                    }
                    subSubItem.setSubItems(subSubSubItems);
                    subSubItems.add(subSubItem);
                }
                subItem.setSubItems(subSubItems);
                subItems.add(subItem);
            }
            parent.setSubItems(subItems);
            items.add(parent);
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
        //add the values which need to be saved from the adapter to the bundle
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
