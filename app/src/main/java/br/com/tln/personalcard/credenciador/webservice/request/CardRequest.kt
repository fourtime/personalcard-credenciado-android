package br.com.tln.personalcard.credenciador.webservice.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class PayWithCardRequest(
        @Json(name = "token") val paymentToken: String,
        @Json(name = "cartao") val carddata: String
)