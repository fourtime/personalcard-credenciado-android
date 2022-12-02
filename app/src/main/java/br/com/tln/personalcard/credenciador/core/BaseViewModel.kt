package br.com.tln.personalcard.credenciador.core

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import br.com.tln.personalcard.credenciador.exception.ConnectionErrorException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

abstract class BaseViewModel<T : BaseNavigator>(val navigator: T) : ViewModel() {

    protected val viewModelJob = Job()

    protected val viewModelScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    protected val _connectionErrorLiveData = MutableLiveData<Event<Nothing?>>()
    protected val _unknownErrorLiveData = MutableLiveData<Event<Nothing?>>()
    protected val _errorMessageLiveData = MutableLiveData<Event<String>>()

    protected val errorNotifier: ErrorNotifier by lazy {
        val errorHandlerMap: LinkedHashMap<Class<out Throwable>, List<MutableLiveData<Event<Nothing?>>>> = linkedMapOf(
            ConnectionErrorException::class.java to listOf(
                _connectionErrorLiveData
            )
        )

        errorHandlerMap.putAll(getAdditionalErrorHandlersMapping())

        ErrorNotifier(
            fallbackHandler = _unknownErrorLiveData,
            errorHandlersMapping = errorHandlerMap.toMap()
        )
    }

    val connectionErrorLiveData: LiveData<Event<Nothing?>> = _connectionErrorLiveData
    val unknownErrorLiveData: LiveData<Event<Nothing?>> = _unknownErrorLiveData
    val errorMessageLiveData: LiveData<Event<String>> = _errorMessageLiveData

    fun setNavController(navController: NavController) {
        navigator.navController = navController
    }

    protected open fun getAdditionalErrorHandlersMapping(): LinkedHashMap<Class<out Throwable>, List<MutableLiveData<Event<Nothing?>>>> {
        return linkedMapOf()
    }

    override fun onCleared() {
        viewModelJob.cancel()
    }
}