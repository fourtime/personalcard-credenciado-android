package br.com.tln.personalcard.credenciador.model

import androidx.annotation.Keep
import com.google.type.DateTime
import org.threeten.bp.LocalDateTime
import java.io.Serializable
import java.math.BigDecimal

@Keep
class CardPayment(
        val nsuHost: String,
        val nsuAuthorization: String,
        val bearerName: String,
        val totalValue: BigDecimal,
        val transactionDate: LocalDateTime
) : Serializable