package com.mikepenz.fastadapter.app

import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.mikepenz.fastadapter.IAdapter
import com.mikepenz.fastadapter.adapters.FastItemAdapter
import com.mikepenz.fastadapter.app.items.RealmSampleUserItem
import com.mikepenz.iconics.IconicsDrawable
import com.mikepenz.iconics.typeface.library.materialdesigniconic.MaterialDesignIconic
import com.mikepenz.iconics.utils.actionBar
import com.mikepenz.iconics.utils.colorInt
import com.mikepenz.itemanimators.AlphaInAnimator
import io.realm.Realm
import io.realm.RealmChangeListener
import io.realm.RealmResults
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class RealmActivity : AppCompatActivity() {
    //save our FastAdapter
    private lateinit var mFastItemAdapter: FastItemAdapter<RealmSampleUserItem>
    //save our Realm instance to close it later
    private lateinit var mRealm: Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sample)

        // Handle Toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setTitle(R.string.sample_realm_list)

        //create our FastAdapter which will manage everything
        mFastItemAdapter = FastItemAdapter()

        //configure our fastAdapter
        mFastItemAdapter.onClickListener = { v: View?, _: IAdapter<RealmSampleUserItem>, item: RealmSampleUserItem, _: Int ->
            v?.let { Toast.makeText(it.context, item.name, Toast.LENGTH_SHORT).show() }
            false
        }

        //get our recyclerView and do basic setup
        rv.layoutManager = LinearLayoutManager(this)
        rv.itemAnimator = AlphaInAnimator()
        rv.adapter = mFastItemAdapter

        //Get a realm instance for this activity
        mRealm = Realm.getDefaultInstance()

        //Add a realm on change listener (don´t forget to close this realm instance before adding this listener again)
        mRealm.where(RealmSampleUserItem::class.java).findAllAsync().addChangeListener { userItems ->
            //This will call twice
            //1.) from findAllAsync()
            //2.) from createData()
            mFastItemAdapter.setNewList(userItems)
        }

        //fill with some sample data
        createData()

        //set the back arrow in the toolbar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(false)

        //restore selections (this has to be done after the items were added
        mFastItemAdapter.withSavedInstanceState(savedInstanceState)
    }

    private fun createData() {
        //Execute transaction
        mRealm.executeTransactionAsync { realm ->
            val users = LinkedList<RealmSampleUserItem>()
            for (i in 1..5) {
                val user = RealmSampleUserItem()
                user.withName("Sample Realm Element $i").withIdentifier(i.toLong())
                users.add(user)
            }
            //insert the created objects to realm
            //a bulk insert has lower object allocations then a copy
            realm.insertOrUpdate(users)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_add, menu)
        menu.findItem(R.id.item_add).icon = IconicsDrawable(this, MaterialDesignIconic.Icon.gmi_plus_square).apply { colorInt = Color.BLACK; actionBar() }
        return true
    }

    override fun onSaveInstanceState(_outState: Bundle) {
        var outState = _outState
        //add the values which need to be saved from the adapter to the bundle
        outState = mFastItemAdapter.saveInstanceState(outState)
        super.onSaveInstanceState(outState)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //handle the click on the back arrow click
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
            R.id.item_add -> {
                mRealm.where(RealmSampleUserItem::class.java).findAllAsync().addChangeListener(object : RealmChangeListener<RealmResults<RealmSampleUserItem>> {
                    override fun onChange(userItems: RealmResults<RealmSampleUserItem>) {
                        //Remove the change listener
                        userItems.removeChangeListener(this)
                        //Store the primary key to get access from a other thread
                        val newPrimaryKey = (userItems.last()?.identifier ?: 0) + 1
                        mRealm.executeTransactionAsync { realm ->
                            val newUser = realm.createObject(RealmSampleUserItem::class.java, newPrimaryKey)
                            newUser.withName("Sample Realm Element $newPrimaryKey")
                        }
                    }
                })
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    //Prevent the realm instance from leaking
    override fun onDestroy() {
        super.onDestroy()
        closeRealm()
    }

    private fun closeRealm() {
        if (!mRealm.isClosed) {
            mRealm.close()
        }
    }
}
