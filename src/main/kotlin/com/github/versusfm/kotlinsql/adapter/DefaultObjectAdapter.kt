package com.github.versusfm.kotlinsql.adapter

import com.github.jasync.sql.db.RowData
import java.lang.RuntimeException
import java.util.concurrent.ConcurrentHashMap
import java.util.stream.Collectors

class DefaultObjectAdapter(private val typeAdapters: List<TypeAdapter<*>>) : ObjectAdapter {
    private val mappedTypeAdapters: Map<Class<*>, List<TypeAdapter<*>>> = typeAdapters
        .stream()
        .collect(Collectors.groupingBy { it.type() })
    private val mappedBlueprints: MutableMap<Class<*>, Record.Blueprint<*>> = ConcurrentHashMap()
    private val mappedRowMappers: MutableMap<Class<*>, (row: RowData, column: String) -> Any?> = ConcurrentHashMap()

    override fun <T> project(type: Class<T>, row: RowData): T {
        val blueprint = getBlueprint(type)
        val mapper = getRowMapper(type)

        return blueprint.construct(row, mapper)
    }

    private fun <T> getBlueprint(type: Class<T>): Record.Blueprint<T> {
        if (mappedBlueprints.containsKey(type)) {
            return (mappedBlueprints[type] as Record.Blueprint<T>)!!
        }
        mappedBlueprints[type] = Record.createBlueprint(type)
        return (mappedBlueprints[type] as Record.Blueprint<T>)!!
    }

    private fun <T> getRowMapper(type: Class<T>): (row: RowData, column: String) -> Any? {
        if (mappedRowMappers.containsKey(type)) {
            return mappedRowMappers[type]!!
        }
        mappedRowMappers[type] = createRowMapper(type)
        return mappedRowMappers[type]!!
    }

    private fun <T> createRowMapper(type: Class<T>): (row: RowData, column: String) -> Any? {
        val blueprint = getBlueprint(type)
        val columnAdapters: Map<String, (row: RowData, column: String) -> Any?> = blueprint.columns.stream()
                .collect(Collectors.toMap({it.columnName}) { createTypeAdapter(it) })

        return {row, column ->
            columnAdapters[column]?.let { it(row, column) }
        }
    }

    private fun createTypeAdapter(boundColumn: Record.BoundColumn): (row: RowData, column: String) -> Any? {
        val columnType = boundColumn.getColumnType()
        val adapter = mappedTypeAdapters[columnType].orThrow { RuntimeException("Missing type adapter for $columnType") }

        return {row, column ->
            adapter.first.convert(column, row)
        }
    }

    private fun <T: Any> T?.orThrow(supplier: () -> Throwable): T {
        if (this == null) {
            throw supplier()
        } else {
            return this
        }
    }
}
