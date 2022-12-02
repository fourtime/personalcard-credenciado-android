package br.com.tln.personalcard.credenciador.webservice.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class QrCodeRequest(
    @Json(name = "dataHoraGeracao") val transactionTime: String,
    @Json(name = "numeroParcelas") val installments: Int,
    @Json(name = "valor") val value: Double
)