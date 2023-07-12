package br.com.tln.personalcard.credenciador.ui.billing

import androidx.navigation.navOptions
import br.com.tln.personalcard.credenciador.R
import br.com.tln.personalcard.credenciador.core.SessionRequiredBaseNavigator
import br.com.tln.personalcard.credenciador.ui.transactions.DayTransactionsFragmentDirections
import javax.inject.Inject

class BillingPaymentSuccessNavigator @Inject constructor() : SessionRequiredBaseNavigator() {

    override fun navigateToWelcome() {
        if (navController.currentDestination == null || navController.currentDestination?.id != R.id.billingPaymentSuccessFragment) {
            return
        }

        val directions = BillingPaymentSuccessFragmentDirections.actionGlobalWelcomeFragment()
        navController.navigate(directions, navOptions {
            popUpTo(R.id.homeFragment) {
                inclusive = true
            }
        })
    }

    override fun navigateToLogin() {
        if (navController.currentDestination == null || navController.currentDestination?.id != R.id.billingPaymentSuccessFragment) {
            return
        }

        val directions = BillingPaymentSuccessFragmentDirections.actionGlobalInitializationLoginFragment()
        navController.navigate(directions, navOptions {
            popUpTo(R.id.homeFragment) {
                inclusive = true
            }
        })
    }

    override fun navigateToCreatePassword() {
        if (navController.currentDestination == null || navController.currentDestination?.id != R.id.billingPaymentSuccessFragment) {
            return
        }

        val directions = BillingPaymentSuccessFragmentDirections.actionGlobalInitializationCreatePasswordFragment()
        navController.navigate(directions, navOptions {
            popUpTo(R.id.homeFragment) {
                inclusive = true
            }
        })
    }

    override fun navigateToUnlockSession() {
        if (navController.currentDestination == null || navController.currentDestination?.id != R.id.billingPaymentSuccessFragment) {
            return
        }

        val directions = BillingPaymentSuccessFragmentDirections.actionGlobalUnlockSessionFragment()
        navController.navigate(directions, navOptions {
            popUpTo(R.id.homeFragment) {
                inclusive = true
            }
        })
    }

    fun navigateToHome() {
        if (navController.currentDestination == null || navController.currentDestination?.id != R.id.billingPaymentSuccessFragment) {
            return
        }

        val directions = BillingPaymentSuccessFragmentDirections.actionBillingPaymentSuccessFragmentToHomeFragment()
        navController.navigate(directions, navOptions {
            popUpTo(R.id.homeFragment) {
                inclusive = true
            }
        })
    }
}