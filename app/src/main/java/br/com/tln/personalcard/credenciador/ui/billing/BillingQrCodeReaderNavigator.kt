package br.com.tln.personalcard.credenciador.ui.billing

import androidx.navigation.navOptions
import br.com.tln.personalcard.credenciador.R
import br.com.tln.personalcard.credenciador.core.SessionRequiredBaseNavigator
import br.com.tln.personalcard.credenciador.entity.PaymentMethod
import br.com.tln.personalcard.credenciador.model.CardPayment
import java.math.BigDecimal
import javax.inject.Inject

class BillingQrCodeReaderNavigator @Inject constructor() : SessionRequiredBaseNavigator() {
    override fun navigateToWelcome() {
        if (navController.currentDestination == null || navController.currentDestination?.id != R.id.billingQrCodeReaderFragment) {
            return
        }

        val directions = BillingQrCodeReaderFragmentDirections.actionGlobalWelcomeFragment()
        navController.navigate(directions, navOptions {
            popUpTo(R.id.homeFragment) {
                inclusive = true
            }
        })
    }

    override fun navigateToLogin() {
        if (navController.currentDestination == null || navController.currentDestination?.id != R.id.billingQrCodeReaderFragment) {
            return
        }

        val directions = BillingQrCodeReaderFragmentDirections.actionGlobalInitializationLoginFragment()
        navController.navigate(directions, navOptions {
            popUpTo(R.id.homeFragment) {
                inclusive = true
            }
        })
    }

    override fun navigateToCreatePassword() {
        if (navController.currentDestination == null || navController.currentDestination?.id != R.id.billingQrCodeReaderFragment) {
            return
        }

        val directions = BillingQrCodeReaderFragmentDirections.actionGlobalInitializationCreatePasswordFragment()
        navController.navigate(directions, navOptions {
            popUpTo(R.id.homeFragment) {
                inclusive = true
            }
        })
    }

    override fun navigateToUnlockSession() {
        if (navController.currentDestination == null || navController.currentDestination?.id != R.id.billingQrCodeReaderFragment) {
            return
        }

        val directions = BillingQrCodeReaderFragmentDirections.actionGlobalUnlockSessionFragment()
        navController.navigate(directions, navOptions {
            popUpTo(R.id.homeFragment) {
                inclusive = true
            }
        })
    }

    fun navigateToCardPayment(paymentToken: String, qrcodeToken: String, cardType: PaymentMethod, billingValue: BigDecimal, installments: Int) {
        if (navController.currentDestination == null || navController.currentDestination?.id != R.id.billingQrCodeReaderFragment) {
            return
        }

        val directions = BillingQrCodeReaderFragmentDirections.actionBillingQrCodeReaderFragmentToBillingCardPaymentFragment(
            qrcodeToken = qrcodeToken,
            cardType = cardType.id ?: 0,
            paymentToken = paymentToken,
            billingValue = billingValue.toString(),
            installments = installments
        )
        navController.navigate(directions)
    }
}