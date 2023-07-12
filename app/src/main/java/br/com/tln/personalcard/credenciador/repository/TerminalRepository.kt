package br.com.tln.personalcard.credenciador.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import arrow.core.Either
import br.com.tln.personalcard.credenciador.R
import br.com.tln.personalcard.credenciador.core.Resource
import br.com.tln.personalcard.credenciador.entity.AccessToken
import br.com.tln.personalcard.credenciador.exception.ConnectionErrorException
import br.com.tln.personalcard.credenciador.exception.InvalidAuthenticationException
import br.com.tln.personalcard.credenciador.extensions.CryptographyUtils.decrypt
import br.com.tln.personalcard.credenciador.extensions.format
import br.com.tln.personalcard.credenciador.extensions.responseError
import br.com.tln.personalcard.credenciador.extensions.toLocalDateTime
import br.com.tln.personalcard.credenciador.model.*
import br.com.tln.personalcard.credenciador.provider.ResourceProvider
import br.com.tln.personalcard.credenciador.webservice.TransactionService
import br.com.tln.personalcard.credenciador.webservice.TelenetService
import br.com.tln.personalcard.credenciador.webservice.request.*
import br.com.tln.personalcard.credenciador.webservice.response.ErrorBodyResponse
import com.squareup.moshi.Moshi
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDateTime
import retrofit2.HttpException
import java.io.EOFException
import java.io.IOException
import java.util.concurrent.TimeoutException
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.coroutineContext

