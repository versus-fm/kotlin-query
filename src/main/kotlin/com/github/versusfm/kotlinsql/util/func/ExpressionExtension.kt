package com.github.versusfm.kotlinsql.util.func

import java.io.Serializable

@FunctionalInterface
fun interface ExpressionExtension<T, R> : Function<R> {
    fun T.invoke(): R
}