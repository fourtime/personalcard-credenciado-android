package br.com.tln.personalcard.credenciador.ui.welcome

import br.com.tln.personalcard.credenciador.core.BaseViewModel
import javax.inject.Inject

class WelcomeViewModel @Inject constructor(navigator: WelcomeNavigator) :
    BaseViewModel<WelcomeNavigator>(navigator = navigator) {
    fun continueClicked() {
        navigator.navigateToInitializationLogin()
    }
}