package br.com.tln.personalcard.credenciador.webservice

import br.com.tln.personalcard.credenciador.AUTHORIZATION_HEADER
import br.com.tln.personalcard.credenciador.webservice.request.CancelTransactionRequest
import br.com.tln.personalcard.credenciador.webservice.response.TelenetBaseResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface TransactionService {

    @POST("services/transacao/cartao/cancelatransacao")
    suspend fun cancelTransaction(
        @Header(AUTHORIZATION_HEADER) authorization: String,
        @Body request: CancelTransactionRequest
    ): TelenetBaseResponse<Any>
}