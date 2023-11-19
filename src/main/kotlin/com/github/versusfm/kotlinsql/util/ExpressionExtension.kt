package com.github.versusfm.kotlinsql.util

import java.io.Serializable

@FunctionalInterface
fun interface ExpressionExtension<T, R> : Serializable {
    fun T.invoke(): R
}