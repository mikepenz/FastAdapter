package com.mikepenz.fastadapter.app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.app.items.RealmSampleUserItem;
import com.mikepenz.materialize.MaterializeBuilder;

import java.util.LinkedList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class RealmActivity extends AppCompatActivity {
    //save our FastAdapter
    private FastItemAdapter<RealmSampleUserItem> mFastItemAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        findViewById(android.R.id.content).setSystemUiVisibility(findViewById(android.R.id.content).getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Handle Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.sample_realm_list);

        //style our ui
        new MaterializeBuilder().withActivity(this).build();

        //create our FastAdapter which will manage everything
        mFastItemAdapter = new FastItemAdapter<>();

        //configure our fastAdapter
        mFastItemAdapter.withOnClickListener(new FastAdapter.OnClickListener<RealmSampleUserItem>() {
            @Override
            public boolean onClick(View v, IAdapter<RealmSampleUserItem> adapter, RealmSampleUserItem item, int position) {
                Toast.makeText(v.getContext(), item.getName(), Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        //get our recyclerView and do basic setup
        RecyclerView rv = (RecyclerView) findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setItemAnimator(new DefaultItemAnimator());
        rv.setAdapter(mFastItemAdapter);

        //fill with some sample data
        mFastItemAdapter.add(getAndCreateData());

        //restore selections (this has to be done after the items were added
        mFastItemAdapter.withSavedInstanceState(savedInstanceState);

        //set the back arrow in the toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(false);
    }

    private List<RealmSampleUserItem> getAndCreateData() {
        // Obtain a Realm instance
        Realm realm = Realm.getDefaultInstance();

        // Build the query looking at all users:
        RealmQuery<RealmSampleUserItem> query = realm.where(RealmSampleUserItem.class);

        // Execute the query:
        RealmResults<RealmSampleUserItem> result = query.findAll();

        if (result.size() > 0) {
            return result.subList(0, result.size() - 1);
        }

        realm.beginTransaction();

        List<RealmSampleUserItem> users = new LinkedList<>();
        for (int i = 1; i <= 1500; i++) {
            RealmSampleUserItem user = realm.createObject(RealmSampleUserItem.class);
            user.withName("Sample Realm Element " + i).withIdentifier(i);
            users.add(user);
        }
        //... add or update objects here ...
        realm.commitTransaction();

        return users;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //add the values which need to be saved from the adapter to the bundel
        outState = mFastItemAdapter.saveInstanceState(outState);
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
