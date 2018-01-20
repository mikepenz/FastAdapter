package com.mikepenz.fastadapter.app;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.mikepenz.aboutlibraries.Libs;
import com.mikepenz.aboutlibraries.LibsBuilder;
import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.app.dummy.ImageDummyData;
import com.mikepenz.fastadapter.app.items.SimpleImageItem;
import com.mikepenz.fastadapter.select.SelectExtension;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.itemanimators.SlideDownAlphaAnimator;
import com.mikepenz.material_design_iconic_typeface_library.MaterialDesignIconic;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import java.util.Arrays;
import java.util.List;

public class SampleActivity extends AppCompatActivity {

    //our rv
    RecyclerView mRecyclerView;
    //save our header or result
    private Drawer mResult = null;
    //save our FastAdapter
    private FastAdapter<SimpleImageItem> mFastAdapter;
    //save our FastAdapter
    private ItemAdapter<SimpleImageItem> mItemAdapter;
    //our `SelectExtension`
    private SelectExtension<SimpleImageItem> selectExtension;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        findViewById(android.R.id.content).setSystemUiVisibility(findViewById(android.R.id.content).getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        //create the activity
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // Handle Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        //Create the drawer
        mResult = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withHasStableIds(true)
                .withSavedInstance(savedInstanceState)
                .withShowDrawerOnFirstLaunch(true)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.sample_icon_grid).withDescription(R.string.sample_icon_grid_descr).withSelectable(false).withIdentifier(8).withIcon(MaterialDesignIconic.Icon.gmi_grid),
                        new PrimaryDrawerItem().withName(R.string.sample_simple_item_list).withDescription(R.string.sample_simple_item_list_descr).withSelectable(false).withIdentifier(6).withIcon(MaterialDesignIconic.Icon.gmi_format_align_justify),
                        new PrimaryDrawerItem().withName(R.string.sample_image_list).withDescription(R.string.sample_image_list_descr).withSelectable(false).withIdentifier(5).withIcon(MaterialDesignIconic.Icon.gmi_wallpaper),
                        new PrimaryDrawerItem().withName(R.string.sample_multi_select).withDescription(R.string.sample_multi_select_descr).withSelectable(false).withIdentifier(1).withIcon(MaterialDesignIconic.Icon.gmi_select_all),
                        new PrimaryDrawerItem().withName(R.string.sample_collapsible).withDescription(R.string.sample_collapsible_descr).withSelectable(false).withIdentifier(2).withIcon(MaterialDesignIconic.Icon.gmi_check_all),
                        new PrimaryDrawerItem().withName(R.string.sample_sticky_header).withDescription(R.string.sample_sticky_header_descr).withSelectable(false).withIdentifier(3).withIcon(MaterialDesignIconic.Icon.gmi_format_align_left),
                        new PrimaryDrawerItem().withName(R.string.sample_advanced).withDescription(R.string.sample_advanced_descr).withSelectable(false).withIdentifier(4).withIcon(MaterialDesignIconic.Icon.gmi_coffee),
                        new PrimaryDrawerItem().withName(R.string.sample_model_item).withDescription(R.string.sample_model_item_descr).withSelectable(false).withIdentifier(7).withIcon(MaterialDesignIconic.Icon.gmi_font),
                        new PrimaryDrawerItem().withName(R.string.sample_multi_model_item).withDescription(R.string.sample_multi_model_item_descr).withSelectable(false).withIdentifier(9).withIcon(MaterialDesignIconic.Icon.gmi_format_list_numbered),
                        new PrimaryDrawerItem().withName(R.string.sample_checkbox_item).withDescription(R.string.sample_checkbox_item_descr).withSelectable(false).withIdentifier(10).withIcon(CommunityMaterial.Icon.cmd_checkbox_marked),
                        new PrimaryDrawerItem().withName(R.string.sample_radiobutton_item).withDescription(R.string.sample_radiobutton_item_descr).withSelectable(false).withIdentifier(11).withIcon(CommunityMaterial.Icon.cmd_radiobox_marked),
                        new PrimaryDrawerItem().withName(R.string.sample_swipe_list).withDescription(R.string.sample_swipe_list_descr).withSelectable(false).withIdentifier(12).withIcon(MaterialDesignIconic.Icon.gmi_format_align_left),
                        new PrimaryDrawerItem().withName(R.string.sample_endless_scroll_list).withDescription(R.string.sample_endless_scroll_list_descr).withSelectable(false).withIdentifier(13).withIcon(MaterialDesignIconic.Icon.gmi_long_arrow_down),
                        new PrimaryDrawerItem().withName(R.string.sample_sort).withDescription(R.string.sample_sort_descr).withSelectable(false).withIdentifier(14).withIcon(MaterialDesignIconic.Icon.gmi_sort_by_alpha),
                        new PrimaryDrawerItem().withName(R.string.sample_mopub).withDescription(R.string.sample_mopub_descr).withSelectable(false).withIdentifier(15).withIcon(MaterialDesignIconic.Icon.gmi_accounts_list),
                        new PrimaryDrawerItem().withName(R.string.sample_realm_list).withDescription(R.string.sample_realm_list_descr).withSelectable(false).withIdentifier(16).withIcon(MaterialDesignIconic.Icon.gmi_format_color_text),
                        new PrimaryDrawerItem().withName(R.string.sample_collapsible_multi_select_delete).withDescription(R.string.sample_collapsible_multi_select_delete_descr).withSelectable(false).withIdentifier(17).withIcon(MaterialDesignIconic.Icon.gmi_check_all),
                        new PrimaryDrawerItem().withName(R.string.sample_sticky_header_mopub).withDescription(R.string.sample_sticky_header_mopub_descr).withSelectable(false).withIdentifier(18).withIcon(MaterialDesignIconic.Icon.gmi_accounts_list),
                        new PrimaryDrawerItem().withName(R.string.sample_diff_util).withDescription(R.string.sample_diff_util_descr).withSelectable(false).withIdentifier(19).withIcon(MaterialDesignIconic.Icon.gmi_refresh),
                        new DividerDrawerItem(),
                        new PrimaryDrawerItem().withName(R.string.open_source).withSelectable(false).withIdentifier(100).withIcon(MaterialDesignIconic.Icon.gmi_github)
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        if (drawerItem != null) {
                            Intent intent = null;
                            if (drawerItem.getIdentifier() == 1) {
                                intent = new Intent(SampleActivity.this, MultiselectSampleActivity.class);
                            } else if (drawerItem.getIdentifier() == 2) {
                                intent = new Intent(SampleActivity.this, ExpandableSampleActivity.class);
                            } else if (drawerItem.getIdentifier() == 3) {
                                intent = new Intent(SampleActivity.this, StickyHeaderSampleActivity.class);
                            } else if (drawerItem.getIdentifier() == 4) {
                                intent = new Intent(SampleActivity.this, AdvancedSampleActivity.class);
                            } else if (drawerItem.getIdentifier() == 5) {
                                intent = new Intent(SampleActivity.this, ImageListActivity.class);
                            } else if (drawerItem.getIdentifier() == 6) {
                                intent = new Intent(SampleActivity.this, SimpleItemListActivity.class);
                            } else if (drawerItem.getIdentifier() == 7) {
                                intent = new Intent(SampleActivity.this, ModelItemActivity.class);
                            } else if (drawerItem.getIdentifier() == 8) {
                                intent = new Intent(SampleActivity.this, IconGridActivity.class);
                            } else if (drawerItem.getIdentifier() == 9) {
                                intent = new Intent(SampleActivity.this, MultiTypeModelItemActivity.class);
                            } else if (drawerItem.getIdentifier() == 10) {
                                intent = new Intent(SampleActivity.this, CheckBoxSampleActivity.class);
                            } else if (drawerItem.getIdentifier() == 11) {
                                intent = new Intent(SampleActivity.this, RadioButtonSampleActivity.class);
                            } else if (drawerItem.getIdentifier() == 12) {
                                intent = new Intent(SampleActivity.this, SwipeListActivity.class);
                            } else if (drawerItem.getIdentifier() == 13) {
                                intent = new Intent(SampleActivity.this, EndlessScrollListActivity.class);
                            } else if (drawerItem.getIdentifier() == 14) {
                                intent = new Intent(SampleActivity.this, SortActivity.class);
                            } else if (drawerItem.getIdentifier() == 15) {
                                intent = new Intent(SampleActivity.this, MopubAdsActivity.class);
                            } else if (drawerItem.getIdentifier() == 16) {
                                intent = new Intent(SampleActivity.this, RealmActivity.class);
                            } else if (drawerItem.getIdentifier() == 17) {
                                intent = new Intent(SampleActivity.this, ExpandableMultiselectDeleteSampleActivity.class);
                            } else if (drawerItem.getIdentifier() == 18) {
                                intent = new Intent(SampleActivity.this, StickyHeaderMopubAdsActivity.class);
                            } else if (drawerItem.getIdentifier() == 19) {
                                intent = new Intent(SampleActivity.this, DiffUtilActivity.class);
                            } else if (drawerItem.getIdentifier() == 100) {
                                intent = new LibsBuilder()
                                        .withFields(R.string.class.getFields())
                                        .withActivityTitle(getString(R.string.open_source))
                                        .withActivityStyle(Libs.ActivityStyle.LIGHT)
                                        .withAboutIconShown(true)
                                        .withVersionShown(true)
                                        .withAboutVersionShown(true)
                                        .intent(SampleActivity.this);
                            }
                            if (intent != null) {
                                SampleActivity.this.startActivity(intent);
                            }
                        }
                        return false;
                    }
                })
                .withSelectedItemByPosition(-1)
                .build();

        //create our ItemAdapter which will host our items
        mItemAdapter = new ItemAdapter<>();

        //create our FastAdapter which will manage everything
        mFastAdapter = FastAdapter.with(Arrays.asList(mItemAdapter));
        mFastAdapter.withSelectable(true);
        mFastAdapter.withMultiSelect(true);
        mFastAdapter.withSelectOnLongClick(false);
        selectExtension = mFastAdapter.getExtension(SelectExtension.class);

        //configure our fastAdapter
        //get our recyclerView and do basic setup
        mRecyclerView = findViewById(R.id.rv);
        //mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mFastAdapter);
        mRecyclerView.setItemAnimator(new SlideDownAlphaAnimator());
        mRecyclerView.getItemAnimator().setAddDuration(500);
        mRecyclerView.getItemAnimator().setRemoveDuration(500);

        //if we do this. the first added items will be animated :D
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //add some dummy data
                mItemAdapter.add(ImageDummyData.getSimpleImageItems());

                //restore selections (this has to be done after the items were added
                mFastAdapter.withSavedInstanceState(savedInstanceState);
            }
        }, 50);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        menu.findItem(R.id.item_add).setIcon(new IconicsDrawable(this, MaterialDesignIconic.Icon.gmi_plus_square).color(Color.BLACK).actionBar());
        menu.findItem(R.id.item_delete).setIcon(new IconicsDrawable(this, MaterialDesignIconic.Icon.gmi_minus_square).color(Color.BLACK).actionBar());
        menu.findItem(R.id.item_change).setIcon(new IconicsDrawable(this, MaterialDesignIconic.Icon.gmi_settings_square).color(Color.BLACK).actionBar());
        menu.findItem(R.id.item_move).setIcon(new IconicsDrawable(this, MaterialDesignIconic.Icon.gmi_format_valign_bottom).color(Color.BLACK).actionBar());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //find out the current visible position
        int firstVisiblePosition = 0;
        if (mRecyclerView.getLayoutManager() instanceof LinearLayoutManager) {
            firstVisiblePosition = ((LinearLayoutManager) mRecyclerView.getLayoutManager()).findFirstVisibleItemPosition();
        } else if (mRecyclerView.getLayoutManager() instanceof GridLayoutManager) {
            firstVisiblePosition = ((GridLayoutManager) mRecyclerView.getLayoutManager()).findFirstVisibleItemPosition();
        }

        //handle the menu item click
        switch (item.getItemId()) {
            case R.id.item_add:
                mItemAdapter.add(firstVisiblePosition + 1, ImageDummyData.getDummyItem());
                return true;
            case R.id.item_change:
                for (Integer pos : selectExtension.getSelections()) {
                    SimpleImageItem i = mItemAdapter.getAdapterItem(pos);
                    i.withName("CHANGED");
                    i.withDescription("This item was modified");
                    mItemAdapter.set(pos, i);
                }
                return true;
            case R.id.item_move:
                List items = mItemAdapter.getAdapterItems();
                if (items.size() > firstVisiblePosition + 3) {
                    mItemAdapter.move(firstVisiblePosition + 1, firstVisiblePosition + 3);
                }
                return true;
            case R.id.item_delete:
                selectExtension.deleteAllSelectedItems();
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //add the values which need to be saved from the drawer to the bundle
        outState = mResult.saveInstanceState(outState);
        //add the values which need to be saved from the adapter to the bundle
        outState = mFastAdapter.saveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        //handle the back press :D close the drawer first and if the drawer is closed close the activity
        if (mResult != null && mResult.isDrawerOpen()) {
            mResult.closeDrawer();
        } else {
            super.onBackPressed();
        }
    }
}
