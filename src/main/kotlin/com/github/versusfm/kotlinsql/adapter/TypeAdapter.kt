package com.github.versusfm.kotlinsql.adapter

import com.github.jasync.sql.db.RowData

interface TypeAdapter<T> {
    fun type(): Class<T>
    fun convert(columnName: String, rowData: RowData): T?
}