package br.com.tln.personalcard.credenciador.webservice.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class PayWithCardQRCodeRequest(
    @Json(name = "tokenPagamento") val paymentToken: String,
    @Json(name = "tipoCartao") val cardType: Int,
    @Json(name = "tokenCartao") val cardToken: String,
    @Json(name = "senhaCartao") val cardPassword: String
)