package br.com.tln.personalcard.credenciador.ui.billing

import androidx.navigation.navOptions
import br.com.tln.personalcard.credenciador.R
import br.com.tln.personalcard.credenciador.core.SessionRequiredBaseNavigator
import br.com.tln.personalcard.credenciador.model.CardPayment
import java.math.BigDecimal
import javax.inject.Inject

class BillingCardPaymentNavigator @Inject constructor() : SessionRequiredBaseNavigator() {

    override fun navigateToWelcome() {
        if (navController.currentDestination == null || navController.currentDestination?.id != R.id.billingCardPaymentFragment) {
            return
        }

        val directions = BillingCardPaymentFragmentDirections.actionGlobalWelcomeFragment()
        navController.navigate(directions, navOptions {
            popUpTo(R.id.homeFragment) {
                inclusive = true
            }
        })
    }

    override fun navigateToLogin() {
        if (navController.currentDestination == null || navController.currentDestination?.id != R.id.billingCardPaymentFragment) {
            return
        }

        val directions = BillingCardPaymentFragmentDirections.actionGlobalInitializationLoginFragment()
        navController.navigate(directions, navOptions {
            popUpTo(R.id.homeFragment) {
                inclusive = true
            }
        })
    }

    override fun navigateToCreatePassword() {
        if (navController.currentDestination == null || navController.currentDestination?.id != R.id.billingCardPaymentFragment) {
            return
        }

        val directions = BillingCardPaymentFragmentDirections.actionGlobalInitializationCreatePasswordFragment()
        navController.navigate(directions, navOptions {
            popUpTo(R.id.homeFragment) {
                inclusive = true
            }
        })
    }

    override fun navigateToUnlockSession() {
        if (navController.currentDestination == null || navController.currentDestination?.id != R.id.billingCardPaymentFragment) {
            return
        }

        val directions = BillingCardPaymentFragmentDirections.actionGlobalUnlockSessionFragment()
        navController.navigate(directions, navOptions {
            popUpTo(R.id.homeFragment) {
                inclusive = true
            }
        })
    }

    fun navigateToPaymentSuccess(cardPayment: CardPayment, billingValue: String) {
        if (navController.currentDestination == null || navController.currentDestination?.id != R.id.billingCardPaymentFragment) {
            return
        }

        val directions = BillingCardPaymentFragmentDirections.actionBillingCardPaymentFragmentToBillingPaymentSuccessFragment(
                cardPayment = cardPayment,
            billingValue = billingValue
        )
        navController.navigate(directions)
    }
}