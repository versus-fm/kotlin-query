package com.github.versusfm.kotlinsql.adapter.types

import com.github.jasync.sql.db.RowData
import com.github.versusfm.kotlinsql.adapter.TypeAdapter
import java.math.BigDecimal

class BigDecimalAdapter : TypeAdapter<BigDecimal> {
    override fun type(): Class<BigDecimal> {
        return BigDecimal::class.java
    }

    override fun convert(columnName: String, rowData: RowData): BigDecimal? {
        return rowData.getAs(columnName)
    }
}