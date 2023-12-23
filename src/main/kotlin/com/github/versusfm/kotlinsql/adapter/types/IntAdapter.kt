package com.github.versusfm.kotlinsql.adapter.types

import com.github.jasync.sql.db.RowData
import com.github.versusfm.kotlinsql.adapter.TypeAdapter

class IntAdapter : TypeAdapter<Int> {
    override fun type(): Class<Int> {
        return Int::class.java
    }

    override fun convert(columnName: String, rowData: RowData): Int? {
        return rowData.getInt(columnName)
    }
}