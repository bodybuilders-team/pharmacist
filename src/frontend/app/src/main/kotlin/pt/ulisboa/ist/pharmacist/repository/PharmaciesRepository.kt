package pt.ulisboa.ist.pharmacist.repository

import pt.ulisboa.ist.pharmacist.repository.local.pharmacies.PharmacyDao
import pt.ulisboa.ist.pharmacist.repository.network.services.pharmacies.PharmaciesService
import javax.inject.Inject

class PharmaciesRepository @Inject constructor(
    private val pharmaciesNetworkDataSource: PharmaciesService,
    private val pharmaciesLocalDataSource: PharmacyDao
) {
    // TODO: Implement the method
}