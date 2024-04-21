package pt.ulisboa.ist.pharmacist.http.controllers.pharmacies

import pt.ulisboa.ist.pharmacist.http.controllers.pharmacies.pharmacyRate.PharmacyRatingModel
import pt.ulisboa.ist.pharmacist.service.pharmacies.dtos.PharmacyWithUserDataDto

data class PharmacyWithUserDataModel(
    val pharmacy: PharmacyModel,
    val userRating: PharmacyRatingModel?,
    val userMarkedAsFavorite: Boolean
) {
    constructor(pharmacy: PharmacyWithUserDataDto) : this(
        pharmacy = PharmacyModel(pharmacy.pharmacy),
        userRating = pharmacy.userRating?.let {
            PharmacyRatingModel(it)
        },
        userMarkedAsFavorite = pharmacy.userMarkedAsFavorite
    )
}