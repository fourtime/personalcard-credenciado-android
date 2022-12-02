package br.com.tln.personalcard.credenciador.ui.changepassword

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import br.com.tln.personalcard.credenciador.R
import br.com.tln.personalcard.credenciador.core.Event
import br.com.tln.personalcard.credenciador.core.Resource
import br.com.tln.personalcard.credenciador.core.SessionRequiredBaseViewModel
import br.com.tln.personalcard.credenciador.extensions.sha256
import br.com.tln.personalcard.credenciador.provider.ResourceProvider
import br.com.tln.personalcard.credenciador.repository.SessionRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

class ChangePasswordViewModel @Inject constructor(
    sessionRepository: SessionRepository,
    navigator: ChangePasswordNavigator,
    private val resourceProvider: ResourceProvider
) : SessionRequiredBaseViewModel<ChangePasswordNavigator>(
    navigator = navigator,
    sessionRepository = sessionRepository
) {

    private val _changePasswordLiveData = MediatorLiveData<Resource<Nothing?>>()

    val currentPassword = MutableLiveData<String?>()
    val newPassword = MutableLiveData<String?>()
    val newPasswordConfirmation = MutableLiveData<String?>()
    val valid: MutableLiveData<Boolean> by lazy {
        val liveData = MediatorLiveData<Boolean>()

        liveData.addSource(currentPassword) {
            liveData.postValue(isValid())
        }

        liveData.addSource(newPassword) {
            liveData.postValue(isValid())
        }

        liveData.addSource(newPasswordConfirmation) {
            liveData.postValue(isValid())
        }

        liveData
    }

    val changePasswordLiveData: LiveData<Resource<Nothing?>> = _changePasswordLiveData

    private fun isValid(): Boolean {
        val currentPassword = this.currentPassword.value
        val password = this.newPassword.value
        val passwordConfirmation = this.newPasswordConfirmation.value

        _errorMessageLiveData.postValue(Event(""))

        return !currentPassword.isNullOrEmpty() && !password.isNullOrEmpty() && password.length >= 4 && password == passwordConfirmation
    }

    fun onCloseClicked() {
        navigator.navigateToHome()
    }

    fun changePasswordClicked() {
        _errorMessageLiveData.postValue(Event(""))
        _changePasswordLiveData.postValue(Resource.loading())

        val currentPassword = this.currentPassword.value
        val newPassword = this.newPassword.value
        val newPasswordConfirmation = this.newPasswordConfirmation.value

        if (currentPassword.isNullOrEmpty() || newPassword.isNullOrEmpty() || newPasswordConfirmation.isNullOrEmpty()) {
            _changePasswordLiveData.postValue(Resource.error())
            return
        } else if (newPassword != newPasswordConfirmation) {
            _changePasswordLiveData.postValue(Resource.error())
            return
        }

        viewModelScope.launch {
            sessionRepository.getAccountData()?.also { accountData ->
                if (currentPassword.sha256() != accountData.localPassword) {
                    _changePasswordLiveData.postValue(Resource.error())
                    _errorMessageLiveData.postValue(Event(resourceProvider.getString(R.string.change_password_invalid_current_password)))
                    return@launch
                }

                sessionRepository.update(accountData.copy(localPassword = newPassword.sha256()))
                _changePasswordLiveData.postValue(Resource.success())
            }
        }
    }
}