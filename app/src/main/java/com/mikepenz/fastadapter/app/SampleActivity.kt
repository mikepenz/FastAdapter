package com.mikepenz.fastadapter.app

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.LibsBuilder
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.mikepenz.fastadapter.app.dummy.ImageDummyData
import com.mikepenz.fastadapter.app.items.SimpleImageItem
import com.mikepenz.fastadapter.select.SelectExtension
import com.mikepenz.fastadapter.select.getSelectExtension
import com.mikepenz.iconics.IconicsColor
import com.mikepenz.iconics.IconicsDrawable
import com.mikepenz.iconics.typeface.library.community.material.CommunityMaterial
import com.mikepenz.iconics.typeface.library.materialdesigniconic.MaterialDesignIconic
import com.mikepenz.itemanimators.SlideDownAlphaAnimator
import com.mikepenz.materialdrawer.Drawer
import com.mikepenz.materialdrawer.DrawerBuilder
import com.mikepenz.materialdrawer.model.DividerDrawerItem
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem
import kotlinx.android.synthetic.main.activity_main.*

class SampleActivity : AppCompatActivity() {

    //save our header or result
    private lateinit var mResult: Drawer
    //save our FastAdapter
    private lateinit var mFastAdapter: FastAdapter<SimpleImageItem>
    //save our FastAdapter
    private lateinit var mItemAdapter: ItemAdapter<SimpleImageItem>
    //our `SelectExtension`
    private lateinit var selectExtension: SelectExtension<SimpleImageItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        findViewById<View>(android.R.id.content).systemUiVisibility = findViewById<View>(android.R.id.content).systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        //create the activity
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        // Handle Toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.title = ""

