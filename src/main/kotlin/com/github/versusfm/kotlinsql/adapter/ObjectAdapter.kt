package com.github.versusfm.kotlinsql.adapter

import com.github.jasync.sql.db.Connection
import com.github.jasync.sql.db.QueryResult
import com.github.jasync.sql.db.RowData

interface ObjectAdapter {
    fun <T> project(type: Class<T>, row: RowData): T
    fun <T> project(type: Class<T>, queryResult: QueryResult): Sequence<T> {
        return queryResult.rows.asSequence().map { this.project(type, it) }
    }
}
