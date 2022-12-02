package br.com.tln.personalcard.credenciador.webservice.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.math.BigDecimal

@JsonClass(generateAdapter = true)
data class CancelTransactionRequest(
    @Json(name = "idCartao") val cardId: String,
    @Json(name = "nsuHostAutorizacao") val nsuHost: String,
    @Json(name = "nsuAutorizacao") val nsuAuthorization: String,
    @Json(name = "valor") val value: BigDecimal,
    @Json(name = "dataHoraTransacao") val dateTimeTransaction: String,
    @Json(name = "codigoTerminal") val terminalCode: String,
    @Json(name = "codigoCredenciado") val accreditedCode: String,
    @Json(name = "dataTransacaoCancelar") val dateTransactionCancel: String
)