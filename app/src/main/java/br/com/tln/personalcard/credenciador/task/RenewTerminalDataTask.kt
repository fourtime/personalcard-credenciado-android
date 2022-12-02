package br.com.tln.personalcard.credenciador.task

import arrow.core.Either
import br.com.tln.personalcard.credenciador.TERMINAL_DATA_RENEWAL_MINUTES_INTERVAL
import br.com.tln.personalcard.credenciador.core.Resource
import br.com.tln.personalcard.credenciador.entity.Terminal
import br.com.tln.personalcard.credenciador.repository.SessionRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDateTime
import org.threeten.bp.temporal.ChronoUnit
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class RenewTerminalDataTask @Inject constructor(
    private val sessionRepository: SessionRepository
) {

    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    private var running = false

    fun start() {
        if (!running) {
            running = true

            scope.launch {
                startTask()
            }
        }
    }

    private suspend fun startTask() {
        getInitialDelayMinutes()?.let {
            delay(TimeUnit.MINUTES.toMillis(it))
        }

        do {
            renewTerminalData()
            delay(TimeUnit.MINUTES.toMillis(TERMINAL_DATA_RENEWAL_MINUTES_INTERVAL))
        } while (true)
    }

    private suspend fun getInitialDelayMinutes(): Long? {
        val terminal = sessionRepository.getTerminal() ?: return null

        val delay =
            TERMINAL_DATA_RENEWAL_MINUTES_INTERVAL - terminal.fetchTime.until(LocalDateTime.now(), ChronoUnit.MINUTES)

        return if (delay <= 0) {
            null
        } else {
            delay
        }
    }

    private suspend fun renewTerminalData() {
        val accessToken = sessionRepository.getAccessToken() ?: return
        val result = sessionRepository.getTerminalData(accessToken.formattedToken)

        if (result is Either.Left) {
            lockSession()
        } else if (result is Either.Right) {
            val resource: Resource<Terminal> = result.b

            if (resource.status == Resource.Status.SUCCESS) {
                val terminal = resource.data ?: run {
                    lockSession()
                    return
                }

                sessionRepository.update(terminal)
            } else {
                lockSession()
            }
        }
    }

    private fun lockSession() {
        GlobalScope.launch(Dispatchers.IO) {
            sessionRepository.getAccountData()?.let { accountData ->
                sessionRepository.update(accountData.copy(locked = true))
            }
        }
    }

    fun stop() {
        job.cancelChildren()
        running = false
    }
}