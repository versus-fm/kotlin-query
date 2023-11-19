package com.github.versusfm.kotlinsql.ast

import com.github.versusfm.kotlinsql.query.QueryContext

interface QueryNode {
    fun compile(context: QueryContext<*>): String
}
