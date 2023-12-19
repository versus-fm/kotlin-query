package com.github.versusfm.kotlinsql.util.func

@FunctionalInterface
interface ExpressionTriExtension<T1, T2, T3, R> : Function<R> {
    fun T1.invoke(arg1: T2, arg2: T3): R
}