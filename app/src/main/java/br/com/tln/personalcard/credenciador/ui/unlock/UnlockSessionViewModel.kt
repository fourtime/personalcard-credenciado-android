package br.com.tln.personalcard.credenciador.ui.unlock

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import br.com.tln.personalcard.credenciador.core.Event
import br.com.tln.personalcard.credenciador.core.Resource
import br.com.tln.personalcard.credenciador.core.SessionRequiredBaseViewModel
import br.com.tln.personalcard.credenciador.extensions.sha256
import br.com.tln.personalcard.credenciador.repository.SessionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UnlockSessionViewModel @Inject constructor(
    sessionRepository: SessionRepository,
    navigator: UnlockSessionNavigator
) : SessionRequiredBaseViewModel<UnlockSessionNavigator>(
    sessionRepository = sessionRepository,
    navigator = navigator
) {

    val password = MutableLiveData<String?>()

    private val _supportLiveData = MediatorLiveData<Event<String>>()

    private val _unlockSessionLiveData = MediatorLiveData<Resource<Nothing?>>()
    private val _forgotPasswordLiveData = MediatorLiveData<Event<Nothing?>>()

    val supportLiveData: LiveData<Event<String>> = _supportLiveData

    val unlockSessionLiveData: LiveData<Resource<Nothing?>> = _unlockSessionLiveData
    val forgotPasswordLiveData: LiveData<Event<Nothing?>> = _forgotPasswordLiveData

    val error: MediatorLiveData<String?> by lazy {
        val liveData = MediatorLiveData<String?>()

        liveData.addSource(password) {
            val error = liveData.value

            if (!it.isNullOrEmpty() && !error.isNullOrEmpty()) {
                liveData.postValue(null)
            }
        }

        liveData
    }

    fun unlockSessionClicked() {
        val password = this.password.value

        if (password.isNullOrEmpty()) {
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            sessionRepository.getAccountData()?.also { accountData ->
                if (password.sha256() == accountData.localPassword) {
                    sessionRepository.update(accountData.copy(locked = false))
                    _unlockSessionLiveData.postValue(Resource.success())

                    withContext(Dispatchers.Main) {
                        navigator.navigateToHome()
                    }
                } else {
                    _unlockSessionLiveData.postValue(Resource.error())
                    this@UnlockSessionViewModel.password.postValue("")
                    error.postValue(" ")
                }
            }
        }
    }

    fun forgotPasswordClicked() {
        _forgotPasswordLiveData.postValue(Event(null))
    }

    fun getSupportUrl() {
        viewModelScope.launch {
            sessionRepository.getTerminal()?.let { terminal ->
                _supportLiveData.postValue(Event(terminal.supportUrl))
            }
        }
    }
}