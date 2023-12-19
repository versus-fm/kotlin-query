package com.github.versusfm.kotlinsql.util.func

import java.io.Serializable

@FunctionalInterface
fun interface ExpressionBiExtension<T1, T2, R> : Function<R> {
    fun T1.invoke(arg1: T2): R
}