@Singleton
class TerminalRepository @Inject constructor(
    private val telenetService: TelenetService,
    private val resourceProvider: ResourceProvider,
    private val transactionService: TransactionService,
    private val moshi: Moshi
) {
    suspend fun payWithCard(accessToken: AccessToken, paymentToken: String, carddata: String): Either<Throwable, Resource<CardPayment>> {
        val request = PayWithCardRequest(paymentToken, carddata)
        return payWithCard(accessToken.formattedToken, request)
    }

    suspend fun payWithCardQRCode(accessToken: AccessToken, paymentToken: String, cardType: Int, cardQRCode: String, cardPassword: String): Either<Throwable, Resource<CardPayment>> {
        val request = PayWithCardQRCodeRequest(paymentToken, cardType, cardQRCode, cardPassword)
        return payWithCardQRCode(accessToken.formattedToken, request)
    }

    suspend fun getBillingQrCodeLiveData(accessToken: AccessToken, installments: Int, billingValue: Double): LiveData<Either<Throwable, Resource<QrCodePayment>>> {
        val liveData: MutableLiveData<Either<Throwable, Resource<QrCodePayment>>> = MutableLiveData()
        liveData.postValue(Either.right(Resource.loading()))

        CoroutineScope(context = coroutineContext).launch {
            val request = QrCodeRequest(LocalDateTime.now().format(), installments, billingValue)
            val result: Either<Throwable, Resource<QrCodePayment>> = getBillingQrCode(accessToken.formattedToken, request)
            liveData.postValue(result)
        }

        return liveData
    }

    suspend fun getTransactionsLiveData(accessToken: AccessToken, request: StatementListRequest): Either<Throwable, Resource<TransactionPeriodSummary>> {
        return getTransactions(accessToken.formattedToken, request)
    }

    suspend fun getDayTransactionsLiveData(accessToken: AccessToken, request: AnalyticSatatementListRequest): Either<Throwable, Resource<AnalyticSummary>> {
        return getDayTransactions(accessToken.formattedToken, request)
    }

    suspend fun getCancelTransactionLiveData(accessToken: AccessToken, account: Account, transaction: Transaction): LiveData<Either<Throwable, Resource<String>>> {
        val liveData: MutableLiveData<Either<Throwable, Resource<String>>> = MutableLiveData()
        liveData.postValue(Either.right(Resource.loading()))

        CoroutineScope(context = coroutineContext).launch {
            val request = CancelTransactionRequest(
                    cardId = transaction.cardId,
                    nsuHost = transaction.nsuHost,
                    nsuAuthorization = transaction.nsuAuthorization,
                    value = transaction.value,
                    dateTimeTransaction = LocalDateTime.now().format(),
                    terminalCode = account.password.decrypt(),
                    accreditedCode = account.terminal.accreditedCode,
                    dateTransactionCancel = transaction.transactionTimestamp.format()
            )

            val result: Either<Throwable, Resource<String>> = cancelTransaction(accessToken.formattedToken, request)
            liveData.postValue(result)
        }

        return liveData
    }

    private suspend fun payWithCard(authorization: String, request: PayWithCardRequest): Either<Throwable, Resource<CardPayment>> {
        return try {
            val response = telenetService.payWithCard(authorization = authorization, request = request)
            val transactionDate = response.results.authorizationDate?.toLocalDateTime() ?: LocalDateTime.now()

            Either.right(
                Resource.success(CardPayment(nsuHost = response.results.nsuHost, nsuAuthorization = response.results.nsuAuthorization, bearerName = response.results.bearerName, totalValue = response.results.totalValue, transactionDate = transactionDate))
            )
        } catch (ex: CancellationException) {
            throw ex
        } catch (ex: HttpException) {
            val errorBodyResponse: ErrorBodyResponse? = moshi.responseError(ex.response())

            if (ex.code() == 401) {
                Either.left(InvalidAuthenticationException())
            } else {
                Either.right(Resource.error(message = errorBodyResponse?.message))
            }
        } catch (ex: EOFException) {
            Either.left(ex)
        } catch (ex: IOException) {
            Either.left(ConnectionErrorException())
        } catch (t: Throwable) {
            Either.left(t)
        }
    }

    private suspend fun payWithCardQRCode(authorization: String, request: PayWithCardQRCodeRequest): Either<Throwable, Resource<CardPayment>> {
        return try {
            var response = telenetService.payWithCardQRCode(authorization = authorization, request = request)
            val transactionDate = response.results.authorizationDate?.toLocalDateTime() ?: LocalDateTime.now()

            Either.right(
                Resource.success(CardPayment(nsuHost = response.results.nsuHost, nsuAuthorization = response.results.nsuAuthorization, bearerName = response.results.bearerName, totalValue = response.results.totalValue, transactionDate = transactionDate))
            )
        } catch (ex: CancellationException) {
            throw ex
        } catch (ex: HttpException) {
            val errorBodyResponse: ErrorBodyResponse? = moshi.responseError(ex.response())

            if (ex.code() == 401) {
                Either.left(InvalidAuthenticationException())
            } else {
                Either.right(Resource.error(message = errorBodyResponse?.message))
            }
        } catch (ex: EOFException) {
            Either.left(ex)
        } catch (ex: IOException) {
            Either.left(ConnectionErrorException())
        } catch (t: Throwable) {
            Either.left(t)
        }
    }

    private suspend fun getBillingQrCode(
            authorization: String,
            request: QrCodeRequest
    ): Either<Throwable, Resource<QrCodePayment>> {
        return try {
            val response = telenetService.getBillingQrCode(authorization = authorization, request = request)
            val data = response.results

            Either.right(
                    Resource.success(
                            QrCodePayment(data.paymentToken, qrCode = data.qrCode)
                    )
            )
        } catch (ex: CancellationException) {
            throw ex
        } catch (ex: HttpException) {
            if (ex.code() == 401) {
                Either.left(InvalidAuthenticationException())
            } else {
                Either.right(Resource.error())
            }
        } catch (ex: EOFException) {
            Either.left(ex)
        } catch (ex: IOException) {
            Either.left(ConnectionErrorException())
        } catch (t: Throwable) {
            Either.left(t)
        }
    }

    private suspend fun getTransactions(
            authorization: String,
            request: StatementListRequest
    ): Either<Throwable, Resource<TransactionPeriodSummary>> {
        return try {
            val response = telenetService.getSyntheticStatement(
                    authorization = authorization,
                    request = request
            )

            val transactionDailySummaryList = response.results.transactions.map { result ->
                TransactionDailySummary(
                        date = result.date.toLocalDateTime(),
                        totalValue = result.value,
                        transactions = result.totalTransactions
                )
            }

            Either.right(
                    Resource.success(
                            TransactionPeriodSummary(
                                    period = "Últimos 30 dias",
                                    totalValue = response.results.totalTicketValue,
                                    averageTicketValue = response.results.averageTicketValue,
                                    dailySummaryList = transactionDailySummaryList
                            )
                    )
            )
        } catch (ex: CancellationException) {
            throw ex
        } catch (ex: HttpException) {
            if (ex.code() == 401) {
                Either.left(InvalidAuthenticationException())
            } else {
                Either.right(Resource.error())
            }
        } catch (ex: EOFException) {
            Either.left(ex)
        } catch (ex: IOException) {
            Either.left(ConnectionErrorException())
        } catch (t: Throwable) {
            Either.left(t)
        }
    }

    private suspend fun getDayTransactions(
            authorization: String,
            request: AnalyticSatatementListRequest
    ): Either<Throwable, Resource<AnalyticSummary>> {
        return try {
            val response = telenetService.getAnalyticStatement(
                    authorization = authorization,
                    request = request
            )

            val transactions = response.results.transactions.map { result ->
                Transaction(
                        cardId = result.cardId,
                        nsuHost = result.nsuHost,
                        nsuAuthorization = result.nsuAuthorization,
                        installments = result.installments,
                        value = result.value,
                        cardLastDigits = result.cardLastDigits,
                        cardType = result.cardType,
                        transactionTimestamp = result.transactionTimestamp.toLocalDateTime()
                )
            }

            Either.right(
                    Resource.success(
                            AnalyticSummary(
                                    pageNumber = response.results.pageNumber,
                                    pageSize = response.results.pageSize,
                                    totalRecords = response.results.totalRecords,
                                    transactions = transactions,
                                    totalTicketValue = response.results.totalTicketValue,
                                    averageTicketValue = response.results.averageTicketValue
                            )
                    )
            )
        } catch (ex: CancellationException) {
            throw ex
        } catch (ex: HttpException) {
            if (ex.code() == 401) {
                Either.left(InvalidAuthenticationException())
            } else {
                Either.right(Resource.error())
            }
        } catch (ex: EOFException) {
            Either.left(ex)
        } catch (ex: IOException) {
            Either.left(ConnectionErrorException())
        } catch (t: Throwable) {
            Either.left(t)
        }
    }

    private suspend fun cancelTransaction(
            authorization: String,
            request: CancelTransactionRequest
    ): Either<Throwable, Resource<String>> {
        return try {
            transactionService.cancelTransaction(
                    authorization = authorization,
                    request = request
            )

            Either.right(Resource.success())
        } catch (ex: CancellationException) {
            throw ex
        } catch (ex: HttpException) {

            val errorBodyResponse: ErrorBodyResponse? = moshi.responseError(ex.response())

            return if (ex.code() == 400) {
                Either.left(InvalidAuthenticationException())
            } else if (ex.code() == 409 && errorBodyResponse != null) {
                Either.right(Resource.error(data = null as String?, message = errorBodyResponse.message))
            } else {
                Either.right(Resource.error(data = null as String?))
            }

        } catch (ex: TimeoutException) {

            val errorMessage: String? = resourceProvider.getString(R.string.timeout_error_message)

            return Either.right(Resource.error(data = null as String?, message = errorMessage))

        } catch (ex: EOFException) {
            Either.left(ex)
        } catch (ex: IOException) {
            Either.left(ConnectionErrorException())
        } catch (t: Throwable) {
            Either.left(t)
        }
    }
}