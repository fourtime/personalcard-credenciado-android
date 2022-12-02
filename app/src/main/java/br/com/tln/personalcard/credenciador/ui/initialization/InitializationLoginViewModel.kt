package br.com.tln.personalcard.credenciador.ui.initialization

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import arrow.core.Either
import br.com.tln.personalcard.credenciador.R
import br.com.tln.personalcard.credenciador.binding.ConditionalMask
import br.com.tln.personalcard.credenciador.core.BaseViewModel
import br.com.tln.personalcard.credenciador.core.Event
import br.com.tln.personalcard.credenciador.core.Resource
import br.com.tln.personalcard.credenciador.core.Resource.Status.ERROR
import br.com.tln.personalcard.credenciador.core.Resource.Status.LOADING
import br.com.tln.personalcard.credenciador.core.Resource.Status.SUCCESS
import br.com.tln.personalcard.credenciador.exception.ForbiddenException
import br.com.tln.personalcard.credenciador.exception.InvalidAuthenticationException
import br.com.tln.personalcard.credenciador.model.Account
import br.com.tln.personalcard.credenciador.provider.ResourceProvider
import br.com.tln.personalcard.credenciador.repository.OperatorRepository
import br.com.tln.personalcard.credenciador.repository.SessionRepository
import br.com.tln.personalcard.credenciador.webservice.response.OperatorLinks
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class InitializationLoginViewModel @Inject constructor(
    private val sessionRepository: SessionRepository,
    private val operatorRepository: OperatorRepository,
    private val resourceProvider: ResourceProvider,
    navigator: InitializationLoginNavigator
) : BaseViewModel<InitializationLoginNavigator>(navigator = navigator) {

    private val cpfLength by lazy {
        resourceProvider.getInteger(R.integer.cpf_length)
    }

    private val cnpjLength by lazy {
        resourceProvider.getInteger(R.integer.cnpj_lenght)
    }

    val terminalLength by lazy {
        resourceProvider.getInteger(R.integer.terminal_length)
    }

    val username = MutableLiveData<String>()
    val password = MutableLiveData<String>()

    private val _loginLiveData = MediatorLiveData<Resource<Nothing?>>()
    private val _needHelpLiveData = MutableLiveData<Event<Nothing?>>()

    val loginLiveData: LiveData<Resource<Nothing?>> = _loginLiveData
    val needHelpLiveData: LiveData<Event<Nothing?>> = _needHelpLiveData
    val operatorLinksLiveData: LiveData<Resource<OperatorLinks>> by lazy {
        getOperatorLinks()
    }

    val credentialMasks = listOf(
        ConditionalMask(resourceProvider.getString(R.string.mask_dynamic_cpf)) {
            it == null || it.length <= cpfLength
        },
        ConditionalMask(resourceProvider.getString(R.string.mask_dynamic_cnpj)) {
            it == null || it.length > cpfLength
        }
    )

    val valid: MutableLiveData<Boolean> by lazy {
        val liveData = MediatorLiveData<Boolean>()

        liveData.addSource(username) {
            liveData.postValue(isValid())
        }

        liveData.addSource(password) {
            liveData.postValue(isValid())
        }

        liveData
    }

    private fun isValid(): Boolean {
        val username = this.username.value
        val password = this.password.value

        return (!username.isNullOrEmpty() && (username.length >= cpfLength && username.length <= cnpjLength)) && (!password.isNullOrEmpty() && password.length == terminalLength)
    }

    fun loginClicked() {
        _errorMessageLiveData.postValue(Event(data = ""))

        val username = this.username.value
        val password = this.password.value

        if (username.isNullOrEmpty() || password.isNullOrEmpty()) {
            return
        }

        viewModelScope.launch {
            val loginLiveData = sessionRepository.getLoginLiveData(username, password)

            _loginLiveData.addSource(loginLiveData) { result ->
                when (result) {
                    is Either.Left -> {
                        when (result.a) {
                            is InvalidAuthenticationException -> {
                                val message =
                                    resourceProvider.getString(R.string.initialization_login_invalid_credentials)
                                _loginLiveData.postValue(Resource.error(message = message))
                                _errorMessageLiveData.postValue(Event(data = message))
                            } is ForbiddenException -> {
                            val message =
                                resourceProvider.getString(R.string.initialization_login_forbidden_terminal)
                            _loginLiveData.postValue(Resource.error(message = message))
                            _errorMessageLiveData.postValue(Event(data = message))
                            }
                            else -> {
                                _errorMessageLiveData.postValue(Event(data = ""))
                                _loginLiveData.postValue(Resource.error())
                                errorNotifier.notify(result.a)
                            }
                        }
                    }
                    is Either.Right -> {
                        val resource: Resource<Account> = result.b

                        when (resource.status) {
                            LOADING -> {
                                _loginLiveData.postValue(Resource.loading())
                            }
                            SUCCESS -> {
                                resource.data?.let {
                                    viewModelScope.launch {
                                        _loginLiveData.postValue(Resource.success())
                                        navigator.navigateToCreatePassword()
                                    }
                                } ?: run {
                                    _loginLiveData.postValue(Resource.error())
                                    _unknownErrorLiveData.postValue(Event(data = null))
                                }
                            }
                            ERROR -> {
                                _loginLiveData.postValue(Resource.error())

                                if (resource.message.isNullOrEmpty()) {
                                    _unknownErrorLiveData.postValue(Event(data = null))
                                } else {
                                    _errorMessageLiveData.postValue(Event(resource.message))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    fun needHelpClicked() {
        _needHelpLiveData.postValue(Event(data = null))
    }

    private fun getOperatorLinks(): LiveData<Resource<OperatorLinks>> {
        val liveData = MutableLiveData<Resource<OperatorLinks>>()

        viewModelScope.launch(Dispatchers.IO) {
            when (val result = operatorRepository.getOperatorLinks()) {
                is Either.Left -> {
                    liveData.postValue(Resource.error())
                    errorNotifier.notify(result.a)
                }
                is Either.Right -> {
                    val resource: Resource<OperatorLinks> = result.b

                    when (resource.status) {
                        LOADING -> {
                            liveData.postValue(Resource.loading())
                        }
                        SUCCESS -> {
                            resource.data?.let {
                                liveData.postValue(Resource.success(data = it))
                            } ?: run {
                                liveData.postValue(Resource.error())
                                _unknownErrorLiveData.postValue(Event(data = null))
                            }
                        }
                        ERROR -> {
                            liveData.postValue(Resource.error())
                            _unknownErrorLiveData.postValue(Event(data = null))
                        }
                    }
                }
            }
        }

        return liveData
    }
}