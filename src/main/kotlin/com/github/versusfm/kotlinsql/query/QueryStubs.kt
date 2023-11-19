package com.github.versusfm.kotlinsql.query

interface QueryStubs {
    companion object {
        fun <T1: Any> all(col1: T1): String {
            return ""
        }
        fun <T1: Any, T2: Any> all(col1: T1, col2: T2): String {
            return ""
        }
        fun <T1: Any, T2: Any, T3: Any> all(col1: T1, col2: T2, col3: T3): String {
            return ""
        }
        fun <T1: Any, T2: Any, T3: Any, T4: Any> all(col1: T1, col2: T2, col3: T3, col4: T4): String {
            return ""
        }
        fun <T1: Any, T2: Any, T3: Any, T4: Any, T5: Any> all(col1: T1, col2: T2, col3: T3, col4: T4, col5: T5): String {
            return ""
        }
        fun <T1: Any, T2: Any, T3: Any, T4: Any, T5: Any, T6: Any> all(col1: T1, col2: T2, col3: T3, col4: T4, col5: T5, col6: T6): String {
            return ""
        }
        fun <T1: Any, T2: Any, T3: Any, T4: Any, T5: Any, T6: Any, T7: Any> all(col1: T1, col2: T2, col3: T3, col4: T4, col5: T5, col6: T6, col7: T7): String {
            return ""
        }
        fun <T1: Any, T2: Any, T3: Any, T4: Any, T5: Any, T6: Any, T7: Any, T8: Any> all(col1: T1, col2: T2, col3: T3, col4: T4, col5: T5, col6: T6, col7: T7, col8: T8): String {
            return ""
        }
        fun <T1: Any, T2: Any, T3: Any, T4: Any, T5: Any, T6: Any, T7: Any, T8: Any, T9: Any> all(col1: T1, col2: T2, col3: T3, col4: T4, col5: T5, col6: T6, col7: T7, col8: T8, col9: T9): String {
            return ""
        }
        fun <T1: Any, T2: Any, T3: Any, T4: Any, T5: Any, T6: Any, T7: Any, T8: Any, T9: Any, T10: Any> all(col1: T1, col2: T2, col3: T3, col4: T4, col5: T5, col6: T6, col7: T7, col8: T8, col9: T9, col10: T10): String {
            return ""
        }
        operator fun <T, R> QueryContext<R>.contains(element: T): Boolean {
            return true
        }

    }
}