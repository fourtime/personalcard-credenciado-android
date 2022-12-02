package br.com.tln.personalcard.credenciador.ui.unlock

import androidx.navigation.navOptions
import br.com.tln.personalcard.credenciador.R
import br.com.tln.personalcard.credenciador.core.SessionRequiredBaseNavigator
import javax.inject.Inject

class UnlockSessionNavigator @Inject constructor() : SessionRequiredBaseNavigator() {

    override fun navigateToWelcome() {
        if (navController.currentDestination == null || navController.currentDestination?.id != R.id.unlockSessionFragment) {
            return
        }

        val directions = UnlockSessionFragmentDirections.actionGlobalWelcomeFragment()
        navController.navigate(directions, navOptions {
            popUpTo(R.id.unlockSessionFragment) {
                inclusive = true
            }
        })
    }

    override fun navigateToLogin() {
        if (navController.currentDestination == null || navController.currentDestination?.id != R.id.unlockSessionFragment) {
            return
        }

        val directions = UnlockSessionFragmentDirections.actionGlobalInitializationLoginFragment()
        navController.navigate(directions, navOptions {
            popUpTo(R.id.unlockSessionFragment) {
                inclusive = true
            }
        })
    }

    override fun navigateToCreatePassword() {
        if (navController.currentDestination == null || navController.currentDestination?.id != R.id.unlockSessionFragment) {
            return
        }

        val directions = UnlockSessionFragmentDirections.actionGlobalInitializationCreatePasswordFragment()
        navController.navigate(directions, navOptions {
            popUpTo(R.id.unlockSessionFragment) {
                inclusive = true
            }
        })
    }

    override fun navigateToUnlockSession() {
        if (navController.currentDestination == null || navController.currentDestination?.id != R.id.unlockSessionFragment) {
            return
        }
    }

    fun navigateToHome() {
        if (navController.currentDestination == null || navController.currentDestination?.id != R.id.unlockSessionFragment) {
            return
        }

        val directions = UnlockSessionFragmentDirections.actionUnlockSessionFragmentToHomeFragment()
        navController.navigate(directions)
    }
}