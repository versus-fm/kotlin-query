package com.github.versusfm.kotlinsql.adapter.types

import com.github.jasync.sql.db.RowData
import com.github.versusfm.kotlinsql.adapter.TypeAdapter

class FloatAdapter : TypeAdapter<Float> {
    override fun type(): Class<Float> {
        return Float::class.java
    }

    override fun convert(columnName: String, rowData: RowData): Float? {
        return rowData.getFloat(columnName)
    }

}
