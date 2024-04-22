package pt.ulisboa.ist.pharmacist.http.controllers.pharmacies.models

import pt.ulisboa.ist.pharmacist.service.pharmacies.dtos.PharmacyWithUserDataDto

/**
 * The model of a pharmacy with user data.
 *
 * @property pharmacy the pharmacy
 * @property userRating the user rating
 * @property userMarkedAsFavorite if the user marked the pharmacy as favorite
 */
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