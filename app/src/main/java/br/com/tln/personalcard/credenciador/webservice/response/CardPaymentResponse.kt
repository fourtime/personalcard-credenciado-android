package br.com.tln.personalcard.credenciador.webservice.response

import com.google.type.DateTime
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.io.Serializable
import java.math.BigDecimal

@JsonClass(generateAdapter = true)
data class CardPaymentResponse(
        @Json(name = "nsuHost") val nsuHost: String,
        @Json(name = "nsuAutorizacao") val nsuAuthorization: String,
        @Json(name = "nomePortador") val bearerName: String,
        @Json(name = "saldoAposTransacao") val totalValue: BigDecimal,
        @Json(name = "dataHoraTransacao") val transactionDate: String?,
        @Json(name = "dataHoraAutorizacao") val authorizationDate: String?
)