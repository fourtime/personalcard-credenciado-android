package br.com.tln.personalcard.credenciador.webservice.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class OperatorLinks(
    @Json(name = "avisosOperadora") val noticeUrl: String,
    @Json(name = "contatoOperadora") val contactUrl: String
)