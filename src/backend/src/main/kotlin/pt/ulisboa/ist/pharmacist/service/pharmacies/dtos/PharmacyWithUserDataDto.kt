package pt.ulisboa.ist.pharmacist.service.pharmacies.dtos

import pt.ulisboa.ist.pharmacist.domain.pharmacies.Pharmacy

data class PharmacyWithUserDataDto(
    val pharmacy: PharmacyDto,
    val userRating: Int?,
    val userMarkedAsFavorite: Boolean
) {
    constructor(pharmacy: Pharmacy, userRating: Int?, userMarkedAsFavorite: Boolean) : this(
        pharmacy = PharmacyDto(pharmacy),
        userRating = userRating,
        userMarkedAsFavorite = userMarkedAsFavorite
    )
}