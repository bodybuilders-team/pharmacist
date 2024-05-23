package pt.ulisboa.ist.pharmacist.repository.remote.pharmacies

import pt.ulisboa.ist.pharmacist.domain.medicines.Medicine
import pt.ulisboa.ist.pharmacist.domain.pharmacies.Location

/**
 * A pharmacy.
 *
 * @property pharmacyId the id of the pharmacy
 * @property name the name of the pharmacy
 * @property location the location of the pharmacy
 * @property pictureUrl the url of the picture of the pharmacy
 */
data class PharmacyDto(
    var pharmacyId: Long,
    val name: String,
    val location: Location,
    val pictureUrl: String,
    val globalRating: Double?,
    val numberOfRatings: Array<Int>
)

data class GetPharmaciesOutputDto(
    val pharmacies: List<PharmacyWithUserDataDto>
)

data class ChangeMedicineStockDto(
    val operation: MedicineStockOperation,
    val quantity: Long
)

enum class MedicineStockOperation {
    ADD,
    REMOVE
}

data class AddPharmacyOutputDto(
    val pharmacyId: Long
)

data class PharmacyWithUserDataDto(
    val pharmacy: PharmacyDto,
    val userRating: Int?,
    val userMarkedAsFavorite: Boolean,
    val userFlagged: Boolean
)

data class ListAvailableMedicinesOutputDto(
    val medicines: List<MedicineStockDto>
)

data class MedicineStockDto(
    val medicine: Medicine,
    val stock: Long
)