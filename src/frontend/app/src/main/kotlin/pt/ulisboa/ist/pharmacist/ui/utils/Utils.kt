package pt.ulisboa.ist.pharmacist.ui.utils

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

@OptIn(ExperimentalContracts::class)
fun <T> assertNotNull(actual: T) {
    contract { returns() implies (actual != null) }
}