package br.com.tln.personalcard.credenciador.ui.billing

import androidx.navigation.navOptions
import br.com.tln.personalcard.credenciador.R
import br.com.tln.personalcard.credenciador.core.SessionRequiredBaseNavigator
import br.com.tln.personalcard.credenciador.entity.PaymentMethod
import java.math.BigDecimal
import javax.inject.Inject

class BillingQrCodeNavigator @Inject constructor() : SessionRequiredBaseNavigator() {

    override fun navigateToWelcome() {
        if (navController.currentDestination == null || navController.currentDestination?.id != R.id.billingQrCodeFragment) {
            return
        }

        val directions = BillingQrCodeFragmentDirections.actionGlobalWelcomeFragment()
        navController.navigate(directions, navOptions {
            popUpTo(R.id.homeFragment) {
                inclusive = true
            }
        })
    }

    override fun navigateToLogin() {
        if (navController.currentDestination == null || navController.currentDestination?.id != R.id.billingQrCodeFragment) {
            return
        }

        val directions = BillingQrCodeFragmentDirections.actionGlobalInitializationLoginFragment()
        navController.navigate(directions, navOptions {
            popUpTo(R.id.homeFragment) {
                inclusive = true
            }
        })
    }

    override fun navigateToCreatePassword() {
        if (navController.currentDestination == null || navController.currentDestination?.id != R.id.billingQrCodeFragment) {
            return
        }

        val directions = BillingQrCodeFragmentDirections.actionGlobalInitializationCreatePasswordFragment()
        navController.navigate(directions, navOptions {
            popUpTo(R.id.homeFragment) {
                inclusive = true
            }
        })
    }

    override fun navigateToUnlockSession() {
        if (navController.currentDestination == null || navController.currentDestination?.id != R.id.billingQrCodeFragment) {
            return
        }

        val directions = BillingQrCodeFragmentDirections.actionGlobalUnlockSessionFragment()
        navController.navigate(directions, navOptions {
            popUpTo(R.id.homeFragment) {
                inclusive = true
            }
        })
    }

    fun navigateToHome() {
        if (navController.currentDestination == null || navController.currentDestination?.id != R.id.billingQrCodeFragment) {
            return
        }

        navController.popBackStack(R.id.homeFragment, false)
    }

    fun navigateToCard(paymentToken: String, cardType: PaymentMethod, billingValue: BigDecimal, installments: Int) {
        if (navController.currentDestination == null || navController.currentDestination?.id != R.id.billingQrCodeFragment) {
            return
        }

        val directions = BillingQrCodeFragmentDirections.actionBillingQrCodeFragmentToBillingCardFragment(
                cardType = cardType.id ?: 0,
                paymentToken = paymentToken,
                billingValue = billingValue.toString(),
                installments = installments,
            qrcodeToken = null
        )
        navController.navigate(directions)
    }

    fun navigateToCardReader(paymentToken: String, cardType: PaymentMethod, billingValue: BigDecimal, installments: Int) {
        if (navController.currentDestination == null || navController.currentDestination?.id != R.id.billingQrCodeFragment) {
            return
        }

        val directions = BillingQrCodeFragmentDirections.actionBillingQrCodeFragmentToBillingQrCodeReaderFragment(
            paymentToken = paymentToken,
            cardType = cardType.id ?: 0,
            billingValue = billingValue.toString(),
            installments = installments
        )
        navController.navigate(directions)
    }
}