package com.mikepenz.fastadapter.app.paged

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

/**
 * https://www.zoftino.com/pagination-in-android-using-paging-library
 */
@Dao
interface DemoEntityLocalDAO {
    //to fetch data required to display in each page
    @Query("SELECT * FROM DemoEntity WHERE  identifier >= :id ORDER BY identifier LIMIT :size")
    fun getDemoEntitiesBySize(id: Int, size: Int): List<DemoEntity>

    //this is used to populate db
    @Insert
    fun insertDemoEntities(demoEntities: List<DemoEntity>)
}