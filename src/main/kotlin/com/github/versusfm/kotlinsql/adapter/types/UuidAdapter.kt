package com.github.versusfm.kotlinsql.adapter.types

import com.github.jasync.sql.db.RowData
import com.github.versusfm.kotlinsql.adapter.TypeAdapter
import java.util.UUID

class UuidAdapter : TypeAdapter<UUID> {
    override fun type(): Class<UUID> {
        return UUID::class.java
    }

    override fun convert(columnName: String, rowData: RowData): UUID? {
        return rowData.getString(columnName)?.let { UUID.fromString(it) }
    }
}