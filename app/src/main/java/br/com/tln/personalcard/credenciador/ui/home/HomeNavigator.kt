package br.com.tln.personalcard.credenciador.ui.home

import androidx.navigation.navOptions
import br.com.tln.personalcard.credenciador.R
import br.com.tln.personalcard.credenciador.core.SessionRequiredBaseNavigator
import timber.log.Timber
import javax.inject.Inject

class HomeNavigator @Inject constructor() : SessionRequiredBaseNavigator() {

    override fun navigateToWelcome() {
        if (navController.currentDestination == null || navController.currentDestination?.id != R.id.homeFragment) {
            return
        }

        val directions = HomeFragmentDirections.actionGlobalWelcomeFragment()
        navController.navigate(directions, navOptions {
            popUpTo(R.id.homeFragment) {
                inclusive = true
            }
        })
    }

    override fun navigateToLogin() {
        if (navController.currentDestination == null || navController.currentDestination?.id != R.id.homeFragment) {
            return
        }

        val directions = HomeFragmentDirections.actionGlobalInitializationLoginFragment()
        navController.navigate(directions, navOptions {
            popUpTo(R.id.homeFragment) {
                inclusive = true
            }
        })
    }

    override fun navigateToCreatePassword() {
        if (navController.currentDestination == null || navController.currentDestination?.id != R.id.homeFragment) {
            return
        }

        val directions = HomeFragmentDirections.actionGlobalInitializationCreatePasswordFragment()
        navController.navigate(directions, navOptions {
            popUpTo(R.id.homeFragment) {
                inclusive = true
            }
        })
    }

    override fun navigateToUnlockSession() {
        if (navController.currentDestination == null || navController.currentDestination?.id != R.id.homeFragment) {
            return
        }

        val directions = HomeFragmentDirections.actionGlobalUnlockSessionFragment()
        navController.navigate(directions, navOptions {
            popUpTo(R.id.homeFragment) {
                inclusive = true
            }
        })
    }

    fun navigateToTransactions() {
        if (navController.currentDestination == null || navController.currentDestination?.id != R.id.homeFragment) {
            return
        }

        val directions = HomeFragmentDirections.actionHomeFragmentToTransactionsFragment()
        navController.navigate(directions)
    }

    fun navigateToChangePassword() {
        if (navController.currentDestination == null || navController.currentDestination?.id != R.id.homeFragment) {
            return
        }

        val directions = HomeFragmentDirections.actionHomeFragmentToChangePasswordFragment()
        navController.navigate(directions)
    }

    fun navigateToPrePaidCard(maxInstallments: Int) {
        if (navController.currentDestination == null || navController.currentDestination?.id != R.id.homeFragment) {
            return
        }

        val directions = HomeFragmentDirections.actionHomeFragmentToBillingValueFragment(
            title = "Cartão Pré-pago",
            maxInstallments = maxInstallments
        )
        navController.navigate(directions)
    }

    fun navigateToPostPaidCard(maxInstallments: Int) {
        if (navController.currentDestination == null || navController.currentDestination?.id != R.id.homeFragment) {
            return
        }

        val directions = HomeFragmentDirections.actionHomeFragmentToBillingValueFragment(
            title = "Cartão Pós-pago",
            maxInstallments = maxInstallments
        )
        navController.navigate(directions)
    }

    fun navigateToFleetCard() {
        if (navController.currentDestination == null || navController.currentDestination?.id != R.id.homeFragment) {
            return
        }

        Timber.i("Not implemented")
    }
}