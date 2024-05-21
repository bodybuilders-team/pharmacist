package pt.ulisboa.ist.pharmacist.repository

import pt.ulisboa.ist.pharmacist.repository.local.medicines.MedicineDao
import pt.ulisboa.ist.pharmacist.repository.network.services.medicines.MedicinesService
import pt.ulisboa.ist.pharmacist.repository.remote.medicines.MedicineApi
import javax.inject.Inject

class MedicinesRepository @Inject constructor(
    val medicinesNetworkDataSource: MedicineApi,
    val medicinesLocalDataSource: MedicineDao
) {
    // TODO: Implement the methods
}