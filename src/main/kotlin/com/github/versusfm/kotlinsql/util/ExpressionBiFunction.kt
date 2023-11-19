package com.github.versusfm.kotlinsql.util

import java.io.Serializable
import java.util.function.BiFunction
import java.util.function.Function

@FunctionalInterface
fun interface ExpressionBiFunction<T, R, K> : BiFunction<T, R, K>, Serializable {
}