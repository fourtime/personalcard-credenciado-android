package br.com.tln.personalcard.credenciador.webservice.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.math.BigDecimal

@JsonClass(generateAdapter = true)
data class TransactionListResponse(
    @Json(name = "dataHoraTransacao") val transactionTimestamp: String,
    @Json(name = "idCartao") val cardId: String,
    @Json(name = "ultimosDigitosCartao") val cardLastDigits: String,
    @Json(name = "valor") val value: BigDecimal,
    @Json(name = "tipoCartao") val cardType: Int,
    @Json(name = "numeroParcelas") val installments: Long,
    @Json(name = "nsuAutorizacao") val nsuAuthorization: String,
    @Json(name = "nsuHost") val nsuHost: String
)