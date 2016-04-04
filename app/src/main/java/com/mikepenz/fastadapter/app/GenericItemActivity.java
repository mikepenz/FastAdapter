package com.mikepenz.fastadapter.app;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.adapters.GenericItemAdapter;
import com.mikepenz.fastadapter.app.adapters.FastScrollIndicatorAdapter;
import com.mikepenz.fastadapter.app.generic.GenericIconItem;
import com.mikepenz.fastadapter.app.generic.IconModel;
import com.mikepenz.iconics.Iconics;
import com.mikepenz.iconics.typeface.ITypeface;
import com.mikepenz.itemanimators.SlideDownAlphaAnimator;
import com.mikepenz.materialize.MaterializeBuilder;
import com.turingtechnologies.materialscrollbar.CustomIndicator;
import com.turingtechnologies.materialscrollbar.DragScrollBar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class GenericItemActivity extends AppCompatActivity {
    //save our FastAdapter
    private FastAdapter fastAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        findViewById(android.R.id.content).setSystemUiVisibility(findViewById(android.R.id.content).getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);

        // Handle Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.sample_generic_item);

        //style our ui
        new MaterializeBuilder().withActivity(this).build();


        //create our FastAdapter which will manage everything
        fastAdapter = new FastAdapter();
        fastAdapter.withSelectable(true);

        //get our recyclerView and do basic setup
        RecyclerView rv = (RecyclerView) findViewById(R.id.rv);

        //init our gridLayoutManager and configure RV
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);

        GenericItemAdapter<IconModel, GenericIconItem> itemAdapter = new GenericItemAdapter<>(GenericIconItem.class, IconModel.class);

        final FastScrollIndicatorAdapter<GenericIconItem> fastScrollIndicatorAdapter = new FastScrollIndicatorAdapter<>();
        rv.setAdapter(fastScrollIndicatorAdapter.wrap(itemAdapter.wrap(fastAdapter)));

        DragScrollBar materialScrollBar = new DragScrollBar(this, rv, true);
        materialScrollBar.setHandleColour(ContextCompat.getColor(this, R.color.colorAccent));
        materialScrollBar.setHandleOffColour(ContextCompat.getColor(this, R.color.colorAccent));
        materialScrollBar.addIndicator(new CustomIndicator(this), true);

        rv.setLayoutManager(gridLayoutManager);
        rv.setItemAnimator(new SlideDownAlphaAnimator());

        //order fonts by their name
        List<ITypeface> mFonts = new ArrayList<>(Iconics.getRegisteredFonts(this));
        Collections.sort(mFonts, new Comparator<ITypeface>() {
            @Override
            public int compare(final ITypeface object1, final ITypeface object2) {
                return object1.getFontName().compareTo(object2.getFontName());
            }
        });

        //add all icons of all registered Fonts to the list
        ArrayList<IconModel> models = new ArrayList<>();
        for (ITypeface font : mFonts) {
            for (String icon : font.getIcons()) {
                models.add(new IconModel(font.getIcon(icon)));
            }
        }

        //fill with some sample data
        itemAdapter.addModel(models);

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
