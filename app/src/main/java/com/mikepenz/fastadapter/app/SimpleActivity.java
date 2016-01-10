package com.mikepenz.fastadapter.app;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.app.items.SampleItem;
import com.mikepenz.fastadapter.helpers.UndoHelper;
import com.mikepenz.materialize.MaterializeBuilder;

import java.util.ArrayList;
import java.util.List;

public class SimpleActivity extends AppCompatActivity {
    //save our FastAdapter
    private FastAdapter<SampleItem> fastAdapter;

    private UndoHelper undoHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);

        // Handle Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        //style our ui
        new MaterializeBuilder().withActivity(this).build();

        //create our FastAdapter which will manage everything
        fastAdapter = new FastAdapter<>();

        //create our ItemAdapter which will host our items
        final ItemAdapter<SampleItem> itemAdapter = new ItemAdapter<>();

        //
        undoHelper = new UndoHelper(itemAdapter, new UndoHelper.UndoListener() {
            @Override
            public void commitRemove(int position, ArrayList<? extends IItem> removed) {
                Log.e("UndoHelper", "Pos: " + position + " Removed: " + removed.size());
                //remember that the items were removed
            }
        });

        //configure our fastAdapter
        //as we provide id's for the items we want the hasStableIds enabled to speed up things
        fastAdapter.setHasStableIds(true);
        fastAdapter.withOnClickListener(new FastAdapter.OnClickListener() {
            @Override
            public boolean onClick(View v, IAdapter adapter, IItem item, int position) {
                Toast.makeText(v.getContext(), ((SampleItem) item).name.getText(v.getContext()), Toast.LENGTH_LONG).show();
                return false;
            }
        });
        fastAdapter.withOnLongClickListener(new FastAdapter.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v, IAdapter adapter, IItem item, int position) {
                undoHelper.remove(SimpleActivity.this.findViewById(android.R.id.content), "Item removed", "Undo", Snackbar.LENGTH_LONG, position, 1);
                return true;
            }
        });

        //get our recyclerView and do basic setup
        RecyclerView rv = (RecyclerView) findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setItemAnimator(new DefaultItemAnimator());
        rv.setAdapter(itemAdapter.wrap(fastAdapter));

        //fill with some sample data
        List<SampleItem> items = new ArrayList<>();
        for (int i = 1; i <= 100; i++) {
            items.add(new SampleItem().withName("Test " + i).withIdentifier(100 + i));
        }
        itemAdapter.add(items);

        //restore selections (this has to be done after the items were added
        fastAdapter.withSavedInstanceState(savedInstanceState);

        //set the back arrow in the toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(false);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
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
}
