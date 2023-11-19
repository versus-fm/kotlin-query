package com.github.versusfm.kotlinsql.util

import org.apache.bcel.classfile.*
import org.apache.bcel.classfile.Deprecated
import org.apache.bcel.util.ClassPath
import org.apache.bcel.util.SyntheticRepository
import java.lang.reflect.Field
import java.util.concurrent.ConcurrentHashMap

class Functor<T>(private val type: Class<T>) {
    private val javaClass: JavaClass
    companion object {
        private val cachedTypes: ConcurrentHashMap<Class<*>, JavaClass> = ConcurrentHashMap()
        private val repository = SyntheticRepository.getInstance(ClassPath("."))
        fun <T: Any> inspect(expression: T): ResolvedFunction {
            return Functor(expression.javaClass).inspect()
        }
        fun <T: Any> resolveBinaryExpressionLhs(expression: T): Field {
            val functor = Functor(expression.javaClass);
            val invoke = functor.getInvoke()
            TODO()
        }

        fun <T: Any> resolveBinaryExpressionRhs(expression: T): Field {
            TODO()
        }

        fun <T: Any> resolveBinaryExpressionBoth(expression: T): Pair<Field, Field> {
            return Pair(resolveBinaryExpressionLhs(expression), resolveBinaryExpressionRhs(expression))
        }
    }
    init {
        if (cachedTypes.containsKey(type)) {
            javaClass = cachedTypes[type]!!
        } else {
            javaClass = repository.loadClass(type)
            cachedTypes[type] = javaClass
        }
    }

    fun getInvoke(): Method {
        return javaClass.methods.first { it.name.equals("invoke") }!!
    }

    fun inspect(): ResolvedFunction {
        val method = getInvoke()
        val visitor = NodeResolver()
        method.code.accept(visitor)

        TODO()
    }


}