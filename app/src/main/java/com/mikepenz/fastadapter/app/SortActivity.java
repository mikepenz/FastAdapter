package com.mikepenz.fastadapter.app;

import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.IntRange;
import android.support.annotation.Nullable;
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
import com.mikepenz.fastadapter.app.items.SampleItem;
import com.mikepenz.materialize.MaterializeBuilder;

import java.io.Serializable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * An Activity which showcases the sort feature of the library.
 *
 * @author Ruben Gees
 */
public class SortActivity extends AppCompatActivity {

    public static final int SORT_ASCENDING = 0;
    public static final int SORT_DESCENDING = 1;
    private static final int SORT_NONE = -1;
    private static final String[] ALPHABET = new String[]{"A", "B", "C", "D", "E", "F", "G", "H",
            "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y",
            "Z"};

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.rv)
    RecyclerView recyclerView;

    //save our FastAdapter
    private FastItemAdapter<SampleItem> fastItemAdapter;

    @SortingStrategy
    private int sortingStrategy;

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

        //create our FastAdapter which will manage everything
        fastItemAdapter = new FastItemAdapter<>();

        //configure our fastAdapter
        fastItemAdapter.withOnClickListener(new FastAdapter.OnClickListener<SampleItem>() {
            @Override
            public boolean onClick(View v, IAdapter<SampleItem> adapter,
                                   SampleItem item, int position) {
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

        if (savedInstanceState != null) {
            //Retrieve the previous sorting strategy from the instance state
            sortingStrategy = savedInstanceState.getInt("sorting_strategy");
        } else {
            //Set the default so
            sortingStrategy = SORT_NONE;
        }

        fastItemAdapter.getItemAdapter().withComparator(getComparator());
        fastItemAdapter.setNewList(generateUnsortedList());

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
        //We need to persist our sorting strategy between orientation changes
        outState.putInt("sorting_strategy", sortingStrategy);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //handle the click on the back arrow click
        switch (item.getItemId()) {
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
     * Sorts the list ascending
     */
    @OnClick(R.id.button_ascending)
    public void sortAscending() {
        //Set the new sorting strategy
        sortingStrategy = SORT_ASCENDING;
        //Set the new comparator to the list
        fastItemAdapter.getItemAdapter().withComparator(getComparator());
        //Reset the list to trigger the sorting
        fastItemAdapter.setNewList(generateUnsortedList());
    }

    /**
     * Sorts the list descending
     */
    @OnClick(R.id.button_descending)
    public void sortDescending() {
        //Set the new sorting strategy
        sortingStrategy = SORT_DESCENDING;
        //Set the new comparator to the list
        fastItemAdapter.getItemAdapter().withComparator(getComparator());
        //Reset the list to trigger the sorting
        fastItemAdapter.setNewList(generateUnsortedList());
    }

    /**
     * Returns the appropriate Comparator for the current sorting strategy or null if no strategy is
     * set. (SORT_NONE)
     *
     * @return The comparator or null.
     */
    @Nullable
    private Comparator<SampleItem> getComparator() {
        switch (sortingStrategy) {
            case SORT_ASCENDING:
                return new AlphabetComparatorAscending();
            case SORT_DESCENDING:
                return new AlphabetComparatorDescending();
            case SORT_NONE:
                return null;
        }

        throw new RuntimeException("This sortingStrategy is not supported.");
    }

    /**
     * Generates a simple list consisting of the letters of the alphabet, unordered on purpose.
     *
     * @return The new list.
     */
    private List<SampleItem> generateUnsortedList() {
        ArrayList<SampleItem> result = new ArrayList<>(26);

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
    private SampleItem makeItem(@IntRange(from = 0, to = 25) int position) {
        SampleItem result = new SampleItem();

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
    private class AlphabetComparatorAscending implements Comparator<SampleItem>, Serializable {
        @Override
        public int compare(SampleItem lhs, SampleItem rhs) {
            return lhs.name.getText().compareTo(rhs.name.getText());
        }
    }

    /**
     * A simple Comparator to sort the items descending.
     */
    private class AlphabetComparatorDescending implements Comparator<SampleItem>, Serializable {
        @Override
        public int compare(SampleItem lhs, SampleItem rhs) {
            return rhs.name.getText().compareTo(lhs.name.getText());
        }
    }
}
