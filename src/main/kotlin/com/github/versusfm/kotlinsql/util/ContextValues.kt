package com.github.versusfm.kotlinsql.util

import co.streamx.fluent.extree.expression.ConstantExpression

class ContextValues(val constants: List<ConstantExpression>) {
    companion object {
        val EMPTY = ContextValues(emptyList())
    }
}