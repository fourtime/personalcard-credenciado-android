package br.com.tln.personalcard.credenciador.repository

import arrow.core.Either
import br.com.tln.personalcard.credenciador.core.Resource
import br.com.tln.personalcard.credenciador.exception.ConnectionErrorException
import br.com.tln.personalcard.credenciador.exception.InvalidAuthenticationException
import br.com.tln.personalcard.credenciador.webservice.TelenetService
import br.com.tln.personalcard.credenciador.webservice.response.OperatorLinks
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.EOFException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OperatorRepository @Inject constructor(
    private val telenetService: TelenetService
) {

    private lateinit var operatorLinksResult: Either<Throwable, Resource<OperatorLinks>>

    suspend fun getOperatorLinks(): Either<Throwable, Resource<OperatorLinks>> {
        if (::operatorLinksResult.isInitialized) {
            val previousResult = operatorLinksResult
            if (previousResult is Either.Right && previousResult.b.status == Resource.Status.SUCCESS) {
                return operatorLinksResult
            }
        }

        return withContext(Dispatchers.IO) {
            val result = operatorLinksRequest()

            operatorLinksResult = result

            result
        }
    }

    private suspend fun operatorLinksRequest(): Either<Throwable, Resource<OperatorLinks>> {
        return try {
            val response = telenetService.getOperatorLinks()
            val data = response.results

            Either.right(Resource.success(data))
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
}