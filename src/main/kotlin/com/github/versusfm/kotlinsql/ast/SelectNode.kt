package com.github.versusfm.kotlinsql.ast

import com.github.versusfm.kotlinsql.query.QueryContext

class SelectNode : QueryNode {
    var distinctClause: DistinctClause? = null
    var selectClauses: MutableList<SelectClause> = ArrayList()
    var fromClauses: MutableList<FromClause> = ArrayList()
    var whereClauses: MutableList<WhereClause> = ArrayList()

    override fun compile(context: QueryContext<*>): String {
        val stringBuilder = StringBuilder()
        stringBuilder.append("SELECT ")
            .append(selectClauses.joinToString(", ") { it.compile(context) })
            .append(fromClauses.joinToString(" ") { " ${it.compile(context)}" })
            .run {
                if (whereClauses.isNotEmpty()) {
                    append(" WHERE ")
                    append(whereClauses.joinToString(" AND ") { it.compile(context) })
                }
            }
        return stringBuilder.toString()
    }
}