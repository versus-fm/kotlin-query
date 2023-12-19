package com.github.versusfm.kotlinsql.util.func

import java.io.Serializable
import java.util.function.Function

@FunctionalInterface
fun interface ExpressionFunction<T, R> : kotlin.Function<R> {
    fun invoke(arg: T): R
}