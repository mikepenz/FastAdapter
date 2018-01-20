package com.mikepenz.fastadapter.app;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.IItemAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.app.adapters.FastScrollIndicatorAdapter;
import com.mikepenz.fastadapter.app.items.SimpleItem;
import com.mikepenz.fastadapter.listeners.ItemFilterListener;
import com.mikepenz.fastadapter.listeners.OnClickListener;
import com.mikepenz.fastadapter_extensions.drag.ItemTouchCallback;
import com.mikepenz.fastadapter_extensions.drag.SimpleDragCallback;
import com.mikepenz.fastadapter_extensions.utilities.DragDropUtil;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.material_design_iconic_typeface_library.MaterialDesignIconic;
import com.mikepenz.materialize.MaterializeBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.mikepenz.fastadapter.adapters.ItemAdapter.items;

public class SimpleItemListActivity extends AppCompatActivity implements ItemTouchCallback, ItemFilterListener<SimpleItem> {
    private static final String[] ALPHABET = new String[]{"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};

    //save our FastAdapter
    private FastAdapter<SimpleItem> fastAdapter;
    private ItemAdapter<SimpleItem> itemAdapter;

    //drag & drop
    private SimpleDragCallback touchCallback;
    private ItemTouchHelper touchHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        findViewById(android.R.id.content).setSystemUiVisibility(findViewById(android.R.id.content).getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);

        // Handle Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        //style our ui
        new MaterializeBuilder().withActivity(this).build();

        //
        FastScrollIndicatorAdapter fastScrollIndicatorAdapter = new FastScrollIndicatorAdapter();
        itemAdapter = items();

        //create our FastAdapter which will manage everything
        fastAdapter = FastAdapter.with(itemAdapter);
        fastAdapter.withSelectable(true);

        //configure our fastAdapter
        fastAdapter.withOnClickListener(new OnClickListener<SimpleItem>() {
            @Override
            public boolean onClick(View v, IAdapter<SimpleItem> adapter, @NonNull SimpleItem item, int position) {
                Toast.makeText(v.getContext(), (item).name.getText(v.getContext()), Toast.LENGTH_LONG).show();
                return false;
            }
        });

        //configure the itemAdapter
        itemAdapter.getItemFilter().withFilterPredicate(new IItemAdapter.Predicate<SimpleItem>() {
            @Override
            public boolean filter(SimpleItem item, CharSequence constraint) {
                //return true if we should filter it out
                //return false to keep it
                return item.name.getText().toString().toLowerCase().contains(constraint.toString().toLowerCase());
            }
        });

        itemAdapter.getItemFilter().withItemFilterListener(this);

        //get our recyclerView and do basic setup
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(fastScrollIndicatorAdapter.wrap(fastAdapter));

        //add a FastScrollBar (Showcase compatibility)
        //DragScrollBar materialScrollBar = new DragScrollBar(this, recyclerView, true);
        //materialScrollBar.setHandleColour(ContextCompat.getColor(this, R.color.accent));
        //materialScrollBar.addIndicator(new AlphabetIndicator(this), true);

        //fill with some sample data
        int x = 0;
        List<SimpleItem> items = new ArrayList<>();
        for (String s : ALPHABET) {
            int count = new Random().nextInt(20);
            for (int i = 1; i <= count; i++) {
                SimpleItem item = new SimpleItem().withName(s + " Test " + x).withIdentifier(100 + x);
                items.add(item);
                x++;
            }
        }
        itemAdapter.add(items);

        //add drag and drop for item
        touchCallback = new SimpleDragCallback(this);
        touchHelper = new ItemTouchHelper(touchCallback); // Create ItemTouchHelper and pass with parameter the SimpleDragCallback
        touchHelper.attachToRecyclerView(recyclerView); // Attach ItemTouchHelper to RecyclerView

        //restore selections (this has to be done after the items were added
        fastAdapter.withSavedInstanceState(savedInstanceState);

        //set the back arrow in the toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(false);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //add the values which need to be saved from the adapter to the bundle
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search, menu);

        //search icon
        menu.findItem(R.id.search).setIcon(new IconicsDrawable(this, MaterialDesignIconic.Icon.gmi_search).color(Color.BLACK).actionBar());

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            final SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String s) {
                    touchCallback.setIsDragEnabled(false);
                    itemAdapter.filter(s);
                    return true;
                }


                @Override
                public boolean onQueryTextChange(String s) {
                    itemAdapter.filter(s);
                    touchCallback.setIsDragEnabled(TextUtils.isEmpty(s));
                    return true;
                }
            });
        } else {
            menu.findItem(R.id.search).setVisible(false);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean itemTouchOnMove(int oldPosition, int newPosition) {
        DragDropUtil.onMove(itemAdapter, oldPosition, newPosition);  // change position
        return true;
    }

    @Override
    public void itemTouchDropped(int oldPosition, int newPosition) {
        // save the new item order, i.e. in your database
    }

    @Override
    public void itemsFiltered(@Nullable CharSequence constraint, @Nullable List<SimpleItem> results) {
        Toast.makeText(SimpleItemListActivity.this, "filtered items count: " + itemAdapter.getAdapterItemCount(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onReset() {

    }
}
