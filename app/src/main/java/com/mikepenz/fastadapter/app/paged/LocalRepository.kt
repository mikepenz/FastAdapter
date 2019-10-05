package com.mikepenz.fastadapter.app.paged

import android.annotation.SuppressLint
import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import java.util.*
import java.util.concurrent.Executors

/**
 * https://www.zoftino.com/pagination-in-android-using-paging-library
 */
@SuppressLint("StaticFieldLeak")
object LocalRepository {
    private var demoEntitiesDB: DemoEntitiesDB? = null
    private val LOCK = Any()
    private var ctx: Context? = null

    private val dbCallback = object : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            Executors.newSingleThreadScheduledExecutor().execute { addDemoEntities(ctx) }
        }
    }

    private val initDemoEntities = arrayOf("1|Description 1", "2|Description 2", "3|Description 3", "4|Description 4", "5|Description 5", "6|Description 6", "7|Description 7", "8|Description 8", "9|Description 9", "10|Description 10", "11|Description 11", "12|Description 12", "13|Description 13", "14|Description 14", "15|Description 15", "16|Description 16", "17|Description 17", "18|Description 18", "19|Description 19", "20|Description 20", "21|Description 21", "22|Description 22", "23|Description 23", "24|Description 24", "25|Description 25", "26|Description 26", "27|Description 27", "28|Description 28", "29|Description 29", "30|Description 30", "31|Description 31", "32|Description 32", "33|Description 33", "34|Description 34", "35|Description 35", "36|Description 36", "37|Description 37", "38|Description 38", "39|Description 39", "40|Description 40", "41|Description 41", "42|Description 42", "43|Description 43", "44|Description 44", "45|Description 45", "46|Description 46", "47|Description 47", "48|Description 48", "49|Description 49", "50|Description 50", "51|Description 51", "52|Description 52", "53|Description 53", "54|Description 54", "55|Description 55", "56|Description 56", "57|Description 57", "58|Description 58", "59|Description 59", "60|Description 60", "61|Description 61", "62|Description 62", "63|Description 63", "64|Description 64", "65|Description 65", "66|Description 66", "67|Description 67", "68|Description 68", "69|Description 69", "70|Description 70", "71|Description 71", "72|Description 72", "73|Description 73", "74|Description 74", "75|Description 75", "76|Description 76", "77|Description 77", "78|Description 78", "79|Description 79", "80|Description 80", "81|Description 81", "82|Description 82", "83|Description 83", "84|Description 84", "85|Description 85", "86|Description 86", "87|Description 87", "88|Description 88", "89|Description 89", "90|Description 90", "91|Description 91", "92|Description 92", "93|Description 93", "94|Description 94", "95|Description 95", "96|Description 96", "97|Description 97", "98|Description 98", "99|Description 99", "100|Description 100")

    @Synchronized
    fun getDemoEntityDB(context: Context?): DemoEntitiesDB {
        if (demoEntitiesDB == null) {
            ctx = context
            synchronized(LOCK) {
                if (demoEntitiesDB == null) {
                    demoEntitiesDB = Room.databaseBuilder(context!!,
                            DemoEntitiesDB::class.java, "DemoEntities Database")
                            .fallbackToDestructiveMigration()
                            .addCallback(dbCallback).build()
                }
            }
        }
        return demoEntitiesDB!!
    }

    private fun addDemoEntities(ctx: Context?) {
        val demoEntitiesList = ArrayList<DemoEntity>()

        for (s in initDemoEntities) {
            val ss = s.split("\\|".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            demoEntitiesList.add(DemoEntity(ss[0], ss[1]))
        }
        getDemoEntityDB(ctx).demoEntityDAO().insertDemoEntities(demoEntitiesList)
    }
}