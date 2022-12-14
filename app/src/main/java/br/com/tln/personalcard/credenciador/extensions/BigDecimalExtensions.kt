package br.com.tln.personalcard.credenciador.extensions

import java.math.BigDecimal

fun BigDecimal?.isValueEqual(other: BigDecimal?): Boolean {
    return if (this == other) {
        true
    } else if (this == null || other == null) {
        when {
            this != null -> this.compareTo(BigDecimal.ZERO) == 0
            other != null -> other.compareTo(BigDecimal.ZERO) == 0
            else -> false
        }
    } else {
        compareTo(other) == 0
    }
}

fun BigDecimal.isPositive() = compareTo(BigDecimal.ZERO) >= 0