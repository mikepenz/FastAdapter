package com.mikepenz.fastadapter.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.mikepenz.aboutlibraries.Libs;
import com.mikepenz.aboutlibraries.LibsBuilder;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.app.items.IconItem;
import com.mikepenz.fastadapter.app.items.SampleItem;
import com.mikepenz.iconics.Iconics;
import com.mikepenz.iconics.typeface.ITypeface;
import com.mikepenz.itemanimators.SlideDownAlphaAnimator;
import com.mikepenz.material_design_iconic_typeface_library.MaterialDesignIconic;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class IconGridActivity extends AppCompatActivity {
    //save our header or result
    private Drawer result = null;
    //save our FastAdapter
    private FastAdapter<SampleItem> fastAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);

        // Handle Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Create the drawer
        result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withHasStableIds(true)
                .withSavedInstance(savedInstanceState)
                .withShowDrawerOnFirstLaunch(true)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.sample_simple).withSelectable(false).withIdentifier(6).withIcon(MaterialDesignIconic.Icon.gmi_format_align_justify),
                        new PrimaryDrawerItem().withName(R.string.sample_image_list).withSelectable(false).withIdentifier(5).withIcon(MaterialDesignIconic.Icon.gmi_wallpaper),
                        new PrimaryDrawerItem().withName(R.string.sample_multi_select).withSelectable(false).withIdentifier(1).withIcon(MaterialDesignIconic.Icon.gmi_select_all),
                        new PrimaryDrawerItem().withName(R.string.sample_collapsible).withSelectable(false).withIdentifier(2).withIcon(MaterialDesignIconic.Icon.gmi_check_all),
                        new PrimaryDrawerItem().withName(R.string.sample_sticky_header).withSelectable(false).withIdentifier(3).withIcon(MaterialDesignIconic.Icon.gmi_format_align_left),
                        new PrimaryDrawerItem().withName(R.string.sample_advanced).withSelectable(false).withDescription(R.string.sample_advanced_descr).withIdentifier(4).withIcon(MaterialDesignIconic.Icon.gmi_coffee),
                        new DividerDrawerItem(),
                        new PrimaryDrawerItem().withName(R.string.open_source).withSelectable(false).withIdentifier(100).withIcon(MaterialDesignIconic.Icon.gmi_github)
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        if (drawerItem != null) {
                            Intent intent = null;
                            if (drawerItem.getIdentifier() == 1) {
                                intent = new Intent(IconGridActivity.this, MultiselectSampleActivity.class);
                            } else if (drawerItem.getIdentifier() == 2) {
                                intent = new Intent(IconGridActivity.this, CollapsibleSampleActivity.class);
                            } else if (drawerItem.getIdentifier() == 3) {
                                intent = new Intent(IconGridActivity.this, StickyHeaderSampleActivity.class);
                            } else if (drawerItem.getIdentifier() == 4) {
                                intent = new Intent(IconGridActivity.this, AdvancedSampleActivity.class);
                            } else if (drawerItem.getIdentifier() == 5) {
                                intent = new Intent(IconGridActivity.this, ImageListActivity.class);
                            } else if (drawerItem.getIdentifier() == 6) {
                                intent = new Intent(IconGridActivity.this, SimpleActivity.class);
                            } else if (drawerItem.getIdentifier() == 100) {
                                intent = new LibsBuilder()
                                        .withFields(R.string.class.getFields())
                                        .withActivityTitle(getString(R.string.open_source))
                                        .withActivityStyle(Libs.ActivityStyle.LIGHT_DARK_TOOLBAR)
                                        .intent(IconGridActivity.this);
                            }
                            if (intent != null) {
                                IconGridActivity.this.startActivity(intent);
                            }
                        }
                        return false;
                    }
                })
                .withSelectedItemByPosition(-1)
                .build();

        //create our FastAdapter which will manage everything
        fastAdapter = new FastAdapter<>();
        fastAdapter.withOnClickListener(new FastAdapter.OnClickListener() {
            @Override
            public boolean onClick(View v, IAdapter adapter, IItem item, int position) {
                if (item instanceof SampleItem) {
                    if (((SampleItem) item).getSubItems() != null) {
                        fastAdapter.toggleExpandable(position);
                        return true;
                    }
                }
                return false;
            }
        });

        //create our ItemAdapter which will host our items
        final ItemAdapter<SampleItem> itemAdapter = new ItemAdapter<>();

        //get our recyclerView and do basic setup
        RecyclerView rv = (RecyclerView) findViewById(R.id.rv);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (fastAdapter.getItemViewType(position)) {
                    case R.id.fastadapter_sample_item_id:
                        return 3;
                    case R.id.fastadapter_icon_item_id:
                        return 1;
                    default:
                        return 1;
                }
            }
        });
        rv.setLayoutManager(gridLayoutManager);
        rv.setItemAnimator(new SlideDownAlphaAnimator());
        rv.setAdapter(itemAdapter.wrap(fastAdapter));

        //order fonts by their name
        List<ITypeface> mFonts = new ArrayList<>(Iconics.getRegisteredFonts(this));
        Collections.sort(mFonts, new Comparator<ITypeface>() {
            @Override
            public int compare(final ITypeface object1, final ITypeface object2) {
                return object1.getFontName().compareTo(object2.getFontName());
            }
        });

        //add all icons of all registered Fonts to the list
        ArrayList<SampleItem> items = new ArrayList<>(Iconics.getRegisteredFonts(this).size());
        int count = 0;
        for (ITypeface font : mFonts) {
            SampleItem sampleItem = new SampleItem().withName(font.getFontName());

            ArrayList<IItem> icons = new ArrayList<>();
            for (String icon : font.getIcons()) {
                icons.add(new IconItem().withIcon(font.getIcon(icon)).withSelectable(false));
            }
            sampleItem.withSubItems(icons);

            items.add(sampleItem);
            count++;
        }

        //fill with some sample data
        itemAdapter.add(items);

        //restore selections (this has to be done after the items were added
        fastAdapter.withSavedInstanceState(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //add the values which need to be saved from the drawer to the bundle
        outState = result.saveInstanceState(outState);
        //add the values which need to be saved from the adapter to the bundel
        outState = fastAdapter.saveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        //handle the back press :D close the drawer first and if the drawer is closed close the activity
        if (result != null && result.isDrawerOpen()) {
            result.closeDrawer();
        } else {
            super.onBackPressed();
        }
    }
}
