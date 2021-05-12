package com.mikepenz.fastadapter.app.paged

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query


/**
 * https://www.zoftino.com/pagination-in-android-using-paging-library
 */
@Dao
interface DemoEntityLocalDAO {
    //to fetch data required to display in each page
    @Query("SELECT * FROM DemoEntity")
    fun getAll(): DataSource.Factory<Int, DemoEntity>

    //this is used to populate db
    @Insert
    fun insertDemoEntities(demoEntities: List<DemoEntity>)

    @Query("UPDATE DemoEntity SET data1 = -data1")
    fun updateDemoEntities()
}