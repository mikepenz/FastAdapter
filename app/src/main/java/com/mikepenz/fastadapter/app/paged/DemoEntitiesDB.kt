package com.mikepenz.fastadapter.app.paged

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * https://www.zoftino.com/pagination-in-android-using-paging-library
 */
@Database(entities = [DemoEntity::class], version = 1)
abstract class DemoEntitiesDB : RoomDatabase() {
    abstract fun demoEntityDAO(): DemoEntityLocalDAO
}