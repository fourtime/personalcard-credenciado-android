package br.com.tln.personalcard.credenciador.webservice.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class StatementListRequest(
    @Json(name = "dataInicio") val from: String,
    @Json(name = "dataFim") val to: String,
    @Json(name = "numeroPagina") val pageNumber: Int,
    @Json(name = "tamanhoPagina") val pageSize: Int
)