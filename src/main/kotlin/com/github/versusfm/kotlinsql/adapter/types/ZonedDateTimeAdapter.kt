package com.github.versusfm.kotlinsql.adapter.types

import com.github.jasync.sql.db.RowData
import com.github.versusfm.kotlinsql.adapter.TypeAdapter
import java.time.ZonedDateTime

class ZonedDateTimeAdapter : TypeAdapter<ZonedDateTime> {
    override fun type(): Class<ZonedDateTime> {
        return ZonedDateTime::class.java
    }

    override fun convert(columnName: String, rowData: RowData): ZonedDateTime? {
        return rowData.getAs(columnName)
    }
}