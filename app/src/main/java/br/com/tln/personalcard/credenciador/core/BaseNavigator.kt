package br.com.tln.personalcard.credenciador.core

import androidx.navigation.NavController

abstract class BaseNavigator : Navigator {

    lateinit var navController: NavController
}