package com.mikepenz.fastadapter.app.paged

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * https://www.zoftino.com/pagination-in-android-using-paging-library
 */
@Entity
class DemoEntity {
    @PrimaryKey(autoGenerate = true)
    var identifier: Int = 0
    var data1: String? = null
    var data2: String? = null

    constructor() {
        // required
    }

    constructor(data1: String, data2: String) {
        this.data1 = data1
        this.data2 = data2
    }


    override fun equals(obj: Any?): Boolean {
        if (obj === this)
            return true
        val demoEntity = obj as DemoEntity?
        return demoEntity!!.identifier == this.identifier &&
                demoEntity.data1 === demoEntity.data1 &&
                demoEntity.data2 === demoEntity.data2
    }

    override fun hashCode(): Int {
        var result = identifier
        result = 31 * result + (data1?.hashCode() ?: 0)
        result = 31 * result + (data2?.hashCode() ?: 0)
        return result
    }
}
