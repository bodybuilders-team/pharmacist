package pt.ulisboa.ist.pharmacist.http.controllers.pharmacies

import pt.ulisboa.ist.pharmacist.service.pharmacies.dtos.PharmacyWithUserDataDto

data class PharmacyWithUserDataModel(
    val pharmacy: PharmacyModel,
    val userRating: Int?,
    val userMarkedAsFavorite: Boolean
) {
    constructor(pharmacy: PharmacyWithUserDataDto) : this(
        pharmacy = PharmacyModel(pharmacy.pharmacy),
        userRating = pharmacy.userRating,
        userMarkedAsFavorite = pharmacy.userMarkedAsFavorite
    )
}