package com.mikepenz.fastadapter.app;

import android.os.Bundle;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.mikepenz.aboutlibraries.util.UIUtils;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.app.items.ExpandableItem;
import com.mikepenz.fastadapter.app.items.SampleItem;
import com.mikepenz.fastadapter_extensions.ActionModeHelper;
import com.mikepenz.fastadapter_extensions.RangeSelectorHelper;
import com.mikepenz.fastadapter_extensions.utilities.SubItemUtils;
import com.mikepenz.iconics.context.IconicsLayoutInflater;
import com.mikepenz.itemanimators.SlideDownAlphaAnimator;
import com.mikepenz.materialize.MaterializeBuilder;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ExpandableMultiselectDeleteSampleActivity extends AppCompatActivity {
    //save our FastAdapter
    private FastItemAdapter<IItem> fastItemAdapter;
    private ActionModeHelper mActionModeHelper;
    private RangeSelectorHelper mRangeSelectorHelper;

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
        fastItemAdapter = new FastItemAdapter<>();

        fastItemAdapter
//                .withPositionBasedStateManagement(false) // this breaks the example!
                .withSelectable(true)
                .withMultiSelect(true)
                .withSelectOnLongClick(true)
                .withOnPreClickListener(new FastAdapter.OnClickListener<IItem>() {
                    @Override
                    public boolean onClick(View v, IAdapter<IItem> adapter, IItem item, int position) {
                        //we handle the default onClick behavior for the actionMode. This will return null if it didn't do anything and you can handle a normal onClick
                        Boolean res = mActionModeHelper.onClick(ExpandableMultiselectDeleteSampleActivity.this, item);
                        // in this example, we want to consume a click, if the ActionModeHelper will remove the ActionMode
                        // so that the click listener is not fired
                        if (res != null && !res)
                            return true;
                        return res != null ? res : false;
                    }
                })
                .withOnClickListener(new FastAdapter.OnClickListener<IItem>() {
                    @Override
                    public boolean onClick(View v, IAdapter<IItem> adapter, IItem item, int position) {
                        // check if the actionMode consumes the click. This returns true, if it does, false if not
                        if (!mActionModeHelper.isActive())
                            Toast.makeText(ExpandableMultiselectDeleteSampleActivity.this, ((SampleItem)item).name + " clicked!", Toast.LENGTH_SHORT).show();
//                        else
//                            mFastAdapter.notifyItemChanged(position); // im Bsp. ist das nicht nötig, k.A. warum ich das machen muss!
                        mRangeSelectorHelper.onClick();
                        return false;
                    }
                })
                .withOnPreLongClickListener(new FastAdapter.OnLongClickListener<IItem>() {
                    @Override
                    public boolean onLongClick(View v, IAdapter<IItem> adapter, IItem item, int position) {
                        ActionMode actionMode = mActionModeHelper.onLongClick((AppCompatActivity) ExpandableMultiselectDeleteSampleActivity.this, position);
                        mRangeSelectorHelper.onLongClick(position);
                        if (actionMode != null) {
                            //we want color our CAB
                            ExpandableMultiselectDeleteSampleActivity.this.findViewById(R.id.action_mode_bar).setBackgroundColor(UIUtils.getThemeColorFromAttrOrRes(ExpandableMultiselectDeleteSampleActivity.this, R.attr.colorPrimary, R.color.material_drawer_primary));
                        }

                        //if we have no actionMode we do not consume the event
                        return actionMode != null;
                    }
                });

        // provide a custom title provider that even shows the count of sub items only!
        // this may not be what you want though, but shows the usage of the SubItemUtils function
        mActionModeHelper = new ActionModeHelper(fastItemAdapter, R.menu.cab, new ActionBarCallBack())
                .withTitleProvider(new ActionModeHelper.ActionModeTitleProvider() {
                    @Override
                    public String getTitle(int selected) {
                        return selected + "/" + SubItemUtils.countItems(fastItemAdapter, false);
                    }
                });

        // this will take care of selecting range of items via long press on the first and afterwards on the last item
        mRangeSelectorHelper = new RangeSelectorHelper(fastItemAdapter)
                .withSavedInstanceState(savedInstanceState)
                .withActionModeHelper(mActionModeHelper);

        //get our recyclerView and do basic setup
        RecyclerView rv = (RecyclerView) findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setItemAnimator(new SlideDownAlphaAnimator());
        rv.setAdapter(fastItemAdapter);

        //fill with some sample data
        List<IItem> items = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            if (i % 2 == 0) {
                ExpandableItem expandableItem = new ExpandableItem()
                        .withName("Test " + (i + 1))
                        .withIdentifier(100 + i)
                        .withIsExpanded(true) // => breaks example in combination with id based state management
                        ;

                //add subitems so we can showcase the collapsible functionality
                List<IItem> subItems = new LinkedList<>();
                for (int ii = 1; ii <= 5; ii++) {
                    subItems.add(new SampleItem().withName("-- Test " + (i + 1) + "." + ii).withIdentifier(1000 + i * 100 + ii));
                }
                expandableItem.withSubItems(subItems);

                items.add(expandableItem);
            } else {
                items.add(new SampleItem().withName("Test " + (i + 1)).withIdentifier(100 + i));
            }
        }
        fastItemAdapter.add(items);

        //restore selections (this has to be done after the items were added
        fastItemAdapter.withSavedInstanceState(savedInstanceState);

        //set the back arrow in the toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(false);

        // restore action mode
        if (savedInstanceState != null)
            mActionModeHelper.checkActionMode(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //add the values which need to be saved from the adapter to the bundel
        outState = fastItemAdapter.saveInstanceState(outState);
        outState = mRangeSelectorHelper.saveInstanceState(outState);
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

    class ActionBarCallBack implements ActionMode.Callback {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu)
        {
            return true;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

            // delete the selected items with the SubItemUtils to correctly handle sub items
            // this will even delete empty headers if you want to
            List<IItem> deleted = SubItemUtils.deleteSelected(fastItemAdapter, true, true);
            //as we no longer have a selection so the actionMode can be finished
            mode.finish();
            //we consume the event
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            // reset the range selector
            mRangeSelectorHelper.reset();
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }
    }
}
