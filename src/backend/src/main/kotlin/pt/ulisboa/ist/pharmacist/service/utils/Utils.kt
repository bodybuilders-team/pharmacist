package pt.ulisboa.ist.pharmacist.service.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking

fun runNewBlocking(block: suspend CoroutineScope.() -> Unit) {
    Thread {
        runBlocking {
            block()
        }
    }.start()
}