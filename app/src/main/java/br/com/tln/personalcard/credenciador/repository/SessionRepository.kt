package br.com.tln.personalcard.credenciador.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import arrow.core.Either
import br.com.tln.personalcard.credenciador.BuildConfig
import br.com.tln.personalcard.credenciador.R
import br.com.tln.personalcard.credenciador.core.Resource
import br.com.tln.personalcard.credenciador.db.AccessTokenDao
import br.com.tln.personalcard.credenciador.db.AccountDataDao
import br.com.tln.personalcard.credenciador.db.AppPreferences
import br.com.tln.personalcard.credenciador.db.DeviceDao
import br.com.tln.personalcard.credenciador.db.SessionDb
import br.com.tln.personalcard.credenciador.db.SessionPreferences
import br.com.tln.personalcard.credenciador.db.TerminalDao
import br.com.tln.personalcard.credenciador.entity.AccessToken
import br.com.tln.personalcard.credenciador.entity.AccountData
import br.com.tln.personalcard.credenciador.entity.Device
import br.com.tln.personalcard.credenciador.entity.PaymentMethod
import br.com.tln.personalcard.credenciador.entity.Terminal
import br.com.tln.personalcard.credenciador.exception.ConnectionErrorException
import br.com.tln.personalcard.credenciador.exception.ForbiddenException
import br.com.tln.personalcard.credenciador.exception.InvalidAuthenticationException
import br.com.tln.personalcard.credenciador.extensions.CryptographyUtils.encrypt
import br.com.tln.personalcard.credenciador.extensions.responseError
import br.com.tln.personalcard.credenciador.model.Account
import br.com.tln.personalcard.credenciador.model.Session
import br.com.tln.personalcard.credenciador.webservice.TelenetService
import br.com.tln.personalcard.credenciador.webservice.request.SettingInstanceRequest
import br.com.tln.personalcard.credenciador.webservice.response.ErrorBodyResponse
import br.com.tln.personalcard.credenciador.webservice.response.OperatorLinks
import com.google.firebase.messaging.FirebaseMessaging
import com.squareup.moshi.Moshi
import kotlinx.android.synthetic.main.fragment_billing_installments.*
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.threeten.bp.LocalDateTime
import retrofit2.HttpException
import timber.log.Timber
import java.io.EOFException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.coroutineContext

