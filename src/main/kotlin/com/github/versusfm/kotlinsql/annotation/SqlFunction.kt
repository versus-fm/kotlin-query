package com.github.versusfm.kotlinsql.annotation

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class SqlFunction(val name: String = "[NULL]")
