package com.mikepenz.fastadapter.app

import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.annotation.IntDef
import androidx.annotation.IntRange
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.ButterKnife
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.IAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.mikepenz.fastadapter.app.items.SimpleItem
import com.mikepenz.fastadapter.select.SelectExtension
import com.mikepenz.fastadapter.select.getSelectExtension
import com.mikepenz.fastadapter.utils.ComparableItemListImpl
import com.mikepenz.iconics.IconicsDrawable
import com.mikepenz.iconics.typeface.library.materialdesigniconic.MaterialDesignIconic
import com.mikepenz.iconics.utils.actionBar
import com.mikepenz.iconics.utils.colorInt
import kotlinx.android.synthetic.main.activity_sample.*
import java.io.Serializable
import java.util.*

/**
 * An Activity which showcases the sort feature of the library.
 *
 * @author Ruben Gees
 */
class SortActivity : AppCompatActivity() {

    //save our FastAdapter
    private lateinit var fastAdapter: FastAdapter<SimpleItem>
    private lateinit var itemAdapter: ItemAdapter<SimpleItem>
    private lateinit var itemListImpl: ComparableItemListImpl<SimpleItem>
    private lateinit var selectExtension: SelectExtension<SimpleItem>

    @SortingStrategy
    private var sortingStrategy: Int = 0

    /**
     * Returns the appropriate Comparator for the current sorting strategy or null if no strategy is
     * set. (SORT_NONE)
     *
     * @return The comparator or null.
     */
    private val comparator: Comparator<SimpleItem>?
        get() {
            when (sortingStrategy) {
                SORT_ASCENDING -> return AlphabetComparatorAscending()
                SORT_DESCENDING -> return AlphabetComparatorDescending()
                SORT_NONE -> return null
            }

            throw RuntimeException("This sortingStrategy is not supported.")
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sample)

        ButterKnife.bind(this)

        // Handle Toolbar
        setSupportActionBar(toolbar)

        //create our FastAdapter which will manage everything
        itemListImpl = ComparableItemListImpl(comparator)
        itemAdapter = ItemAdapter(itemListImpl)
        fastAdapter = FastAdapter.with(itemAdapter)
        selectExtension = fastAdapter.getSelectExtension()
        selectExtension.isSelectable = true

        //configure our fastAdapter
        fastAdapter.onClickListener = { _: View?, _: IAdapter<SimpleItem>, item: SimpleItem, _: Int ->
            Toast.makeText(this@SortActivity, item.name?.getText(this@SortActivity), Toast.LENGTH_LONG).show()
            false
        }

        //get our recyclerView and do basic setup
        val recyclerView = findViewById<View>(R.id.rv) as RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.adapter = fastAdapter

        sortingStrategy = if (savedInstanceState != null) {
            //Retrieve the previous sorting strategy from the instance state
            toSortingStrategy(savedInstanceState.getInt("sorting_strategy"))
        } else {
            //Set the default so
            SORT_NONE
        }

        //initial filling of the list
        itemAdapter.setNewList(generateUnsortedList())

        //restore selections (this has to be done after the items were added
        fastAdapter.withSavedInstanceState(savedInstanceState)

        //set the back arrow in the toolbar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(false)
    }

    @SortingStrategy
    internal fun toSortingStrategy(`val`: Int): Int {
        return `val`
    }

    override fun onSaveInstanceState(_outState: Bundle) {
        var outState = _outState
        //add the values which need to be saved from the adapter to the bundle
        outState = fastAdapter.saveInstanceState(outState)
        //We need to persist our sorting strategy between orientation changes
        outState.putInt("sorting_strategy", sortingStrategy)
        super.onSaveInstanceState(outState)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.sort, menu)
        menu.findItem(R.id.item_sort_random).icon = IconicsDrawable(this, MaterialDesignIconic.Icon.gmi_wrap_text).apply { colorInt = Color.BLACK; actionBar() }
        menu.findItem(R.id.item_sort_asc).icon = IconicsDrawable(this, MaterialDesignIconic.Icon.gmi_sort_asc).apply { colorInt = Color.BLACK; actionBar() }
        menu.findItem(R.id.item_sort_desc).icon = IconicsDrawable(this, MaterialDesignIconic.Icon.gmi_sort_desc).apply { colorInt = Color.BLACK; actionBar() }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        //handle the menu item click
        when (item.itemId) {
            R.id.item_sort_random -> {
                //Set the new sorting strategy
                sortingStrategy = SORT_NONE
                //randomize the items
                itemAdapter.adapterItems.shuffle()
                fastAdapter.notifyDataSetChanged()
                return true
            }
            R.id.item_sort_asc -> {
                //Set the new sorting strategy
                sortingStrategy = SORT_ASCENDING
                //Set the new comparator to the list
                itemListImpl.withComparator(comparator)
                return true
            }
            R.id.item_sort_desc -> {
                //Set the new sorting strategy
                sortingStrategy = SORT_DESCENDING
                //Set the new comparator to the list
                itemListImpl.withComparator(comparator)
                return true
            }
            android.R.id.home -> {
                Toast.makeText(applicationContext, "selections = " + selectExtension.selections, Toast.LENGTH_LONG).show()
                onBackPressed()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    /**
     * Generates a simple list consisting of the letters of the alphabet, unordered on purpose.
     *
     * @return The new list.
     */
    private fun generateUnsortedList(): List<SimpleItem> {
        val result = ArrayList<SimpleItem>(26)
        for (i in 0..25) {
            result.add(makeItem(i))
        }
        result.shuffle()
        return result
    }

    /**
     * Build a simple item with one letter of the alphabet.
     *
     * @param position The position of the letter in the alphabet.
     * @return The new item.
     */
    private fun makeItem(@IntRange(from = 0, to = 25) _position: Int): SimpleItem {
        var position = _position
        val result = SimpleItem()

        result.withName(ALPHABET[position])

        position++

        var description = "The $position"

        description += if (position == 1 || position == 21) {
            "st"
        } else if (position == 2 || position == 22) {
            "nd"
        } else if (position == 3 || position == 23) {
            "rd"
        } else {
            "th"
        }

        return result.withDescription("$description letter in the alphabet")
    }

    @Retention(AnnotationRetention.SOURCE)
    @IntDef(SORT_NONE, SORT_ASCENDING, SORT_DESCENDING)
    annotation class SortingStrategy

    /**
     * A simple Comparator to sort the items ascending.
     */
    private inner class AlphabetComparatorAscending : Comparator<SimpleItem>, Serializable {
        override fun compare(lhs: SimpleItem, rhs: SimpleItem): Int {
            return lhs.name?.textString.toString().compareTo(rhs.name?.textString.toString())
        }
    }

    /**
     * A simple Comparator to sort the items descending.
     */
    private inner class AlphabetComparatorDescending : Comparator<SimpleItem>, Serializable {
        override fun compare(lhs: SimpleItem, rhs: SimpleItem): Int {
            return rhs.name?.textString.toString().compareTo(lhs.name?.textString.toString())
        }
    }

    companion object {
        private const val SORT_ASCENDING = 0
        private const val SORT_DESCENDING = 1
        private const val SORT_NONE = -1

        private val ALPHABET = arrayOf("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z")
    }
}
