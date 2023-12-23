package com.github.versusfm.kotlinsql.query

import co.streamx.fluent.extree.expression.BinaryExpression
import co.streamx.fluent.extree.expression.ConstantExpression
import co.streamx.fluent.extree.expression.Expression
import co.streamx.fluent.extree.expression.ExpressionType
import co.streamx.fluent.extree.expression.InvocationExpression
import co.streamx.fluent.extree.expression.LambdaExpression
import co.streamx.fluent.extree.expression.MemberExpression
import co.streamx.fluent.extree.expression.ParameterExpression
import co.streamx.fluent.extree.expression.UnaryExpression
import com.github.versusfm.kotlinsql.annotation.Column
import com.github.versusfm.kotlinsql.annotation.SqlFunction
import com.github.versusfm.kotlinsql.annotation.Table
import com.github.versusfm.kotlinsql.ast.*
import com.github.versusfm.kotlinsql.util.*
import com.github.versusfm.kotlinsql.util.func.ExpressionBiExtension
import com.github.versusfm.kotlinsql.util.func.ExpressionExtension
import kotlinx.metadata.jvm.KotlinClassMetadata
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.lang.reflect.Parameter
import kotlin.jvm.internal.Intrinsics
import kotlin.reflect.jvm.ExperimentalReflectionOnLambdas
import kotlin.reflect.jvm.reflect

abstract class QueryContext<T> {
    companion object {

        internal fun <K> resolveTableName(type: Class<K>): String {
            if (type.isAnnotationPresent(Table::class.java)) {
                return type.getAnnotation(Table::class.java).value
            }
            return formatSnakeCaseName(type.simpleName)
        }

        internal fun resolveColumnName(field: Field): String {
            if (field.isAnnotationPresent(Column::class.java)) {
                return field.getAnnotation(Column::class.java).value
            }
            return formatSnakeCaseName(field.name)
        }

        internal fun resolveColumnName(parameter: Parameter): String {
            if (parameter.isAnnotationPresent(Column::class.java)) {
                return parameter.getAnnotation(Column::class.java).value
            }
            return formatSnakeCaseName(parameter.name)
        }

        internal fun formatSnakeCaseName(identifier: String): String {
            val stringBuilder = StringBuilder()
            identifier.forEach {
                if (it.isUpperCase()) {
                    stringBuilder.append("_").append(it.lowercaseChar())
                } else {
                    stringBuilder.append(it)
                }
            }
            return stringBuilder.toString()
        }

        // Ugly hack to convince kotlin obviously incorrect code works
        internal fun <T> T?.allowNull(): T = this as T
    }

    @OptIn(ExperimentalReflectionOnLambdas::class)
    fun <R> join(joinType: JoinType, type: Class<R>, innerContext: ExpressionBiExtension<JoinContext<T, R>, R, Unit>): QueryContext<T> {
        val joinContext = JoinContext<T, R>(this, type, joinType)
        val instance: R? = null
        with(innerContext) {
            joinContext.invoke(instance.allowNull())
        }
        return this
    }
    fun <R> innerJoin(type: Class<R>, innerContext: ExpressionBiExtension<JoinContext<T, R>, R, Unit>): QueryContext<T> {
        return join(JoinType.Inner, type, innerContext)
    }

    abstract fun <R> select(selector: ExpressionExtension<T, R>): QueryContext<T>
    abstract fun <R> selectAll(): QueryContext<T>
    abstract fun <R> where(selector: ExpressionExtension<T, R>): QueryContext<T>

    internal abstract fun <R: Any> putParamValue(value: R): String
    internal abstract fun getType(): Class<T>
    internal abstract fun getTargetName(type: Class<*>): String

    internal abstract fun addSelect(selectClause: SelectClause)
    internal abstract fun addWhere(whereClause: WhereClause)
    internal abstract fun addFrom(fromClause: FromClause)
    internal abstract fun setAlias(alias: String)

    internal fun <K, L, R> resolveSelectExpression(context: QueryContext<R>, selector: ExpressionExtension<K, L>): List<SelectClause> {
        val method = selector::class.java.methods.first { it.name == "invoke" }
        val lambda = LambdaExpression.parseMethod(method, selector)
        return resolveSelectExpression(context, lambda).map { SelectClause.Value(it) }
    }

    internal fun <R> resolveSelectExpression(context: QueryContext<R>, expression: LambdaExpression<*>): List<ValueNode> {
        when (val body = expression.body) {
            is InvocationExpression -> return resolveColumnSelector(context, body)
            else -> TODO()
        }
        TODO()
    }

    internal fun <K, L, R> resolveWhereExpression(context: QueryContext<R>, selector: ExpressionExtension<K, L>): WhereClause {
        val method = selector::class.java.methods.first { it.name == "invoke" }
        val lambda = LambdaExpression.parseMethod(method, selector)
        return WhereClause.Condition(resolveWhereExpression(context, lambda))
    }

