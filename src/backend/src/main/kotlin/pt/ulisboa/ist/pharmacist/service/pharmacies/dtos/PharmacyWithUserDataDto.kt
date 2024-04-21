package pt.ulisboa.ist.pharmacist.service.pharmacies.dtos

import pt.ulisboa.ist.pharmacist.domain.pharmacies.Pharmacy
import pt.ulisboa.ist.pharmacist.domain.users.UserPharmacyRating

data class PharmacyWithUserDataDto(
    val pharmacy: PharmacyDto,
    val userRating: UserPharmacyRatingDto?,
    val userMarkedAsFavorite: Boolean
) {
    constructor(pharmacy: Pharmacy, userRating: UserPharmacyRating?, userMarkedAsFavorite: Boolean) : this(
        pharmacy = PharmacyDto(pharmacy),
        userRating = userRating?.let { UserPharmacyRatingDto(it) },
        userMarkedAsFavorite = userMarkedAsFavorite
    )
}