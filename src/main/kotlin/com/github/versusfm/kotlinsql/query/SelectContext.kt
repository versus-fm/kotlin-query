package com.github.versusfm.kotlinsql.query

import com.github.versusfm.kotlinsql.ast.*
import com.github.versusfm.kotlinsql.util.func.ExpressionExtension
import java.util.concurrent.ConcurrentHashMap
import java.util.stream.Collectors

open class SelectContext<T>(protected val type: Class<T>, protected var tableName: String) : QueryContext<T>() {
    private val select: SelectNode = SelectNode()
    private var paramIndex: Int = 0
    private var paramValues = ConcurrentHashMap<Int, Pair<String, Any>>()
    internal var parent: QueryContext<*>? = null

    companion object {
        fun <T> from(type: Class<T>, expression: QueryContext<T>.() -> Unit): QueryContext<T> {
            val tableName = resolveTableName(type)
            val context = SelectContext(type, tableName)
            context.addFrom(FromClause.FromTable(tableName))
            expression(context)
            return context
        }
    }

    override fun <R> select(selector: ExpressionExtension<T, R>): QueryContext<T> {
        val selectClause = resolveSelectExpression(this, selector)
        select.selectClauses.addAll(selectClause)
        return this
    }

    override fun <R> where(selector: ExpressionExtension<T, R>): QueryContext<T> {
        val whereClause = resolveWhereExpression(this, selector)
        select.whereClauses.add(whereClause)
        return this
    }

    override fun selectAll(): QueryContext<T> {
        select.selectClauses.add(SelectClause.All(tableName))
        return this
    }


    override fun <R : Any> putParamValue(value: R): String {
        if (parent != null) {
            return parent!!.putParamValue(value)
        }
        if (paramValues.containsKey(value.hashCode())) {
            return paramValues[value.hashCode()]!!.first
        }
        paramValues[value.hashCode()] = Pair("param${paramIndex++}", value)
        return paramValues[value.hashCode()]!!.first
    }


    override fun getType(): Class<T> {
        return type
    }

    override fun getTargetName(type: Class<*>): String {
        return tableName
    }

    override fun addSelect(selectClause: SelectClause) {
        select.selectClauses.add(selectClause)
    }

    override fun addWhere(whereClause: WhereClause) {
        select.whereClauses.add(whereClause)
    }

    override fun addFrom(fromClause: FromClause) {
        select.fromClauses.add(fromClause)
    }

    override fun setAlias(alias: String) {
        this.tableName = alias
    }

    override fun getParamValues(): Map<String, Any> {
        return paramValues.values
            .stream()
            .collect(Collectors.toMap({ it.first }) { it.second })
    }

    override fun compile(): String {
        return select.compile(this)
    }


}