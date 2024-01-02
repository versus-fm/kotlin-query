package com.github.versusfm.kotlinsql.adapter

import com.github.jasync.sql.db.Connection
import com.github.jasync.sql.db.QueryResult
import com.github.jasync.sql.db.RowData
import com.github.versusfm.kotlinsql.adapter.types.*

interface ObjectAdapter {

    companion object {
        fun createDefaultAdapter(): ObjectAdapter {
            return createDefaultAdapter(emptyList())
        }

        fun createDefaultAdapter(typeAdapters: List<TypeAdapter<*>>): ObjectAdapter {
            val allAdapters: MutableList<TypeAdapter<*>> = mutableListOf(
                BigDecimalAdapter(),
                ByteArrayAdapter(),
                DoubleAdapter(),
                FloatAdapter(),
                IntAdapter(),
                LocalDateAdapter(),
                LocalDateTimeAdapter(),
                LocalTimeAdapter(),
                LongAdapter(),
                ShortAdapter(),
                StringAdapter(),
                UuidAdapter(),
                ZonedDateTimeAdapter()
            )
            allAdapters.addAll(typeAdapters)
            return DefaultObjectAdapter(allAdapters)
        }
    }

    fun <T> project(type: Class<T>, row: RowData): T
    fun <T> project(type: Class<T>, queryResult: QueryResult): Sequence<T> {
        return queryResult.rows.asSequence().map { this.project(type, it) }
    }
}
