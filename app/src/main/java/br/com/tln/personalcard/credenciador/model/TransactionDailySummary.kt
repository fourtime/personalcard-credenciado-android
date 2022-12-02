package br.com.tln.personalcard.credenciador.model

import androidx.annotation.Keep
import org.threeten.bp.LocalDateTime
import java.io.Serializable
import java.math.BigDecimal

@Keep
class TransactionDailySummary(
    val date: LocalDateTime,
    val totalValue: BigDecimal,
    val transactions: Int
) : Serializable