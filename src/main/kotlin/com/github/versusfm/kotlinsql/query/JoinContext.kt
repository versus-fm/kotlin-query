package com.github.versusfm.kotlinsql.query

import co.streamx.fluent.extree.expression.LambdaExpression
import com.github.versusfm.kotlinsql.ast.FromClause
import com.github.versusfm.kotlinsql.ast.SelectClause
import com.github.versusfm.kotlinsql.ast.WhereClause
import com.github.versusfm.kotlinsql.util.func.ExpressionBiFunction
import com.github.versusfm.kotlinsql.util.func.ExpressionExtension
import com.github.versusfm.kotlinsql.util.JoinType

class JoinContext<Parent, T>(val parent: QueryContext<Parent>, private val type: Class<T>, val joinType: JoinType) : QueryContext<T>() {

    private var targetName = resolveTableName(type)
    private val join = FromClause.JoinTable(targetName, joinType)
    init {
        parent.addFrom(join)
    }


    fun on(selector: ExpressionBiFunction<Parent, T, Boolean>): JoinContext<Parent, T> {
        val method = selector::class.java.methods.first { it.name == "invoke" }
        val lambda = LambdaExpression.parseMethod(method, selector)
        val conditionClause = resolveWhereExpression(this, lambda)
        join.condition = conditionClause
        return this
    }

    override fun <R> select(selector: ExpressionExtension<T, R>): QueryContext<T> {
        val method = selector::class.java.methods.first { it.name == "invoke" }
        val lambda = LambdaExpression.parseMethod(method, selector)
        val target = resolveSelectExpression(this, lambda)
        target.forEach { parent.addSelect(SelectClause.Value(it)) }
        return this
    }

    override fun <R> where(selector: ExpressionExtension<T, R>): QueryContext<T> {
        val method = selector::class.java.methods.first { it.name == "invoke" }
        val lambda = LambdaExpression.parseMethod(method, selector)
        val condition = resolveWhereExpression(this, lambda)
        parent.addWhere(WhereClause.Condition(condition))
        return this
    }

    override fun <R> selectAll(): QueryContext<T> {
        parent.addSelect(SelectClause.All(this.targetName))
        return this;
    }

    override fun <R: Any> putParamValue(value: R): String {
        return parent.putParamValue(value)
    }

    override fun getType(): Class<T> {
        return type
    }

    override fun getTargetName(type: Class<*>): String {
        if (type == this.type) {
            return targetName
        }
        return parent.getTargetName(type)
    }

    override fun addSelect(selectClause: SelectClause) {
        parent.addSelect(selectClause)
    }

    override fun addWhere(whereClause: WhereClause) {
        parent.addWhere(whereClause)
    }

    override fun addFrom(fromClause: FromClause) {
        parent.addFrom(fromClause)
    }

    override fun setAlias(alias: String) {
        this.targetName = alias
    }

    override fun compile(): String {
        return join.compile(this)
    }
}