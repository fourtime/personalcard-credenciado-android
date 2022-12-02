package br.com.tln.personalcard.credenciador.ui.initialization

import br.com.tln.personalcard.credenciador.R
import br.com.tln.personalcard.credenciador.core.BaseNavigator
import javax.inject.Inject

class InitializationCreatePasswordNavigator @Inject constructor() : BaseNavigator() {

    fun navigateToHome() {
        if (navController.currentDestination == null || navController.currentDestination?.id != R.id.initializationCreatePasswordFragment) {
            return
        }

        val directions =
            InitializationCreatePasswordFragmentDirections.actionInitializationCreatePasswordFragmentToHomeFragment()
        navController.navigate(directions)
    }
}