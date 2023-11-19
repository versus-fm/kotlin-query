package com.github.versusfm.kotlinsql.ast

import com.github.versusfm.kotlinsql.query.QueryContext
import com.github.versusfm.kotlinsql.util.Operators

sealed interface ConditionClause : QueryNode {
    data class ConditionNode(var lhs: ValueNode, var rhs: ValueNode, private val op: Operators) : ConditionClause, ValueNode {
        override fun compile(context: QueryContext<*>): String {
            return "${lhs.compile(context)} ${op.fragment} ${rhs.compile(context)}"
        }
    }
    data class ConditionGroup(var lhs: ValueNode, var rhs: ValueNode, private val op: Operators) : ConditionClause {
        override fun compile(context: QueryContext<*>): String {
            return "(${lhs.compile(context)} ${op.fragment} ${rhs.compile(context)})"
        }
    }
}