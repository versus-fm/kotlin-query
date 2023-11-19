package com.github.versusfm.kotlinsql.query

class SubSelectContext<T, TParent>(parent: QueryContext<TParent>, type: Class<T>, tableName: String) : SelectContext<T>(type, tableName) {
    init {
        this.parent = parent
    }
    override fun <R: Any> putParamValue(value: R): String {
        return parent!!.putParamValue(value)
    }

    override fun getTargetName(type: Class<*>): String {
        if (type == this.type) {
            return tableName
        }
        return parent!!.getTargetName(type)
    }
}