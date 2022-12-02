package br.com.tln.personalcard.credenciador.ui.billing

import androidx.navigation.navOptions
import br.com.tln.personalcard.credenciador.R
import br.com.tln.personalcard.credenciador.core.SessionRequiredBaseNavigator
import java.math.BigDecimal
import javax.inject.Inject

class BillingValueNavigator @Inject constructor() : SessionRequiredBaseNavigator() {

    override fun navigateToWelcome() {
        if (navController.currentDestination == null || navController.currentDestination?.id != R.id.billingValueFragment) {
            return
        }

        val directions = BillingValueFragmentDirections.actionGlobalWelcomeFragment()
        navController.navigate(directions, navOptions {
            popUpTo(R.id.homeFragment) {
                inclusive = true
            }
        })
    }

    override fun navigateToLogin() {
        if (navController.currentDestination == null || navController.currentDestination?.id != R.id.billingValueFragment) {
            return
        }

        val directions = BillingValueFragmentDirections.actionGlobalInitializationLoginFragment()
        navController.navigate(directions, navOptions {
            popUpTo(R.id.homeFragment) {
                inclusive = true
            }
        })
    }

    override fun navigateToCreatePassword() {
        if (navController.currentDestination == null || navController.currentDestination?.id != R.id.billingValueFragment) {
            return
        }

        val directions = BillingValueFragmentDirections.actionGlobalInitializationCreatePasswordFragment()
        navController.navigate(directions, navOptions {
            popUpTo(R.id.homeFragment) {
                inclusive = true
            }
        })
    }

    override fun navigateToUnlockSession() {
        if (navController.currentDestination == null || navController.currentDestination?.id != R.id.billingValueFragment) {
            return
        }

        val directions = BillingValueFragmentDirections.actionGlobalUnlockSessionFragment()
        navController.navigate(directions, navOptions {
            popUpTo(R.id.homeFragment) {
                inclusive = true
            }
        })
    }

    fun navigateToChooseInstallments(title: String, billingValue: BigDecimal, maxInstallments: Int) {
        if (navController.currentDestination == null || navController.currentDestination?.id != R.id.billingValueFragment) {
            return
        }

        val directions = BillingValueFragmentDirections.actionBillingValueFragmentToBillingInstallmentsFragment(
            title = title,
            billingValue = billingValue.toString(),
            maxInstallments = maxInstallments
        )
        navController.navigate(directions)
    }

    fun navigateToQrCode(title: String, billingValue: BigDecimal, installments: Int) {
        if (navController.currentDestination == null || navController.currentDestination?.id != R.id.billingValueFragment) {
            return
        }

        val directions =
            BillingValueFragmentDirections.actionBillingValueFragmentToBillingQrCodeFragment(
                title = title,
                billingValue = billingValue.toString(),
                installments = installments
            )
        navController.navigate(directions)
    }
}