package br.com.tln.personalcard.credenciador.model

import br.com.tln.personalcard.credenciador.entity.AccessToken
import br.com.tln.personalcard.credenciador.entity.AccountData
import br.com.tln.personalcard.credenciador.entity.Terminal

data class Account(
    val accountData: AccountData,
    val accessToken: AccessToken,
    val terminal: Terminal
) {
    val id
        get() = accountData.id

    val username
        get() = accountData.username

    val password
        get() = accountData.password

    val localPassword
        get() = accountData.localPassword

    val locked
        get() = accountData.locked
}