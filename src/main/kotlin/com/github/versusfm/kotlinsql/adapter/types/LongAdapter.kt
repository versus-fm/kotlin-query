package com.github.versusfm.kotlinsql.adapter.types

import com.github.jasync.sql.db.RowData
import com.github.versusfm.kotlinsql.adapter.TypeAdapter

class LongAdapter : TypeAdapter<Long> {
    override fun type(): Class<Long> {
        return Long::class.java
    }

    override fun convert(columnName: String, rowData: RowData): Long? {
        return rowData.getLong(columnName)
    }

}
