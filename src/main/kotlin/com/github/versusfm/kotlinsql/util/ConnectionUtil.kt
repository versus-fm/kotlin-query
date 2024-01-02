package com.github.versusfm.kotlinsql.util

import com.github.jasync.sql.db.Connection
import com.github.jasync.sql.db.QueryResult
import com.github.jasync.sql.db.SuspendingConnection
import com.github.jasync.sql.db.util.map
import com.github.versusfm.kotlinsql.adapter.ObjectAdapter
import com.github.versusfm.kotlinsql.query.QueryContext
import java.util.concurrent.CompletableFuture
import kotlin.reflect.jvm.ExperimentalReflectionOnLambdas

class ConnectionUtil {
    companion object {
        @OptIn(ExperimentalReflectionOnLambdas::class)
        suspend fun <T> SuspendingConnection.query(supplier: () -> QueryContext<T>): QueryResult {
            val query = supplier.invoke()
            val sql = query.compile();
            val indexedParams = query.getParamValues().entries
                .map { Pair(sql.indexOf(it.key), it.value) }
                .sortedBy { it.first }
            query.getParamValues().forEach {
                sql.replace(":${it.key}", "?")
            }

            return this.sendPreparedStatement(sql, indexedParams.map { it.second }, release = true)
        }

        suspend fun <T, R> SuspendingConnection.queryIntoSequence(
            type: Class<R>,
            supplier: () -> QueryContext<T>
        ): (adapter: ObjectAdapter) -> Sequence<R> {
            return this.query { supplier.invoke() } projectInto type
        }

        infix fun <T> QueryResult.projectInto(type: Class<T>): (adapter: ObjectAdapter) -> Sequence<T> {
            return { adapter ->
                adapter.project(type, this)
            }
        }

        suspend infix fun <T> ((adapter: ObjectAdapter) -> Sequence<T>).using(adapter: ObjectAdapter): Sequence<T> {
            return this(adapter)
        }
    }
}