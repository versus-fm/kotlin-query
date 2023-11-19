package com.github.versusfm.kotlinsql.ast

import com.github.versusfm.kotlinsql.query.QueryContext

sealed interface DistinctClause : QueryNode {
    data object Distinct : DistinctClause {
        override fun compile(context: QueryContext<*>): String {
            return "DISTINCT"
        }
    }
    data class DistinctOn(val nodes: List<QueryNode>) : DistinctClause {
        override fun compile(context: QueryContext<*>): String {
            return "DISTINCT ON (${nodes.joinToString(", ")})"
        }
    }
}