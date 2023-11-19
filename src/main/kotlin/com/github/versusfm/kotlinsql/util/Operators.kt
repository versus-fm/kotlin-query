package com.github.versusfm.kotlinsql.util

enum class Operators(val fragment: String, val operatorType: OperatorType) {
    Equals("=", OperatorType.ValueOp),
    NotEquals("!=", OperatorType.ValueOp),
    In("IN", OperatorType.ValueOp),
    Or("OR", OperatorType.Logical),
    And("AND", OperatorType.Logical);
}