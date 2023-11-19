package com.github.versusfm.kotlinsql.util

import java.lang.reflect.Field

sealed interface ResolvedFunction {
    data class FieldAccess<T>(val type: Class<T>, val field: Field) : ResolvedFunction
}