@Singleton
class SessionRepository @Inject constructor(
    private val appPreferences: AppPreferences,
    private val sessionPreferences: SessionPreferences,
    private val sessionDb: SessionDb,
    private val accountDataDao: AccountDataDao,
    private val accessTokenDao: AccessTokenDao,
    private val terminalDao: TerminalDao,
    private val deviceDao: DeviceDao,
    private val operatorRepository: OperatorRepository,
    private val telenetService: TelenetService,
    private val moshi: Moshi
) {

    private var activeSession: Boolean = false

    private val sessionLiveData: LiveData<Session?>

    init {
        sessionLiveData = createSessionLiveData()
        sessionLiveData.observeForever {
            Timber.i("Session state changed")
            activeSession = it?.account != null

            checkFcmToken(it)
        }
    }

    private fun checkFcmToken(session: Session?) {
        GlobalScope.launch(Dispatchers.IO) {

            if (session?.device == null || session.account == null) {
                return@launch
            }

            if (session.device.token == sessionPreferences.getFcmSentToken()) {
                return@launch
            }

            val settingInstanceRequest = SettingInstanceRequest(
                imei = appPreferences.getDeviceId(),
                instanceId = session.device.token
            )

            settingAppInstance(
                settingInstanceRequest,
                session.account.accessToken,
                session.device.token
            )
        }
    }

    private fun createSessionLiveData(): LiveData<Session?> {
        val liveData = MediatorLiveData<Session?>()

        var accountLoaded = false
        var accountDataLoaded = false
        var accessTokenLoaded = false
        var terminalLoaded = false
        var deviceLoaded = false

        var device: Device? = null
        var account: Account? = null
        var accountData: AccountData? = null
        var accessToken: AccessToken? = null
        var terminal: Terminal? = null

        liveData.addSource(accountDataDao.getLiveData()) { accData ->
            accountDataLoaded = true
            accountLoaded = accountDataLoaded && accessTokenLoaded && terminalLoaded

            accountData = accData

            val currentAccountData = accountData
            val currentAccessToken = accessToken
            val currentTerminal = terminal

            account =
                if (currentAccountData == null || currentAccessToken == null || currentTerminal == null) {
                    accountLoaded =
                        accountLoaded && currentAccountData == null && currentAccessToken == null && currentTerminal == null
                    null
                } else {
                    Account(
                        accountData = currentAccountData,
                        accessToken = currentAccessToken,
                        terminal = currentTerminal
                    )
                }


            if (accountLoaded && deviceLoaded) {
                liveData.postValue(
                    Session(
                        account = account,
                        device = device
                    )
                )
            }
        }

        liveData.addSource(accessTokenDao.getLiveData()) { accTok ->
            accessTokenLoaded = true
            accountLoaded = accountDataLoaded && accessTokenLoaded && terminalLoaded

            accessToken = accTok

            val currentAccountData = accountData
            val currentAccessToken = accessToken
            val currentTerminal = terminal

            account =
                if (currentAccountData == null || currentAccessToken == null || currentTerminal == null) {
                    accountLoaded =
                        accountLoaded && currentAccountData == null && currentAccessToken == null && currentTerminal == null
                    null
                } else {
                    Account(
                        accountData = currentAccountData,
                        accessToken = currentAccessToken,
                        terminal = currentTerminal
                    )
                }


            if (accountLoaded && deviceLoaded) {
                liveData.postValue(
                    Session(
                        account = account,
                        device = device
                    )
                )
            }
        }

        liveData.addSource(terminalDao.getLiveData()) { term ->
            terminalLoaded = true
            accountLoaded = accountDataLoaded && accessTokenLoaded && terminalLoaded

            terminal = term

            val currentAccountData = accountData
            val currentAccessToken = accessToken
            val currentTerminal = terminal

            account =
                if (currentAccountData == null || currentAccessToken == null || currentTerminal == null) {
                    accountLoaded =
                        accountLoaded && currentAccountData == null && currentAccessToken == null && currentTerminal == null
                    null
                } else {
                    Account(
                        accountData = currentAccountData,
                        accessToken = currentAccessToken,
                        terminal = currentTerminal
                    )
                }


            if (accountLoaded && deviceLoaded) {
                liveData.postValue(
                    Session(
                        account = account,
                        device = device
                    )
                )
            }
        }

        liveData.addSource(deviceDao.getLiveData()) { dev ->
            deviceLoaded = true

            device = dev

            if (accountLoaded && deviceLoaded) {
                liveData.postValue(
                    Session(
                        account = account,
                        device = device
                    )
                )
            }
        }

        return liveData
    }

    fun getSessionLiveData(allowCache: Boolean = true): LiveData<Session?> {
        return if (allowCache) {
            sessionLiveData
        } else {
            createSessionLiveData()
        }
    }

    fun getAccountLiveData(allowCache: Boolean = true): LiveData<Account> {
        return Transformations.map(getSessionLiveData(allowCache = allowCache)) {
            it?.account
        }
    }

    fun getAccountDataLiveData(allowCache: Boolean = true): LiveData<AccountData> {
        return Transformations.map(getSessionLiveData(allowCache = allowCache)) {
            it?.account?.accountData
        }
    }

    fun getAccessTokenLiveData(allowCache: Boolean = true): LiveData<AccessToken> {
        return Transformations.map(getSessionLiveData(allowCache = allowCache)) {
            it?.account?.accessToken
        }
    }

    fun getTerminalLiveData(allowCache: Boolean = true): LiveData<Terminal> {
        return Transformations.map(getSessionLiveData(allowCache = allowCache)) {
            it?.account?.terminal
        }
    }

    fun getDeviceLiveData(allowCache: Boolean = true): LiveData<Device> {
        return Transformations.map(getSessionLiveData(allowCache = allowCache)) {
            it?.device
        }
    }

    suspend fun getLoginLiveData(
        username: String,
        password: String
    ): LiveData<Either<Throwable, Resource<Account>>> {
        val liveData: MutableLiveData<Either<Throwable, Resource<Account>>> = MutableLiveData()
        liveData.postValue(Either.right(Resource.loading()))

        CoroutineScope(context = coroutineContext).launch {
            val response: Either<Throwable, Resource<Account>> = login(username, password)
            liveData.postValue(response)
        }

        return liveData
    }

    suspend fun getTerminalDataLiveData(accessToken: AccessToken): LiveData<Either<Throwable, Resource<Terminal>>> {
        val liveData: MutableLiveData<Either<Throwable, Resource<Terminal>>> = MutableLiveData()
        liveData.postValue(Either.right(Resource.loading()))

        CoroutineScope(context = coroutineContext).launch {
            val result: Either<Throwable, Resource<Terminal>> =
                getTerminalData(accessToken.formattedToken)
            liveData.postValue(result)
        }

        return liveData
    }

    suspend fun getAcceptedPaymentMethodsLiveData(accessToken: AccessToken): LiveData<Either<Throwable, Resource<List<PaymentMethod>>>> {
        val liveData = MutableLiveData<Either<Throwable, Resource<List<PaymentMethod>>>>()
        liveData.postValue(Either.right(Resource.loading()))

        CoroutineScope(context = coroutineContext).launch {
            val result: Either<Throwable, Resource<List<PaymentMethod>>> =
                getAcceptedPaymentMethods(accessToken.formattedToken)
            liveData.postValue(result)
        }

        return liveData
    }

    private suspend fun login(
        rawUsername: String,
        password: String
    ): Either<Throwable, Resource<Account>> {
        val deviceId = appPreferences.getDeviceId()
        val username = "$deviceId@$rawUsername"

        val loginResponse = try {
            telenetService.login(
                username = username,
                password = password
            )
        } catch (ex: CancellationException) {
            throw ex
        } catch (ex: KotlinNullPointerException) {
            return Either.left(InvalidAuthenticationException())
        } catch (ex: HttpException) {
            val errorBodyResponse: ErrorBodyResponse? = moshi.responseError(ex.response())

            return if (ex.code() == 400) {
                Either.left(InvalidAuthenticationException())
            } else if (ex.code() == 403) {
                Either.left(ForbiddenException())
            } else if (ex.code() == 409 && errorBodyResponse != null) {
                Either.right(Resource.error(data = null as Account?, message = errorBodyResponse.message))
            } else {
                Either.right(Resource.error(data = null as Account?))
            }
        } catch (ex: EOFException) {
            return Either.left(ex)
        } catch (ex: IOException) {
            return Either.left(ConnectionErrorException())
        } catch (t: Throwable) {
            return Either.left(t)
        }

        val accountData = AccountData(
            username = username.encrypt(
                key = BuildConfig.DEFAULT_CRYPTOGRAPHY_KEY,
                initializationVector = BuildConfig.DEFAULT_CRYPTOGRAPHY_INITIALIZATION_VECTOR,
                transformation = BuildConfig.DEFAULT_CRYPTOGRAPHY_TRANSFORMATION,
                algorithm = BuildConfig.DEFAULT_CRYPTOGRAPHY_ALGORITHM
            ),
            password = password.encrypt(
                key = BuildConfig.DEFAULT_CRYPTOGRAPHY_KEY,
                initializationVector = BuildConfig.DEFAULT_CRYPTOGRAPHY_INITIALIZATION_VECTOR,
                transformation = BuildConfig.DEFAULT_CRYPTOGRAPHY_TRANSFORMATION,
                algorithm = BuildConfig.DEFAULT_CRYPTOGRAPHY_ALGORITHM
            )
        )

        val accessToken = AccessToken(
            type = loginResponse.results.type,
            value = loginResponse.results.accessToken,
            scope = loginResponse.results.scope,
            expiration = LocalDateTime.now().plusSeconds(loginResponse.results.expiresIn)
        )

        val terminal: Terminal =
            when (val terminalResult = getTerminalData(accessToken.formattedToken)) {
                is Either.Left -> {
                    return Either.left(terminalResult.a)
                }
                is Either.Right -> {
                    val terminalResource: Resource<Terminal> = terminalResult.b

                    if (terminalResource.status == Resource.Status.SUCCESS) {
                        terminalResource.data ?: return Either.right(Resource.error())
                    } else {
                        return Either.right(
                            Resource(
                                status = terminalResource.status,
                                data = null as Account?,
                                message = terminalResource.message
                            )
                        )
                    }
                }
            }

        val account = Account(
            accountData = accountData,
            accessToken = accessToken,
            terminal = terminal
        )

        sessionDb.runInTransaction {
            accountDataDao.insert(account.accountData)
            accessTokenDao.insert(account.accessToken)
            terminalDao.insert(account.terminal)
        }

        appPreferences.setInitialized(true)

        return Either.right(Resource.success(data = account))
    }


    private suspend fun settingAppInstance(
        settingInstanceRequest: SettingInstanceRequest,
        accessToken: AccessToken,
        deviceToken: String
    ) {
        try {
            val response = telenetService.settingAppInstance(
                request = settingInstanceRequest,
                authorization = accessToken.formattedToken
            )
            val data = response.results

            sessionPreferences.setFcmSentToken(deviceToken)

            Either.right(Resource.success(null))
        } catch (ex: CancellationException) {
            throw ex
        } catch (ex: HttpException) {
            if (ex.code() == 401) {
                Either.left(InvalidAuthenticationException())
            } else {
                //Either.right(Resource.error())
            }
        } catch (ex: EOFException) {
            Either.left(ex)
        } catch (ex: IOException) {
            Either.left(ConnectionErrorException())
        } catch (t: Throwable) {
            Either.left(t)
        }
    }

    suspend fun getTerminalData(authorization: String): Either<Throwable, Resource<Terminal>> {
        val paymentMethods: List<PaymentMethod> =
            when (val paymentMethodResult = getAcceptedPaymentMethods(authorization)) {
                is Either.Left -> {
                    return Either.left(paymentMethodResult.a)
                }
                is Either.Right -> {
                    val paymentMethodsResource: Resource<List<PaymentMethod>> =
                        paymentMethodResult.b

                    if (paymentMethodsResource.status == Resource.Status.SUCCESS) {
                        paymentMethodsResource.data ?: return Either.right(Resource.error())
                    } else {
                        return Either.right(
                            Resource(
                                status = paymentMethodsResource.status,
                                data = null as Terminal?,
                                message = paymentMethodsResource.message
                            )
                        )
                    }
                }
            }

        val links: OperatorLinks =
            when (val paymentMethodResult = operatorRepository.getOperatorLinks()) {
                is Either.Left -> {
                    return Either.left(paymentMethodResult.a)
                }
                is Either.Right -> {
                    val paymentMethodsResource: Resource<OperatorLinks> = paymentMethodResult.b

                    if (paymentMethodsResource.status == Resource.Status.SUCCESS) {
                        paymentMethodsResource.data ?: return Either.right(Resource.error())
                    } else {
                        return Either.right(
                            Resource(
                                status = paymentMethodsResource.status,
                                data = null as Terminal?,
                                message = paymentMethodsResource.message
                            )
                        )
                    }
                }
            }

        return try {
            val response = telenetService.getTerminalData(authorization = authorization)
            val data = response.results

            Either.right(
                Resource.success(
                    data = Terminal(
                        accreditedCode = data.accreditedCode,
                        cnpj = data.cnpj.trim(),
                        businessName = data.businessName,
                        address = data.address,
                        neighborhood = data.neighborhood,
                        city = data.city,
                        state = data.state,
                        postalCode = data.postalCode,
                        maxInstallment = data.maxInstallment,
                        supportUrl = links.contactUrl,
                        acceptedPaymentMethods = paymentMethods,
                        fetchTime = LocalDateTime.now()
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

    private suspend fun getAcceptedPaymentMethods(authorization: String): Either<Throwable, Resource<List<PaymentMethod>>> {
        return try {
            val response =
                telenetService.getTerminalAvailablePaymentMethods(authorization = authorization)
            val data = response.results

            val paymentMethods = data.map {
                PaymentMethod.fromId(it)
            }.filterNotNull()

            Either.right(Resource.success(paymentMethods))
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

    suspend fun isInitialized(): Boolean {
        return appPreferences.isInitialized()
    }

    suspend fun getAccount(): Account? {
        val accountData = accountDataDao.get()
        val accessToken = accessTokenDao.get()
        val terminal = terminalDao.get()

        return if (accountData == null || accessToken == null || terminal == null) {
            null
        } else {
            Account(
                accountData = accountData,
                accessToken = accessToken,
                terminal = terminal
            )
        }
    }

    suspend fun create(device: Device) {
        deviceDao.insert(device)
    }

    suspend fun getDevice(): Device? {
        return deviceDao.get()
    }

    suspend fun update(device: Device) {
        deviceDao.update(device)
    }

    suspend fun update(account: Account) {
        sessionDb.runInTransaction {
            runBlocking {
                update(account.accountData)
                update(account.terminal)
                update(account.accessToken)
            }
        }
    }

    suspend fun getAccountData(): AccountData? {
        return accountDataDao.get()
    }

    suspend fun update(accountData: AccountData) {
        accountDataDao.update(accountData)
    }

    suspend fun getAccessToken(): AccessToken? {
        return accessTokenDao.get()
    }

    suspend fun update(accessToken: AccessToken) {
        accessTokenDao.update(accessToken)
    }

    suspend fun getTerminal(): Terminal? {
        return terminalDao.get()
    }

    suspend fun update(terminal: Terminal) {
        terminalDao.update(terminal)
    }

    suspend fun logout() {
        FirebaseMessaging.getInstance().deleteToken()
        sessionPreferences.clear()
        sessionDb.clearAllTables()
    }
}