    internal fun <R> resolveWhereExpression(context: QueryContext<R>, expression: LambdaExpression<*>): ConditionClause {
        when (val body = expression.body) {
            is InvocationExpression -> when (val target = body.target) {
                is MemberExpression -> when (val member = target.member) {
                    is Method -> {
                        val instance = target.instance
                        val innerMethod = LambdaExpression.parseMethod(member, instance)
                        return resolveInnerWhereExpression(context, innerMethod.body, ContextValues(body.arguments.filterIsInstance(ConstantExpression::class.java)))
                    }
                }
                is LambdaExpression<*> ->  {
                    return resolveInnerWhereExpression(context, target.body, ContextValues(body.arguments.filterIsInstance(ConstantExpression::class.java)))
                }
            }
            is UnaryExpression -> {
                if (body.expressionType == ExpressionType.Convert) {
                    return resolveInnerWhereExpression(context, body.first)
                }
                TODO()
            }
            else -> TODO()
        }
        TODO()
    }

    internal fun <R> resolveInnerWhereExpression(context: QueryContext<R>, expression: Expression, contextValues: ContextValues = ContextValues.EMPTY): ConditionClause {
        when (expression) {
            is InvocationExpression -> when (val target = expression.target) {
                is MemberExpression -> when (val member = target.member) {
                    is Method -> {
                        if (member.declaringClass == Intrinsics::class.java) {
                            return resolveIntrinsic(context, expression, member, contextValues) as ConditionClause.ConditionNode
                        } else if (member.declaringClass == QueryStubs.Companion::class.java) {
                            return resolveConditionStubMethods(context, expression, member)
                        } else {
                            val instance = target.instance
                            val innerMethod = LambdaExpression.parseMethod(member, instance)
                            return resolveInnerWhereExpression<R>(context, innerMethod.body, contextValues)
                        }
                    }
                }
            }
            is BinaryExpression -> return resolveBinaryExpression(context, expression, contextValues)
            is UnaryExpression -> {
                if (expression.expressionType == ExpressionType.Convert) {
                    return resolveInnerWhereExpression(context, expression.first, contextValues)
                }
                TODO()
            }
        }
        TODO()
    }

    private fun <R> resolveIntrinsic(
        context: QueryContext<R>,
        invocationExpression: InvocationExpression,
        method: Method,
        contextValues: ContextValues = ContextValues.EMPTY
    ): ValueNode {
        if (method.name == "areEqual") {
            return ConditionClause.ConditionNode(
                resolveValueExpression(context, invocationExpression.arguments[0]),
                resolveValueExpression(context, invocationExpression.arguments[1]),
                Operators.Equals
            )
        } else {
            TODO()
        }
    }

    private fun <R> resolveBinaryExpression(context: QueryContext<R>, expression: BinaryExpression, contextValues: ContextValues = ContextValues.EMPTY): ConditionClause {
        val lhs: ValueNode = resolveValueExpression(context, expression.first, contextValues)
        val rhs: ValueNode = resolveValueExpression(context, expression.second, contextValues)
        val operator = resolveOperator(expression.expressionType)
        return when (operator.operatorType) {
            OperatorType.Logical -> ConditionClause.ConditionGroup(lhs, rhs, operator)
            OperatorType.ValueOp -> ConditionClause.ConditionNode(lhs, rhs, operator)
        }
    }

    private fun <R> resolveMethodBinaryExpression(
        context: QueryContext<R>,
        invocationExpression: InvocationExpression,
        method: Method,
        contextValues: ContextValues = ContextValues.EMPTY
    ): ConditionClause {
        TODO()
    }

    private fun <R> resolveValueExpression(context: QueryContext<R>, expression: Expression, contextValues: ContextValues = ContextValues.EMPTY): ValueNode {
       return when (expression) {
            is ParameterExpression -> ValueNode.NamedParam(contextValues.constants.get(expression.index))
            is ConstantExpression -> ValueNode.NamedParam(expression.value)
            is InvocationExpression -> when (val target = expression.target) {
                is MemberExpression -> when (val member = target.member) {
                    is Method -> {
                        if (expression.resultType == QueryContext::class.java) {
                            val arr: Array<Any> = Array(expression.arguments.size) {
                                getInstance(
                                    context,
                                    expression.arguments[it]
                                )
                            }
                            val instance = getInstance(context, target)
                            val subQuery = member.invoke(instance, *arr) as SelectContext<*>
                            subQuery.parent = context
                            return ValueNode.SubQuery(subQuery)
                        }
                        else {
                            resolveColumnSelector(context, expression).first()
                        }
                    }
                    else -> resolveColumnSelector(context, expression).first()
                }
                else -> resolveColumnSelector(context, expression).first()
            }
            is BinaryExpression -> ValueNode.BooleanCondition(resolveBinaryExpression(context, expression, contextValues))
            is UnaryExpression -> resolveColumnSelector(context, expression).first()
            is MemberExpression -> ValueNode.NamedParam(getInstance(context, expression))
            else -> TODO()
        }
    }

