package br.com.tln.personalcard.credenciador.core

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import br.com.tln.personalcard.credenciador.entity.Terminal
import br.com.tln.personalcard.credenciador.exception.InvalidAuthenticationException
import br.com.tln.personalcard.credenciador.repository.SessionRepository
import kotlinx.coroutines.launch

abstract class SessionRequiredBaseViewModel<T : SessionRequiredBaseNavigator>(
    protected val sessionRepository: SessionRepository,
    navigator: T
) : BaseViewModel<T>(navigator = navigator) {

    private val _authenticationErrorLiveData = MutableLiveData<Event<Nothing?>>()

    val authenticationErrorLiveData: LiveData<Event<Nothing?>> = _authenticationErrorLiveData
    val sessionMonitorLiveData: LiveData<Terminal> by lazy {
        val liveData: MediatorLiveData<Terminal> = MediatorLiveData()

        liveData.addSource(
            sessionRepository.getAccountLiveData(
                allowCache = false
            )
        ) { account ->
            if (account == null) {
                onSessionNotFound()
                viewModelScope.launch {
                    if (sessionRepository.isInitialized()) {
                        navigator.navigateToLogin()
                    } else {
                        navigator.navigateToWelcome()
                    }
                }
            } else {
                when {
                    account.localPassword == null -> {
                        onLockedSessionFound()
                        navigator.navigateToCreatePassword()
                    }
                    account.locked -> {
                        onLockedSessionFound()
                        navigator.navigateToUnlockSession()
                    }
                    else -> {
                        onSessionFound()
                        liveData.postValue(account.terminal)
                    }
                }
            }
        }

        liveData
    }

    override fun getAdditionalErrorHandlersMapping(): LinkedHashMap<Class<out Throwable>, List<MutableLiveData<Event<Nothing?>>>> {
        return linkedMapOf(
            InvalidAuthenticationException::class.java to listOf(
                _authenticationErrorLiveData
            )
        )
    }

    protected open fun onSessionNotFound() {
    }

    protected open fun onLockedSessionFound() {
    }

    protected open fun onSessionFound() {
    }

    fun lockSession() {
        viewModelScope.launch {
            sessionRepository.getAccountData()?.let { accountData ->
                sessionRepository.update(accountData.copy(locked = true))
            }
        }
    }

}