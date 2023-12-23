package com.github.versusfm.kotlinsql.adapter.types

import com.github.jasync.sql.db.RowData
import com.github.versusfm.kotlinsql.adapter.TypeAdapter
import java.time.LocalTime

class LocalTimeAdapter : TypeAdapter<LocalTime> {
    override fun type(): Class<LocalTime> {
        return LocalTime::class.java
    }

    override fun convert(columnName: String, rowData: RowData): LocalTime? {
        return rowData.getAs(columnName)
    }
}