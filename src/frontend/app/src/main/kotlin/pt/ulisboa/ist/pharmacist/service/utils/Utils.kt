package pt.ulisboa.ist.pharmacist.service.utils

import kotlinx.coroutines.runBlocking

fun runNewBlocking(function: suspend () -> Unit) {
    Thread {
        runBlocking {
            function()
        }
    }.start()
}