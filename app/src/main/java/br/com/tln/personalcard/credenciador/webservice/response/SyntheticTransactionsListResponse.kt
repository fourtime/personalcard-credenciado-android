package br.com.tln.personalcard.credenciador.webservice.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.math.BigDecimal

@JsonClass(generateAdapter = true)
data class SyntheticTransactionsListResponse(
    @Json(name = "data") val date: String,
    @Json(name = "valor") val value: BigDecimal,
    @Json(name = "totalTransacoes") val totalTransactions: Int
)