package com.mikepenz.fastadapter.app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IInterceptor;
import com.mikepenz.fastadapter.adapters.ModelAdapter;
import com.mikepenz.fastadapter.app.model.IconModel;
import com.mikepenz.fastadapter.app.model.ModelIconItem;
import com.mikepenz.fastadapter.app.model.RightIconModel;
import com.mikepenz.fastadapter.app.model.RightModelIconItem;
import com.mikepenz.iconics.Iconics;
import com.mikepenz.iconics.typeface.ITypeface;
import com.mikepenz.itemanimators.SlideDownAlphaAnimator;
import com.mikepenz.materialize.MaterializeBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MultiTypeModelItemActivity extends AppCompatActivity {
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
        getSupportActionBar().setTitle(R.string.sample_multi_model_item);

        //style our ui
        new MaterializeBuilder().withActivity(this).build();

        //if you need multiple items for different models you can also do this be defining a Function which get's the model object and returns the item (extends IItem)
        ModelAdapter<IconModel, ModelIconItem> itemAdapter = new ModelAdapter<>(new IInterceptor<IconModel, ModelIconItem>() {
            @Override
            public ModelIconItem intercept(IconModel o) {
                if (o instanceof RightIconModel) {
                    return new RightModelIconItem(o);
                } else {
                    return new ModelIconItem(o);
                }
            }
        });

        //create our FastAdapter which will manage everything
        fastAdapter = FastAdapter.with(Arrays.asList(itemAdapter));
        fastAdapter.withSelectable(true);

        //get our recyclerView and do basic setup
        RecyclerView rv = (RecyclerView) findViewById(R.id.rv);

        //init our gridLayoutManager and configure RV
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);

        rv.setLayoutManager(gridLayoutManager);
        rv.setItemAnimator(new SlideDownAlphaAnimator());
        rv.setAdapter(fastAdapter);

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
        int i = 0;
        for (ITypeface font : mFonts) {
            for (String icon : font.getIcons()) {
                if (i % 3 == 0) {
                    models.add(new IconModel(font.getIcon(icon)));
                } else {
                    models.add(new RightIconModel(font.getIcon(icon)));
                }
                i++;
            }
        }

        //fill with some sample data
        itemAdapter.add(models);

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
}
