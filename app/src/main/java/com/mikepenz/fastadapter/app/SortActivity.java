package com.mikepenz.fastadapter.app;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.IntRange;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.app.items.SimpleItem;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.items.SortedItemList;
import com.mikepenz.fastadapter.items.SortedItemList.Callback;
import com.mikepenz.fastadapter.listeners.OnClickListener;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.material_design_iconic_typeface_library.MaterialDesignIconic;
import com.mikepenz.materialize.MaterializeBuilder;

import java.io.Serializable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * An Activity which showcases the sort feature of the library.
 *
 * @author Ruben Gees
 */
public class SortActivity extends AppCompatActivity {

    private static final int SORT_ASCENDING = 0;
    private static final int SORT_DESCENDING = 1;
    private static final int SORT_NONE = -1;

    private static final String[] ALPHABET = new String[]{"A", "B", "C", "D", "E", "F", "G", "H",
            "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y",
            "Z"};

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.rv)
    RecyclerView recyclerView;

    //save our FastAdapter
    private FastItemAdapter<SimpleItem> fastItemAdapter;

    @SortingStrategy
    private int sortingStrategy;

    private Comparator<SimpleItem> comparator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        findViewById(android.R.id.content).setSystemUiVisibility(findViewById(android.R.id.content)
                .getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sort);

        ButterKnife.bind(this);

        // Handle Toolbar
        setSupportActionBar(toolbar);

        //style our ui
        new MaterializeBuilder().withActivity(this).build();

        if (savedInstanceState != null) {
            //Retrieve the previous sorting strategy from the instance state
            sortingStrategy = toSortingStrategy(savedInstanceState.getInt("sorting_strategy"));
        } else {
            //Set the default so
            sortingStrategy = SORT_NONE;
        }

        setComparator();

        List<SimpleItem> initialList = generateUnsortedList();
        //create our FastAdapter which will manage everything
        SortedItemList<SimpleItem> itemList = new SortedItemList<SimpleItem>(sortedCallback, SimpleItem.class, initialList.size());
        fastItemAdapter = new FastItemAdapter<>(itemList);
        fastItemAdapter.withSelectable(true);
        fastItemAdapter.add(generateUnsortedList());

        //configure our fastAdapter
        fastItemAdapter.withOnClickListener(new OnClickListener<SimpleItem>() {
            @Override
            public boolean onClick(View v, IAdapter<SimpleItem> adapter,
                                   SimpleItem item, int position) {
                Toast.makeText(v.getContext(), (item).name.getText(v.getContext()),
                        Toast.LENGTH_LONG).show();
                return false;
            }
        });

        //get our recyclerView and do basic setup
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(fastItemAdapter);

        //restore selections (this has to be done after the items were added
        fastItemAdapter.withSavedInstanceState(savedInstanceState);

        //set the back arrow in the toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(false);
    }

    @SortingStrategy
    int toSortingStrategy(int val) {
        return val;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //add the values which need to be saved from the adapter to the bundle
        outState = fastItemAdapter.saveInstanceState(outState);
        //We need to persist our sorting strategy between orientation changes
        outState.putInt("sorting_strategy", sortingStrategy);
        super.onSaveInstanceState(outState);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.sort, menu);
        menu.findItem(R.id.item_sort_random).setIcon(new IconicsDrawable(this, MaterialDesignIconic.Icon.gmi_wrap_text).color(Color.BLACK).actionBar());
        menu.findItem(R.id.item_sort_asc).setIcon(new IconicsDrawable(this, MaterialDesignIconic.Icon.gmi_sort_asc).color(Color.BLACK).actionBar());
        menu.findItem(R.id.item_sort_desc).setIcon(new IconicsDrawable(this, MaterialDesignIconic.Icon.gmi_sort_desc).color(Color.BLACK).actionBar());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        //handle the menu item click
        switch (item.getItemId()) {
            case R.id.item_sort_random:
                //Set the new sorting strategy
                sortingStrategy = SORT_NONE;
                setComparator();
                List<SimpleItem> items = fastItemAdapter.getAdapterItems();
                Collections.shuffle(items);
                fastItemAdapter.setNewList(items);
                return true;
            case R.id.item_sort_asc:
                items = fastItemAdapter.getAdapterItems();
                fastItemAdapter.clear();
                //Set the new sorting strategy
                sortingStrategy = SORT_ASCENDING;
                setComparator();
                fastItemAdapter.add(items);
                return true;
            case R.id.item_sort_desc:
                items = fastItemAdapter.getAdapterItems();
                fastItemAdapter.clear();
                //Set the new sorting strategy
                sortingStrategy = SORT_DESCENDING;
                setComparator();
                fastItemAdapter.add(items);
                return true;
            case android.R.id.home:
                Toast.makeText(getApplicationContext(), "selections = " +
                        fastItemAdapter.getSelections(), Toast.LENGTH_LONG).show();
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Sets the appropriate Comparator for the current sorting strategy or null if no strategy is
     * set. (SORT_NONE)
     */
    private void setComparator() {
        switch (sortingStrategy) {
            case SORT_ASCENDING:
                comparator = new AlphabetComparatorAscending();
                break;
            case SORT_DESCENDING:
                comparator = new AlphabetComparatorDescending();
                break;
            case SORT_NONE:
                comparator = null;
                break;
            default:
                throw new RuntimeException("This sortingStrategy is not supported.");
        }
    }

    /**
     * Generates a simple list consisting of the letters of the alphabet, unordered on purpose.
     *
     * @return The new list.
     */
    private List<SimpleItem> generateUnsortedList() {
        ArrayList<SimpleItem> result = new ArrayList<>(26);

        for (int i = 0; i < 26; i++) {
            result.add(makeItem(i));
        }

        Collections.shuffle(result);

        return result;
    }

    /**
     * Build a simple item with one letter of the alphabet.
     *
     * @param position The position of the letter in the alphabet.
     * @return The new item.
     */
    private SimpleItem makeItem(@IntRange(from = 0, to = 25) int position) {
        SimpleItem result = new SimpleItem();

        result.withName(ALPHABET[position]);

        position++;

        String description = "The " + (position);

        if (position == 1 || position == 21) {
            description += "st";
        } else if (position == 2 || position == 22) {
            description += "nd";
        } else if (position == 3 || position == 23) {
            description += "rd";
        } else {
            description += "th";
        }

        return result.withDescription(description + " letter in the alphabet");
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({SORT_NONE, SORT_ASCENDING, SORT_DESCENDING})
    public @interface SortingStrategy {
    }

    /**
     * A simple Comparator to sort the items ascending.
     */
    private class AlphabetComparatorAscending implements Comparator<SimpleItem>, Serializable {
        @Override
        public int compare(SimpleItem lhs, SimpleItem rhs) {
            return lhs.name.getText().toString().compareTo(rhs.name.getText().toString());
        }
    }

    /**
     * A simple Comparator to sort the items descending.
     */
    private class AlphabetComparatorDescending implements Comparator<SimpleItem>, Serializable {
        @Override
        public int compare(SimpleItem lhs, SimpleItem rhs) {
            return rhs.name.getText().toString().compareTo(lhs.name.getText().toString());
        }
    }

    private final Callback<SimpleItem> sortedCallback = new Callback<SimpleItem>() {
        @Override
        public int compare(SimpleItem lhs, SimpleItem rhs) {
            if(comparator == null) {
                return 0;
            }
            else {
                return comparator.compare(lhs, rhs);
            }
        }

        @Override
        public boolean isChanged(SimpleItem oldItem, SimpleItem newItem) {
            return false;
        }

        @Override
        public boolean areEqual(SimpleItem o1, SimpleItem o2) {
            return o1.name.equals(o2.name);
        }
    };
}
