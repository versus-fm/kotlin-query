package com.github.versusfm.kotlinsql.adapter

import com.github.jasync.sql.db.RowData
import com.github.versusfm.kotlinsql.annotation.RecordConstructor
import com.github.versusfm.kotlinsql.query.QueryContext
import com.github.versusfm.kotlinsql.query.QueryContext.Companion.resolveColumnName
import java.lang.reflect.Constructor
import java.lang.reflect.Field
import java.lang.reflect.Parameter
import java.util.stream.Collectors

class Record {

    companion object {
        fun <T> createBlueprint(type: Class<T>): Blueprint<T> {
            val ctor = type.constructors
                .filter { it.isAnnotationPresent(RecordConstructor::class.java) }
                .first ?: type.constructors.first()
            val boundColumns = ArrayList<BoundColumn>()
            val mappedColumns = HashSet<String>()
            ctor.parameters.forEachIndexed { index, parameter ->
                val columnName = resolveColumnName(parameter)
                boundColumns.add(BoundColumn.ConstructorArg(columnName, index, parameter))
                mappedColumns.add(columnName)
            }
            type.fields.forEach {
                val columnName = resolveColumnName(it)
                if (columnName !in mappedColumns) {
                    boundColumns.add(BoundColumn.Property(columnName, it))
                    mappedColumns.add(columnName)
                }
            }
            return Blueprint<T>(type, boundColumns, ctor as Constructor<T>)
        }
    }

    data class Blueprint<T>(val type: Class<T>, val columns: List<BoundColumn>, val ctor: Constructor<T>) {
        private val indexedBoundArgs = columns.stream()
            .filter { it is BoundColumn.ConstructorArg }
            .map { it as BoundColumn.ConstructorArg }
            .collect(Collectors.toMap({ it.index }, { it }))

        fun construct(row: RowData, values: (row: RowData, column: String) -> Any?): T {
            val ctorArgs: Array<Any?> = Array(ctor.parameterCount) { null }
            for (i in 0..ctor.parameterCount) {
                ctorArgs[i] = indexedBoundArgs[i]?.columnName?.let { values(row, it) }
            }
            val record = ctor.newInstance(*ctorArgs)
            columns.forEach {
                when (it) {
                    is BoundColumn.Property -> {
                        it.field.set(record, values(row, it.columnName))
                    }
                    is BoundColumn.ConstructorArg -> {}
                }
            }
            return record
        }
    }
    
    sealed interface BoundColumn {
        val columnName: String
        fun getColumnType(): Class<*>
        data class ConstructorArg(override val columnName: String, val index: Int, val parameter: Parameter) : BoundColumn {
            override fun getColumnType(): Class<*> {
                return parameter.type
            }
        }

        data class Property(override val columnName: String, val field: Field) : BoundColumn {
            override fun getColumnType(): Class<*> {
                return field.type
            }
        }
    }
}
