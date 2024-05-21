package pt.ulisboa.ist.pharmacist.repository

import pt.ulisboa.ist.pharmacist.repository.local.pharmacies.PharmacyDao
import pt.ulisboa.ist.pharmacist.repository.remote.pharmacies.PharmacyApi
import javax.inject.Inject

class PharmaciesRepository @Inject constructor(
    private val pharmaciesNetworkDataSource: PharmacyApi,
    private val pharmaciesLocalDataSource: PharmacyDao
) {
    // TODO: Implement the method
}