    private fun <R> getInstance(context: QueryContext<R>, expression: Expression): Any {
        when (expression) {
            is MemberExpression -> {
                when (val instanceExpression = expression.instance) {
                    is MemberExpression -> when (val member = instanceExpression.member) {
                        is Field -> {
                            member.isAccessible = true
                            return member.get(instanceExpression.instance)
                        }
                    }
                }
                when (val member = expression.member) {
                    is Field -> {
                        member.isAccessible = true
                        if (expression.instance == null) {
                            return member.get(null)
                        } else {
                            return member.get(getInstance(context, expression.instance))
                        }
                    }
                }
            }
            is ConstantExpression -> return expression.value
        }
        TODO()
    }

    private fun resolveOperator(expressionType: Int): Operators {
        return when (expressionType) {
            ExpressionType.Equal -> Operators.Equals
            ExpressionType.NotEqual -> Operators.NotEquals
            ExpressionType.LogicalOr -> Operators.Or
            ExpressionType.LogicalAnd -> Operators.And
            else -> TODO()
        }
    }

    private fun <R> resolveColumnSelector(context: QueryContext<R>, invocationExpression: InvocationExpression): List<ValueNode> {
        when (val target = invocationExpression.target) {
            is MemberExpression -> when (val member = target.member) {
                is Method -> {
                    if (member.declaringClass == QueryStubs.Companion::class.java) {
                        return resolveStubMethods(context, invocationExpression, member)
                    } else if (member.declaringClass == Intrinsics::class.java) {
                        return listOf(resolveIntrinsic(context, invocationExpression, member))
                    } else if (member.isAnnotationPresent(SqlFunction::class.java)) {
                        return resolveFunctionCall(context, invocationExpression, member)
                    } else {
                        val instance = target.instance
                        val innerMethod = LambdaExpression.parseMethod(member, instance)
                        return resolveColumnSelector(context, innerMethod)
                    }
                }
                is Field -> {
                    val tableName = context.getTargetName(member.declaringClass)
                    val columnName = resolveColumnName(member)
                    return listOf(ValueNode.TableColumn(tableName, columnName))
                }
            }
            is LambdaExpression<*> -> return resolveSelectExpression(context, target)
        }
        TODO()
    }

    private fun <R> resolveColumnSelector(context: QueryContext<R>, memberExpression: MemberExpression): List<ValueNode> {
        when (val member = memberExpression.member) {
            is Field -> {
                val tableName = context.getTargetName(member.declaringClass)
                val columnName = resolveColumnName(member)
                return listOf(ValueNode.TableColumn(tableName, columnName))
            }
        }
        TODO()
    }

    private fun <R> resolveColumnSelector(context: QueryContext<R>, unaryExpression: UnaryExpression): List<ValueNode> {
        when (val first = unaryExpression.first) {
            is InvocationExpression -> return resolveColumnSelector(context, first)
            else -> TODO()
        }
    }

    private fun <T, R> resolveColumnSelector(context: QueryContext<R>, expression: LambdaExpression<T>): List<ValueNode> {
        when (val body = expression.body) {
            is InvocationExpression -> return resolveColumnSelector(context, body)
            is MemberExpression -> return resolveColumnSelector(context, body)
            is UnaryExpression -> {
                if (body.expressionType == ExpressionType.Convert) {
                    return when (val innerBody = body.first) {
                        is InvocationExpression -> resolveColumnSelector(context, innerBody)
                        is MemberExpression -> resolveColumnSelector(context, innerBody)
                        else -> TODO()
                    }
                }
                TODO()
            }
        }
        TODO()
    }

    private fun <R> resolveFunctionCall(
        context: QueryContext<R>,
        invocationExpression: InvocationExpression,
        method: Method
    ): List<ValueNode> {
        val functionAnnotation = method.getAnnotation(SqlFunction::class.java)
        val functionName = if (functionAnnotation.name == "[NULL]") {
            formatSnakeCaseName(method.name)
        } else {
            functionAnnotation.name
        }
        return listOf(ValueNode.FunctionCall(functionName, invocationExpression.arguments.flatMap {
            when (it) {
                is UnaryExpression -> resolveColumnSelector(context, it)
                is InvocationExpression -> resolveColumnSelector(context, it)
                is ConstantExpression -> listOf(resolveValueExpression(context, it))
                else -> TODO()
            }
        }))
    }

    private fun <R> resolveStubMethods(
        context: QueryContext<R>,
        invocationExpression: InvocationExpression,
        method: Method
    ): List<ValueNode> {
        when (method.name) {
            "all" -> {
                return invocationExpression.arguments.flatMap {
                    when (it) {
                        is UnaryExpression -> resolveColumnSelector(context, it)
                        is InvocationExpression -> resolveColumnSelector(context, it)
                        else -> TODO()
                    }
                }
            }
            else -> TODO()
        }
    }

    private fun <R> resolveConditionStubMethods(
        context: QueryContext<R>,
        invocationExpression: InvocationExpression,
        method: Method
    ): ConditionClause {
        when (method.name) {
            "contains" -> {
                val lhs: ValueNode = resolveValueExpression(context, invocationExpression.arguments[0])
                val rhs: ValueNode = resolveValueExpression(context, invocationExpression.arguments[1])
                val operator = Operators.In
                return ConditionClause.ConditionNode(rhs, lhs, operator)
            }
            else -> TODO()
        }
        TODO()
    }


    internal abstract fun compile(): String


}