package com.mikepenz.fastadapter.app

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.ModelAdapter
import com.mikepenz.fastadapter.app.adapters.FastScrollIndicatorAdapter
import com.mikepenz.fastadapter.app.model.IconModel
import com.mikepenz.fastadapter.app.model.ModelIconItem
import com.mikepenz.fastadapter.select.getSelectExtension
import com.mikepenz.iconics.Iconics
import com.mikepenz.itemanimators.SlideDownAlphaAnimator
import com.turingtechnologies.materialscrollbar.CustomIndicator
import com.turingtechnologies.materialscrollbar.DragScrollBar
import kotlinx.android.synthetic.main.activity_sample.*
import java.util.ArrayList
import kotlin.Comparator

class ModelItemActivity : AppCompatActivity() {
    //save our FastAdapter
    private lateinit var fastAdapter: FastAdapter<ModelIconItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sample)

        // Handle Toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setTitle(R.string.sample_model_item)

        //adapters
        val fastScrollIndicatorAdapter = FastScrollIndicatorAdapter<ModelIconItem>()
        val itemAdapter = ModelAdapter { model: IconModel ->
            ModelIconItem(model)
        }

        //create our FastAdapter which will manage everything
        fastAdapter = FastAdapter.with(listOf(itemAdapter))
        val selectExtension = fastAdapter.getSelectExtension()
        selectExtension.isSelectable = true

        //init our gridLayoutManager and configure RV
        rv.adapter = fastScrollIndicatorAdapter.wrap(fastAdapter)

        val materialScrollBar = findViewById<DragScrollBar>(R.id.dragScrollBar)
        materialScrollBar.visibility = View.VISIBLE
        materialScrollBar.setHandleColor(ContextCompat.getColor(this, R.color.colorAccent))
        materialScrollBar.setHandleOffColor(ContextCompat.getColor(this, R.color.colorAccent))
        materialScrollBar.setIndicator(CustomIndicator(this), true)

        rv.layoutManager = GridLayoutManager(this, 3)
        rv.itemAnimator = SlideDownAlphaAnimator()

        //order fonts by their name
        val mFonts = ArrayList(Iconics.getRegisteredFonts(this))
        mFonts.sortWith(Comparator { object1, object2 -> object1.fontName.compareTo(object2.fontName) })

        //add all icons of all registered Fonts to the list
        val models = ArrayList<IconModel>()
        for (font in mFonts) {
            for (icon in font.icons) {
                models.add(IconModel(font.getIcon(icon)))
            }
        }

        //fill with some sample data
        itemAdapter.add(models)

        //restore selections (this has to be done after the items were added
        fastAdapter.withSavedInstanceState(savedInstanceState)

        //set the back arrow in the toolbar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(false)
    }

    override fun onSaveInstanceState(_outState: Bundle) {
        var outState = _outState
        //add the values which need to be saved from the adapter to the bundle
        outState = fastAdapter.saveInstanceState(outState)
        super.onSaveInstanceState(outState)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //handle the click on the back arrow click
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
