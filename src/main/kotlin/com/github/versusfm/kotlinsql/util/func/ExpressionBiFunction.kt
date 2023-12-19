package com.github.versusfm.kotlinsql.util.func

import java.io.Serializable
import java.util.function.BiFunction

@FunctionalInterface
fun interface ExpressionBiFunction<T, R, K> : Function<K> {
    fun invoke(arg1: T, arg2: R): K
}