package com.github.versusfm.kotlinsql.ast

import com.github.versusfm.kotlinsql.query.QueryContext

sealed interface ValueNode : QueryNode {
    data class TableColumn(val tableName: String, val tableColumn: String) : ValueNode{
        override fun compile(context: QueryContext<*>): String {
            return "\"${tableName}\".\"${tableColumn}\""
        }
    }
    data class FunctionCall(val functionName: String, val params: List<ValueNode>) : ValueNode {
        override fun compile(context: QueryContext<*>): String {
            return "${functionName}(${params.joinToString(", ") { it.compile(context) }})"
        }
    }
    data class NamedParam<T: Any>(val value: T) : ValueNode {
        override fun compile(context: QueryContext<*>): String {
            return ":${context.putParamValue(value)}"
        }
    }
    data class UnresolvedReference<T>(val ref: T) : ValueNode {
        override fun compile(context: QueryContext<*>): String {
            TODO("Not yet implemented")
        }
    }
    data class BooleanCondition(val condition: ConditionClause) : ValueNode {
        override fun compile(context: QueryContext<*>): String {
            return condition.compile(context)
        }
    }
    data class SubQuery<T>(val query: QueryContext<T>) : ValueNode {
        override fun compile(context: QueryContext<*>): String {
            return "(${query.compile()})"
        }

    }
}