package com.lifeof843.windows10calculator

import java.math.BigDecimal

enum class Operator(
    val operation: (BigDecimal, BigDecimal) -> BigDecimal
) {
    ADD     ( { A, B -> A + B } ),
    SUBTRACT( { A, B -> A - B } ),
    MULTIPLY( { A, B -> A * B } ),
    DIVIDE  ( { A, B -> A.divide(B, 8, java.math.RoundingMode.HALF_EVEN) } )
}