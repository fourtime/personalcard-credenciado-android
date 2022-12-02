package br.com.tln.personalcard.credenciador.model

import androidx.annotation.Keep
import org.threeten.bp.LocalDateTime
import java.io.Serializable
import java.math.BigDecimal

@Keep
class Transaction(
    val cardId: String,
    val nsuHost: String,
    val nsuAuthorization: String,
    val installments: Long,
    val value: BigDecimal,
    val cardLastDigits: String,
    val cardType: Int,
    val transactionTimestamp: LocalDateTime,
    var canceling: Boolean = false
) : Serializable