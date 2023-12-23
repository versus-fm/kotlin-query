package com.github.versusfm.kotlinsql.adapter.types

import com.github.jasync.sql.db.RowData
import com.github.versusfm.kotlinsql.adapter.TypeAdapter
import java.time.LocalDateTime

class LocalDateTimeAdapter : TypeAdapter<LocalDateTime> {
    override fun type(): Class<LocalDateTime> {
        return LocalDateTime::class.java
    }

    override fun convert(columnName: String, rowData: RowData): LocalDateTime? {
        return rowData.getDate(columnName)
    }
}