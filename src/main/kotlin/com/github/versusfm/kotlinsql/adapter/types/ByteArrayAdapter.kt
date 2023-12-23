package com.github.versusfm.kotlinsql.adapter.types

import com.github.jasync.sql.db.RowData
import com.github.versusfm.kotlinsql.adapter.TypeAdapter

class ByteArrayAdapter : TypeAdapter<Array<Byte>> {
    override fun type(): Class<Array<Byte>> {
        return Array<Byte>::class.java
    }

    override fun convert(columnName: String, rowData: RowData): Array<Byte>? {
        return rowData.getAs(columnName)
    }
}