package com.github.versusfm.kotlinsql.adapter.types

import com.github.jasync.sql.db.RowData
import com.github.versusfm.kotlinsql.adapter.TypeAdapter
import java.time.LocalDate

class LocalDateAdapter : TypeAdapter<LocalDate> {
    override fun type(): Class<LocalDate> {
        return LocalDate::class.java
    }

    override fun convert(columnName: String, rowData: RowData): LocalDate? {
        return rowData.getAs(columnName)
    }
}