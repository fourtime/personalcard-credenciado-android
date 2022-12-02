package br.com.tln.personalcard.credenciador.ui.initialization

import br.com.tln.personalcard.credenciador.R
import br.com.tln.personalcard.credenciador.core.BaseNavigator
import javax.inject.Inject

class InitializationLoginNavigator @Inject constructor() : BaseNavigator() {

    fun navigateToCreatePassword() {
        if (navController.currentDestination == null || navController.currentDestination?.id != R.id.initializationLoginFragment) {
            return
        }

        val directions =
            InitializationLoginFragmentDirections.actionInitializationLoginFragmentToInitializationCreatePasswordFragment()
        navController.navigate(directions)
    }
}