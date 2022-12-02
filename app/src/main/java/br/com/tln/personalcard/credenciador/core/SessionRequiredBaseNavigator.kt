package br.com.tln.personalcard.credenciador.core

abstract class SessionRequiredBaseNavigator : BaseNavigator() {
    abstract fun navigateToWelcome()

    abstract fun navigateToLogin()

    abstract fun navigateToCreatePassword()

    abstract fun navigateToUnlockSession()
}