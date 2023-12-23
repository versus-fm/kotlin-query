package com.github.versusfm.kotlinsql.adapter.types

import com.github.jasync.sql.db.RowData
import com.github.versusfm.kotlinsql.adapter.TypeAdapter

class DoubleAdapter : TypeAdapter<Double> {
    override fun type(): Class<Double> {
        return Double::class.java
    }

    override fun convert(columnName: String, rowData: RowData): Double? {
        return rowData.getDouble(columnName)
    }

}
