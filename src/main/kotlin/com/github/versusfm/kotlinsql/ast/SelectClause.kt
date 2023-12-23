package com.github.versusfm.kotlinsql.ast

import com.github.versusfm.kotlinsql.query.QueryContext

sealed interface SelectClause : QueryNode {
    data class Value(val value: ValueNode) : SelectClause {
        override fun compile(context: QueryContext<*>): String {
            return value.compile(context)
        }
    }
    data class All(val table: String) : SelectClause {
        override fun compile(context: QueryContext<*>): String {
            return "\"$table\".*"
        }
    }
}