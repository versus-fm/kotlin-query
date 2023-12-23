package com.github.versusfm.kotlinsql.adapter.types

import com.github.jasync.sql.db.RowData
import com.github.versusfm.kotlinsql.adapter.TypeAdapter

class ShortAdapter : TypeAdapter<Short> {
    override fun type(): Class<Short> {
        return Short::class.java
    }

    override fun convert(columnName: String, rowData: RowData): Short? {
        return rowData.getAs(columnName)
    }
}