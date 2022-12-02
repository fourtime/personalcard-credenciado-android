package br.com.tln.personalcard.credenciador.okhttp.authenticator

import br.com.tln.personalcard.credenciador.AUTHENTICATION_TELENET_SERVICE
import br.com.tln.personalcard.credenciador.AUTHORIZATION_HEADER
import br.com.tln.personalcard.credenciador.db.AccessTokenDao
import br.com.tln.personalcard.credenciador.db.AccountDataDao
import br.com.tln.personalcard.credenciador.entity.AccessToken
import br.com.tln.personalcard.credenciador.extensions.CryptographyUtils.decrypt
import br.com.tln.personalcard.credenciador.webservice.TelenetService
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import org.threeten.bp.LocalDateTime
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

class TelenetAuthenticator @Inject constructor(
    @param:Named(AUTHENTICATION_TELENET_SERVICE) private val telenetService: TelenetService,
    private val accountDataDao: AccountDataDao,
    private val accessTokenDao: AccessTokenDao
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {

        val previousToken = response.request().header(AUTHORIZATION_HEADER) ?: return null
        if (response.request().tag(TokenRetry::class.java) != null) {
            Timber.d("Token renewal did not fix authentication issues")
            return null
        }

        Timber.d("Invalid token detected: $previousToken\nRenewal will be attempted")

        val accessToken: AccessToken = accountDataDao.get()?.let { account ->
            runBlocking {
                try {
                    telenetService.login(
                        username = account.username.decrypt(),
                        password = account.password.decrypt()
                    ).let { loginResponse ->
                        AccessToken(
                            type = loginResponse.results.type,
                            value = loginResponse.results.accessToken,
                            scope = loginResponse.results.scope,
                            expiration = LocalDateTime.now().plusSeconds(loginResponse.results.expiresIn)
                        )
                    }.also { accessToken ->
                        Timber.d("Token renewal attempt successfully completed.\nNew token is: ${accessToken.formattedToken}")
                        accessTokenDao.update(accessToken)
                    }
                } catch (t: Throwable) {
                    Timber.d(t, "Token renewal attempt failed")
                    return@runBlocking null
                }
            }
        } ?: return null

        return response.request().newBuilder()
            .header(AUTHORIZATION_HEADER, accessToken.formattedToken)
            .tag(TokenRetry::class.java, TokenRetry())
            .build()
    }
}

private class TokenRetry()