package com.github.versusfm.kotlinsql.util

import java.io.Serializable
import java.util.function.Function

@FunctionalInterface
fun interface ExpressionFunction<T, R> : Function<T, R>, Serializable {
}