package com.github.versusfm.kotlinsql.ast

import com.github.versusfm.kotlinsql.query.QueryContext

sealed interface WhereClause : QueryNode {
    data class Condition(val conditionClause: ConditionClause) : WhereClause {
        override fun compile(context: QueryContext<*>): String {
            return conditionClause.compile(context)
        }
    }
}