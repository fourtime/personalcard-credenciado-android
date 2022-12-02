package br.com.tln.personalcard.credenciador.ui.transactions

import androidx.navigation.navOptions
import br.com.tln.personalcard.credenciador.R
import br.com.tln.personalcard.credenciador.core.SessionRequiredBaseNavigator
import br.com.tln.personalcard.credenciador.extensions.format
import br.com.tln.personalcard.credenciador.model.TransactionDailySummary
import javax.inject.Inject

class TransactionsNavigator @Inject constructor() : SessionRequiredBaseNavigator() {

    override fun navigateToWelcome() {
        if (navController.currentDestination == null || navController.currentDestination?.id != R.id.transactionsFragment) {
            return
        }

        val directions = TransactionsFragmentDirections.actionGlobalWelcomeFragment()
        navController.navigate(directions, navOptions {
            popUpTo(R.id.homeFragment) {
                inclusive = true
            }
        })
    }

    override fun navigateToLogin() {
        if (navController.currentDestination == null || navController.currentDestination?.id != R.id.transactionsFragment) {
            return
        }

        val directions = TransactionsFragmentDirections.actionGlobalInitializationLoginFragment()
        navController.navigate(directions, navOptions {
            popUpTo(R.id.homeFragment) {
                inclusive = true
            }
        })
    }

    override fun navigateToCreatePassword() {
        if (navController.currentDestination == null || navController.currentDestination?.id != R.id.transactionsFragment) {
            return
        }

        val directions = TransactionsFragmentDirections.actionGlobalInitializationCreatePasswordFragment()
        navController.navigate(directions, navOptions {
            popUpTo(R.id.homeFragment) {
                inclusive = true
            }
        })
    }

    override fun navigateToUnlockSession() {
        if (navController.currentDestination == null || navController.currentDestination?.id != R.id.transactionsFragment) {
            return
        }

        val directions = TransactionsFragmentDirections.actionGlobalUnlockSessionFragment()
        navController.navigate(directions, navOptions {
            popUpTo(R.id.homeFragment) {
                inclusive = true
            }
        })
    }

    fun navigateToDayTransactions(dailySummary: TransactionDailySummary) {
        if (navController.currentDestination == null || navController.currentDestination?.id != R.id.transactionsFragment) {
            return
        }

        val directions =
            TransactionsFragmentDirections.actionTransactionsFragmentToDayTransactionsFragment(
                dailySummary = dailySummary,
                label = "${dailySummary.date.format("dd' de '")} ${dailySummary.date.format("MMMM").capitalize()}"
            )
        navController.navigate(directions)
    }
}