package com.github.versusfm.kotlinsql.adapter.types

import com.github.jasync.sql.db.RowData
import com.github.versusfm.kotlinsql.adapter.TypeAdapter

class StringAdapter : TypeAdapter<String> {
    override fun type(): Class<String> {
        return String::class.java
    }

    override fun convert(columnName: String, rowData: RowData): String? {
        return rowData.getString(columnName)
    }
}