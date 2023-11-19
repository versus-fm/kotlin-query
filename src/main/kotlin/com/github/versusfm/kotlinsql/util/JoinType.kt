package com.github.versusfm.kotlinsql.util

enum class JoinType(val fragment: String?) {
    Inner("INNER"),
    Left("LEFT"),
    Natural(null)
}