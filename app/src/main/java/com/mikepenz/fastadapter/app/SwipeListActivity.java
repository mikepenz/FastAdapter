package com.mikepenz.fastadapter.app;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
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

import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.IItemAdapter;
import com.mikepenz.fastadapter.app.items.SwipeableItem;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.listeners.OnClickListener;
import com.mikepenz.fastadapter_extensions.drag.ItemTouchCallback;
import com.mikepenz.fastadapter_extensions.drag.SimpleDragCallback;
import com.mikepenz.fastadapter_extensions.swipe.SimpleSwipeCallback;
import com.mikepenz.fastadapter_extensions.swipe.SimpleSwipeDragCallback;
import com.mikepenz.fastadapter_extensions.utilities.DragDropUtil;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.material_design_iconic_typeface_library.MaterialDesignIconic;
import com.mikepenz.materialize.MaterializeBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SwipeListActivity extends AppCompatActivity implements ItemTouchCallback, SimpleSwipeCallback.ItemSwipeCallback {
    private static final String[] ALPHABET = new String[]{"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};

    //save our FastAdapter
    private FastItemAdapter<SwipeableItem> fastItemAdapter;

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

        //create our FastAdapter which will manage everything
        fastItemAdapter = new FastItemAdapter<>();

        //configure our fastAdapter
        fastItemAdapter.withOnClickListener(new OnClickListener<SwipeableItem>() {
            @Override
            public boolean onClick(View v, IAdapter<SwipeableItem> adapter, @NonNull SwipeableItem item, int position) {
                Toast.makeText(v.getContext(), (item).name.getText(v.getContext()), Toast.LENGTH_LONG).show();
                return false;
            }
        });

        //configure the itemAdapter
        fastItemAdapter.getItemFilter().withFilterPredicate(new IItemAdapter.Predicate<SwipeableItem>() {
            @Override
            public boolean filter(SwipeableItem item, CharSequence constraint) {
                //return true if we should filter it out
                //return false to keep it
                return item.name.getText().toString().toLowerCase().contains(constraint.toString().toLowerCase());
            }
        });

        //get our recyclerView and do basic setup
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(fastItemAdapter);

        //fill with some sample data
        int x = 0;
        List<SwipeableItem> items = new ArrayList<>();
        for (String s : ALPHABET) {
            int count = new Random().nextInt(20);
            for (int i = 1; i <= count; i++) {
                SwipeableItem swipeableItem = new SwipeableItem().withName(s + " Test " + x).withIdentifier(100 + x);
                swipeableItem.withIsSwipeable(i % 5 != 0);
                swipeableItem.withIsDraggable(i % 5 != 0);
                items.add(swipeableItem);
                x++;
            }
        }
        fastItemAdapter.add(items);


        //add drag and drop for item
        //and add swipe as well
        Drawable leaveBehindDrawableLeft = new IconicsDrawable(this)
                .icon(MaterialDesignIconic.Icon.gmi_delete)
                .color(Color.WHITE)
                .sizeDp(24);
        Drawable leaveBehindDrawableRight = new IconicsDrawable(this)
                .icon(MaterialDesignIconic.Icon.gmi_archive)
                .color(Color.WHITE)
                .sizeDp(24);

        touchCallback = new SimpleSwipeDragCallback(
                this,
                this,
                leaveBehindDrawableLeft,
                ItemTouchHelper.LEFT,
                ContextCompat.getColor(this, R.color.md_red_900)
        )
                .withBackgroundSwipeRight(ContextCompat.getColor(this, R.color.md_blue_900))
                .withLeaveBehindSwipeRight(leaveBehindDrawableRight);

        touchHelper = new ItemTouchHelper(touchCallback); // Create ItemTouchHelper and pass with parameter the SimpleDragCallback
        touchHelper.attachToRecyclerView(recyclerView); // Attach ItemTouchHelper to RecyclerView

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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search, menu);

        //search icon
        menu.findItem(R.id.search).setIcon(new IconicsDrawable(this, MaterialDesignIconic.Icon.gmi_search).color(Color.BLACK).actionBar());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            final SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String s) {
                    touchCallback.setIsDragEnabled(false);
                    fastItemAdapter.filter(s);
                    return true;
                }


                @Override
                public boolean onQueryTextChange(String s) {
                    fastItemAdapter.filter(s);
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
        DragDropUtil.onMove(fastItemAdapter.getItemAdapter(), oldPosition, newPosition);  // change position
        return true;
    }

    @Override
    public void itemTouchDropped(int oldPosition, int newPosition) {
        // save the new item order, i.e. in your database
    }

    @Override
    public void itemSwiped(int position, int direction) {
        // -- Option 1: Direct action --
        //do something when swiped such as: select, remove, update, ...:
        //A) fastItemAdapter.select(position);
        //B) fastItemAdapter.remove(position);
        //C) update item, set "read" if an email etc

        // -- Option 2: Delayed action --
        final SwipeableItem item = fastItemAdapter.getItem(position);
        item.setSwipedDirection(direction);

        // This can vary depending on direction but remove & archive simulated here both results in
        // removal from list
        final Runnable removeRunnable = new Runnable() {
            @Override
            public void run() {
                item.setSwipedAction(null);
                int position = fastItemAdapter.getAdapterPosition(item);
                if (position != RecyclerView.NO_POSITION) {
                    //this sample uses a filter. If a filter is used we should use the methods provided by the filter (to make sure filter and normal state is updated)
                    fastItemAdapter.getItemFilter().remove(position);
                }
            }
        };
        final View rv = findViewById(R.id.rv);
        rv.postDelayed(removeRunnable, 3000);

        item.setSwipedAction(new Runnable() {
            @Override
            public void run() {
                rv.removeCallbacks(removeRunnable);
                item.setSwipedDirection(0);
                int position = fastItemAdapter.getAdapterPosition(item);
                if (position != RecyclerView.NO_POSITION) {
                    fastItemAdapter.notifyItemChanged(position);
                }
            }
        });

        fastItemAdapter.notifyItemChanged(position);

        //TODO can this above be made more generic, along with the support in the item?
    }

}
