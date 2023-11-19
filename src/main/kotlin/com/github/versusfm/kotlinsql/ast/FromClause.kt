package com.github.versusfm.kotlinsql.ast

import com.github.versusfm.kotlinsql.query.QueryContext
import com.github.versusfm.kotlinsql.util.JoinType
import java.lang.RuntimeException

sealed interface FromClause : QueryNode {
    data class FromTable(val tableName: String) : FromClause {
        override fun compile(context: QueryContext<*>): String {
            return "FROM \"${tableName}\""
        }
    }
    data class JoinTable(val tableName: String, val joinType: JoinType) : FromClause {
        internal var condition: ConditionClause? = null
        override fun compile(context: QueryContext<*>): String {
            return when (condition) {
                null -> {
                    throw RuntimeException("Condition cannot be empty")
                }
                else -> if (joinType.fragment == null) {
                    "JOIN \"${tableName}\" ON ${condition?.compile(context)}"
                } else {
                    "${joinType.fragment} JOIN \"${tableName}\" ON ${condition?.compile(context)}"
                }
            }
        }

    }
}