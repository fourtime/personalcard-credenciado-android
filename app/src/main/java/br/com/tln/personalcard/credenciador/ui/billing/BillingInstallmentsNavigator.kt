package br.com.tln.personalcard.credenciador.ui.billing

import androidx.navigation.navOptions
import br.com.tln.personalcard.credenciador.R
import br.com.tln.personalcard.credenciador.core.SessionRequiredBaseNavigator
import br.com.tln.personalcard.credenciador.entity.PaymentMethod
import java.math.BigDecimal
import javax.inject.Inject

class BillingInstallmentsNavigator @Inject constructor() : SessionRequiredBaseNavigator() {

    override fun navigateToWelcome() {
        if (navController.currentDestination == null || navController.currentDestination?.id != R.id.billingInstallmentsFragment) {
            return
        }

        val directions = BillingInstallmentsFragmentDirections.actionGlobalWelcomeFragment()
        navController.navigate(directions, navOptions {
            popUpTo(R.id.homeFragment) {
                inclusive = true
            }
        })
    }

    override fun navigateToLogin() {
        if (navController.currentDestination == null || navController.currentDestination?.id != R.id.billingInstallmentsFragment) {
            return
        }

        val directions = BillingInstallmentsFragmentDirections.actionGlobalInitializationLoginFragment()
        navController.navigate(directions, navOptions {
            popUpTo(R.id.homeFragment) {
                inclusive = true
            }
        })
    }

    override fun navigateToCreatePassword() {
        if (navController.currentDestination == null || navController.currentDestination?.id != R.id.billingInstallmentsFragment) {
            return
        }

        val directions = BillingInstallmentsFragmentDirections.actionGlobalInitializationCreatePasswordFragment()
        navController.navigate(directions, navOptions {
            popUpTo(R.id.homeFragment) {
                inclusive = true
            }
        })
    }

    override fun navigateToUnlockSession() {
        if (navController.currentDestination == null || navController.currentDestination?.id != R.id.billingInstallmentsFragment) {
            return
        }

        val directions = BillingInstallmentsFragmentDirections.actionGlobalUnlockSessionFragment()
        navController.navigate(directions, navOptions {
            popUpTo(R.id.homeFragment) {
                inclusive = true
            }
        })
    }

    fun navigateToQrCode(cardType: PaymentMethod, installments: Int, billingValue: BigDecimal) {
        if (navController.currentDestination == null || navController.currentDestination?.id != R.id.billingInstallmentsFragment) {
            return
        }

        val directions = BillingInstallmentsFragmentDirections.actionBillingInstallmentsFragmentToBillingQrCodeFragment(
            cardType = cardType.id ?: 0,
            installments = installments,
            billingValue = billingValue.toString()
        )
        navController.navigate(directions)
    }
}