        //Create the drawer
        mResult = DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withHasStableIds(true)
                .withSavedInstance(savedInstanceState)
                .withShowDrawerOnFirstLaunch(true)
                .addDrawerItems(
                        PrimaryDrawerItem().withName(R.string.sample_icon_grid).withDescription(R.string.sample_icon_grid_descr).withSelectable(false).withIdentifier(8).withIcon(MaterialDesignIconic.Icon.gmi_grid),
                        PrimaryDrawerItem().withName(R.string.sample_simple_item_list).withDescription(R.string.sample_simple_item_list_descr).withSelectable(false).withIdentifier(6).withIcon(MaterialDesignIconic.Icon.gmi_format_align_justify),
                        PrimaryDrawerItem().withName(R.string.sample_image_list).withDescription(R.string.sample_image_list_descr).withSelectable(false).withIdentifier(5).withIcon(MaterialDesignIconic.Icon.gmi_wallpaper),
                        PrimaryDrawerItem().withName(R.string.sample_multi_select).withDescription(R.string.sample_multi_select_descr).withSelectable(false).withIdentifier(1).withIcon(MaterialDesignIconic.Icon.gmi_select_all),
                        PrimaryDrawerItem().withName(R.string.sample_collapsible).withDescription(R.string.sample_collapsible_descr).withSelectable(false).withIdentifier(2).withIcon(MaterialDesignIconic.Icon.gmi_check_all),
                        PrimaryDrawerItem().withName(R.string.sample_sticky_header).withDescription(R.string.sample_sticky_header_descr).withSelectable(false).withIdentifier(3).withIcon(MaterialDesignIconic.Icon.gmi_format_align_left),
                        PrimaryDrawerItem().withName(R.string.sample_advanced).withDescription(R.string.sample_advanced_descr).withSelectable(false).withIdentifier(4).withIcon(MaterialDesignIconic.Icon.gmi_coffee),
                        PrimaryDrawerItem().withName(R.string.sample_model_item).withDescription(R.string.sample_model_item_descr).withSelectable(false).withIdentifier(7).withIcon(MaterialDesignIconic.Icon.gmi_font),
                        PrimaryDrawerItem().withName(R.string.sample_multi_model_item).withDescription(R.string.sample_multi_model_item_descr).withSelectable(false).withIdentifier(9).withIcon(MaterialDesignIconic.Icon.gmi_format_list_numbered),
                        PrimaryDrawerItem().withName(R.string.sample_checkbox_item).withDescription(R.string.sample_checkbox_item_descr).withSelectable(false).withIdentifier(10).withIcon(CommunityMaterial.Icon.cmd_checkbox_marked),
                        PrimaryDrawerItem().withName(R.string.sample_radiobutton_item).withDescription(R.string.sample_radiobutton_item_descr).withSelectable(false).withIdentifier(11).withIcon(CommunityMaterial.Icon2.cmd_radiobox_marked),
                        PrimaryDrawerItem().withName(R.string.sample_swipe_list).withDescription(R.string.sample_swipe_list_descr).withSelectable(false).withIdentifier(12).withIcon(MaterialDesignIconic.Icon.gmi_format_align_left),
                        PrimaryDrawerItem().withName(R.string.sample_endless_scroll_list).withDescription(R.string.sample_endless_scroll_list_descr).withSelectable(false).withIdentifier(13).withIcon(MaterialDesignIconic.Icon.gmi_long_arrow_down),
                        PrimaryDrawerItem().withName(R.string.sample_sort).withDescription(R.string.sample_sort_descr).withSelectable(false).withIdentifier(14).withIcon(MaterialDesignIconic.Icon.gmi_sort_by_alpha),
                        PrimaryDrawerItem().withName(R.string.sample_mopub).withDescription(R.string.sample_mopub_descr).withSelectable(false).withIdentifier(15).withIcon(MaterialDesignIconic.Icon.gmi_accounts_list),
                        PrimaryDrawerItem().withName(R.string.sample_realm_list).withDescription(R.string.sample_realm_list_descr).withSelectable(false).withIdentifier(16).withIcon(MaterialDesignIconic.Icon.gmi_format_color_text),
                        PrimaryDrawerItem().withName(R.string.sample_collapsible_multi_select_delete).withDescription(R.string.sample_collapsible_multi_select_delete_descr).withSelectable(false).withIdentifier(17).withIcon(MaterialDesignIconic.Icon.gmi_check_all),
                        PrimaryDrawerItem().withName(R.string.sample_sticky_header_mopub).withDescription(R.string.sample_sticky_header_mopub_descr).withSelectable(false).withIdentifier(18).withIcon(MaterialDesignIconic.Icon.gmi_accounts_list),
                        PrimaryDrawerItem().withName(R.string.sample_diff_util).withDescription(R.string.sample_diff_util_descr).withSelectable(false).withIdentifier(19).withIcon(MaterialDesignIconic.Icon.gmi_refresh),
                        DividerDrawerItem(),
                        PrimaryDrawerItem().withName(R.string.open_source).withSelectable(false).withIdentifier(100).withIcon(MaterialDesignIconic.Icon.gmi_github)
                )
                .withOnDrawerItemClickListener(object : Drawer.OnDrawerItemClickListener {
                    override fun onItemClick(view: View?, position: Int, drawerItem: IDrawerItem<*>): Boolean {
                        var intent: Intent? = null
                        when {
                            drawerItem.identifier == 1L -> intent = Intent(this@SampleActivity, MultiselectSampleActivity::class.java)
                            drawerItem.identifier == 2L -> intent = Intent(this@SampleActivity, ExpandableSampleActivity::class.java)
                            drawerItem.identifier == 3L -> intent = Intent(this@SampleActivity, StickyHeaderSampleActivity::class.java)
                            drawerItem.identifier == 4L -> intent = Intent(this@SampleActivity, AdvancedSampleActivity::class.java)
                            drawerItem.identifier == 5L -> intent = Intent(this@SampleActivity, ImageListActivity::class.java)
                            drawerItem.identifier == 6L -> intent = Intent(this@SampleActivity, SimpleItemListActivity::class.java)
                            drawerItem.identifier == 7L -> intent = Intent(this@SampleActivity, ModelItemActivity::class.java)
                            drawerItem.identifier == 8L -> intent = Intent(this@SampleActivity, IconGridActivity::class.java)
                            drawerItem.identifier == 9L -> intent = Intent(this@SampleActivity, MultiTypeModelItemActivity::class.java)
                            drawerItem.identifier == 10L -> intent = Intent(this@SampleActivity, CheckBoxSampleActivity::class.java)
                            drawerItem.identifier == 11L -> intent = Intent(this@SampleActivity, RadioButtonSampleActivity::class.java)
                            drawerItem.identifier == 12L -> intent = Intent(this@SampleActivity, SwipeListActivity::class.java)
                            drawerItem.identifier == 13L -> intent = Intent(this@SampleActivity, EndlessScrollListActivity::class.java)
                            drawerItem.identifier == 14L -> intent = Intent(this@SampleActivity, SortActivity::class.java)
                            drawerItem.identifier == 15L -> intent = Intent(this@SampleActivity, MopubAdsActivity::class.java)
                            drawerItem.identifier == 16L -> intent = Intent(this@SampleActivity, RealmActivity::class.java)
                            drawerItem.identifier == 17L -> intent = Intent(this@SampleActivity, ExpandableMultiselectDeleteSampleActivity::class.java)
                            drawerItem.identifier == 18L -> intent = Intent(this@SampleActivity, StickyHeaderMopubAdsActivity::class.java)
                            drawerItem.identifier == 19L -> intent = Intent(this@SampleActivity, DiffUtilActivity::class.java)
                            drawerItem.identifier == 100L -> intent = LibsBuilder()
                                    .withFields(R.string::class.java.fields)
                                    .withActivityTitle(getString(R.string.open_source))
                                    .withActivityStyle(Libs.ActivityStyle.LIGHT)
                                    .withAboutIconShown(true)
                                    .withVersionShown(true)
                                    .withAboutVersionShown(true)
                                    .intent(this@SampleActivity)
                        }
                        if (intent != null) {
                            this@SampleActivity.startActivity(intent)
                        }
                        return false
                    }
                })
                .withSelectedItemByPosition(-1)
                .build()

