package com.mikepenz.fastadapter.app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.app.items.CheckBoxSampleItem;
import com.mikepenz.fastadapter.helpers.ClickListenerHelper;
import com.mikepenz.materialize.MaterializeBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CheckBoxSampleActivity extends AppCompatActivity {
    private static final String[] ALPHABET = new String[]{"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};

    //save our FastAdapter
    private FastItemAdapter<CheckBoxSampleItem> fastItemAdapter;

    private ClickListenerHelper<CheckBoxSampleItem> mClickListenerHelper;

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
        fastItemAdapter.withSelectable(true);

        //configure our fastAdapter
        fastItemAdapter.withOnClickListener(new FastAdapter.OnClickListener<CheckBoxSampleItem>() {
            @Override
            public boolean onClick(View v, IAdapter<CheckBoxSampleItem> adapter, CheckBoxSampleItem item, int position) {
                Toast.makeText(v.getContext(), (item).name.getText(v.getContext()), Toast.LENGTH_LONG).show();
                return false;
            }
        });

        //init the ClickListenerHelper which simplifies custom click listeners on views of the Adapter
        mClickListenerHelper = new ClickListenerHelper<>(fastItemAdapter);

        fastItemAdapter.withOnPreClickListener(new FastAdapter.OnClickListener<CheckBoxSampleItem>() {
            @Override
            public boolean onClick(View v, IAdapter<CheckBoxSampleItem> adapter, CheckBoxSampleItem item, int position) {
                // consume otherwise radio/checkbox will be deselected
                return true;
            }
        });

        fastItemAdapter.withOnCreateViewHolderListener(new FastAdapter.OnCreateViewHolderListener() {
            @Override
            public RecyclerView.ViewHolder onPreCreateViewHolder(ViewGroup parent, int viewType) {
                return fastItemAdapter.getTypeInstance(viewType).getViewHolder(parent);
            }

            @Override
            public RecyclerView.ViewHolder onPostCreateViewHolder(final RecyclerView.ViewHolder viewHolder) {
                mClickListenerHelper.listen(viewHolder, ((CheckBoxSampleItem.ViewHolder) viewHolder).checkBox, new ClickListenerHelper.OnClickListener<CheckBoxSampleItem>() {
                    @Override
                    public void onClick(View v, int position, CheckBoxSampleItem item) {
                        fastItemAdapter.toggleSelection(position);
                    }
                });
                return viewHolder;
            }
        });

        //get our recyclerView and do basic setup
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(fastItemAdapter);

        //fill with some sample data
        int x = 0;
        List<CheckBoxSampleItem> items = new ArrayList<>();
        for (String s : ALPHABET) {
            int count = new Random().nextInt(20);
            for (int i = 1; i <= count; i++) {
                items.add(new CheckBoxSampleItem().withName(s + " Test " + x).withIdentifier(100 + x));
                x++;
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
                Toast.makeText(getApplicationContext(), "selections = " + fastItemAdapter.getSelections(), Toast.LENGTH_LONG).show();
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
