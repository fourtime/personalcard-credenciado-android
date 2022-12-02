package br.com.tln.personalcard.credenciador.ui.initialization

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import br.com.tln.personalcard.credenciador.core.BaseViewModel
import br.com.tln.personalcard.credenciador.core.Resource
import br.com.tln.personalcard.credenciador.extensions.sha256
import br.com.tln.personalcard.credenciador.repository.SessionRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

class InitializationCreatePasswordViewModel @Inject constructor(
    private val sessionRepository: SessionRepository,
    navigator: InitializationCreatePasswordNavigator
) : BaseViewModel<InitializationCreatePasswordNavigator>(navigator = navigator) {

    private val _createPasswordLiveData = MediatorLiveData<Resource<Nothing?>>()

    val password = MutableLiveData<String?>()
    val passwordConfirmation = MutableLiveData<String?>()
    val valid: MutableLiveData<Boolean> by lazy {
        val liveData = MediatorLiveData<Boolean>()

        liveData.addSource(password) {
            liveData.postValue(isValid())
        }

        liveData.addSource(passwordConfirmation) {
            liveData.postValue(isValid())
        }

        liveData
    }

    val createPasswordLiveData: LiveData<Resource<Nothing?>> = _createPasswordLiveData

    private fun isValid(): Boolean {
        val password = this.password.value
        val passwordConfirmation = this.passwordConfirmation.value

        return !password.isNullOrEmpty() && password.length >= 4 && password == passwordConfirmation
    }

    fun createPasswordClicked() {
        val password = this.password.value
        val passwordConfirmation = this.passwordConfirmation.value

        if (password.isNullOrEmpty() || passwordConfirmation.isNullOrEmpty()) {
            return
        } else if (password != passwordConfirmation) {
            return
        }

        viewModelScope.launch {
            sessionRepository.getAccountData()?.also { accountData ->
                sessionRepository.update(accountData.copy(localPassword = password.sha256()))
                _createPasswordLiveData.postValue(Resource.success())
                navigator.navigateToHome()
            }
        }
    }
}