        //create our ItemAdapter which will host our items
        mItemAdapter = ItemAdapter()

        //create our FastAdapter which will manage everything
        mFastAdapter = FastAdapter.with(listOf(mItemAdapter))
        selectExtension = mFastAdapter.getSelectExtension()
        selectExtension.apply {
            isSelectable = true
            multiSelect = true
            selectOnLongClick = false
        }

        //configure our fastAdapter
        //rv.setLayoutManager(new GridLayoutManager(this, 3));
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = mFastAdapter
        rv.itemAnimator = SlideDownAlphaAnimator().apply {
            addDuration = 500
            removeDuration = 500
        }

        //if we do this. the first added items will be animated :D
        Handler().postDelayed({
            //add some dummy data
            mItemAdapter.add(ImageDummyData.simpleImageItems)

            //restore selections (this has to be done after the items were added
            mFastAdapter.withSavedInstanceState(savedInstanceState)
        }, 50)
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        menu.findItem(R.id.item_add).icon = IconicsDrawable(this, MaterialDesignIconic.Icon.gmi_plus_square).color(IconicsColor.colorInt(Color.BLACK)).actionBar()
        menu.findItem(R.id.item_delete).icon = IconicsDrawable(this, MaterialDesignIconic.Icon.gmi_minus_square).color(IconicsColor.colorInt(Color.BLACK)).actionBar()
        menu.findItem(R.id.item_change).icon = IconicsDrawable(this, MaterialDesignIconic.Icon.gmi_settings_square).color(IconicsColor.colorInt(Color.BLACK)).actionBar()
        menu.findItem(R.id.item_move).icon = IconicsDrawable(this, MaterialDesignIconic.Icon.gmi_format_valign_bottom).color(IconicsColor.colorInt(Color.BLACK)).actionBar()
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //find out the current visible position
        var firstVisiblePosition = 0
        if (rv.layoutManager is LinearLayoutManager) {
            firstVisiblePosition = (rv.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
        } else if (rv.layoutManager is GridLayoutManager) {
            firstVisiblePosition = (rv.layoutManager as GridLayoutManager).findFirstVisibleItemPosition()
        }

        //handle the menu item click
        when (item.itemId) {
            R.id.item_add -> {
                mItemAdapter.add(firstVisiblePosition + 1, ImageDummyData.dummyItem)
                return true
            }
            R.id.item_change -> {
                for (pos in selectExtension.selections) {
                    val i = mItemAdapter.getAdapterItem(pos)
                    i.withName("CHANGED")
                    i.withDescription("This item was modified")
                    mItemAdapter[pos] = i
                }
                return true
            }
            R.id.item_move -> {
                val items = mItemAdapter.adapterItems
                if (items.size > firstVisiblePosition + 3) {
                    mItemAdapter.move(firstVisiblePosition + 1, firstVisiblePosition + 3)
                }
                return true
            }
            R.id.item_delete -> {
                selectExtension.deleteAllSelectedItems()
                return true
            }
            android.R.id.home -> {
                onBackPressed()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onSaveInstanceState(_outState: Bundle) {
        var outState = _outState
        //add the values which need to be saved from the drawer to the bundle
        outState = mResult.saveInstanceState(outState)
        //add the values which need to be saved from the adapter to the bundle
        outState = mFastAdapter.saveInstanceState(outState)
        super.onSaveInstanceState(outState)
    }

    override fun onBackPressed() {
        //handle the back press :D close the drawer first and if the drawer is closed close the activity
        if (mResult.isDrawerOpen) {
            mResult.closeDrawer()
        } else {
            super.onBackPressed()
        }
    }
}
