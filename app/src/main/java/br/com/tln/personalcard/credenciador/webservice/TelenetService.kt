package br.com.tln.personalcard.credenciador.webservice

import br.com.tln.personalcard.credenciador.AUTHORIZATION_HEADER
import br.com.tln.personalcard.credenciador.BuildConfig
import br.com.tln.personalcard.credenciador.webservice.request.AnalyticSatatementListRequest
import br.com.tln.personalcard.credenciador.webservice.request.QrCodeRequest
import br.com.tln.personalcard.credenciador.webservice.request.SettingInstanceRequest
import br.com.tln.personalcard.credenciador.webservice.request.StatementListRequest
import br.com.tln.personalcard.credenciador.webservice.response.LoginResponse
import br.com.tln.personalcard.credenciador.webservice.response.OperatorLinks
import br.com.tln.personalcard.credenciador.webservice.response.SyntheticTransactionsResponse
import br.com.tln.personalcard.credenciador.webservice.response.TelenetBaseResponse
import br.com.tln.personalcard.credenciador.webservice.response.TerminalDataResponse
import br.com.tln.personalcard.credenciador.webservice.response.TransactionResponse
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path


interface TelenetService {

    @FormUrlEncoded
    @POST("services/usuario/mobile/inicializaappterminal")
    suspend fun login(
        @Field("applicationKey") applicationKey: String = BuildConfig.USER_APPLICATION_KEY,
        @Field("username") username: String,
        @Field("password") password: String
    ): TelenetBaseResponse<LoginResponse>

    @GET("services/consulta/credenciado/dadosterminal")
    suspend fun getTerminalData(
        @Header(AUTHORIZATION_HEADER) authorization: String
    ): TelenetBaseResponse<TerminalDataResponse>

    @GET("services/consulta/credenciado/cartoesaceitos")
    suspend fun getTerminalAvailablePaymentMethods(
        @Header(AUTHORIZATION_HEADER) authorization: String
    ): TelenetBaseResponse<List<Int>>

    @GET("services/usuario/mobile/linksuteis/{operatorCode}")
    suspend fun getOperatorLinks(
        @Path("operatorCode") operatorCode: String = BuildConfig.OPERATOR_CODE
    ): TelenetBaseResponse<OperatorLinks>

    @POST("services/transacao/cartao/geraqrcodepagamento")
    suspend fun getBillingQrCode(
        @Header(AUTHORIZATION_HEADER) authorization: String,
        @Body request: QrCodeRequest
    ): TelenetBaseResponse<String>

    @POST("services/consulta/credenciado/extratosinteticoterminal")
    suspend fun getSyntheticStatement(
        @Header(AUTHORIZATION_HEADER) authorization: String,
        @Body request: StatementListRequest
    ): TelenetBaseResponse<SyntheticTransactionsResponse>

    @POST("services/consulta/credenciado/extratoanaliticoterminal")
    suspend fun getAnalyticStatement(
        @Header(AUTHORIZATION_HEADER) authorization: String,
        @Body request: AnalyticSatatementListRequest
    ): TelenetBaseResponse<TransactionResponse>

    @POST("services/usuario/mobile/ConfiguraInstanciaApp")
    suspend fun settingAppInstance(
        @Header(AUTHORIZATION_HEADER) authorization: String,
        @Body request: SettingInstanceRequest
    ): TelenetBaseResponse<Any>
}