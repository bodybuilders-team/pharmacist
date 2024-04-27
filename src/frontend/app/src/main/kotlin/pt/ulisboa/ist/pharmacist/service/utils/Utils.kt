package pt.ulisboa.ist.pharmacist.service.utils

import kotlinx.coroutines.runBlocking

/**
 * Runs a blocking function in a new thread.
 *
 * @param function The function to run.
 */
fun runNewBlocking(function: suspend () -> Unit) {
    Thread {
        runBlocking {
            function()
        }
    }.start()
}