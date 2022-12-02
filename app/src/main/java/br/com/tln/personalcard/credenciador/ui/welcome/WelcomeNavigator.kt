package br.com.tln.personalcard.credenciador.ui.welcome

import br.com.tln.personalcard.credenciador.R
import br.com.tln.personalcard.credenciador.core.BaseNavigator
import javax.inject.Inject

class WelcomeNavigator @Inject constructor() : BaseNavigator() {
    fun navigateToInitializationLogin() {
        if (navController.currentDestination == null || navController.currentDestination?.id != R.id.welcomeFragment) {
            return
        }

        val directions = WelcomeFragmentDirections.actionWelcomeFragmentToInitializationLoginFragment()
        navController.navigate(directions)
    }
}