package pt.ulisboa.ist.pharmacist.service.pharmacies.dtos

import pt.ulisboa.ist.pharmacist.domain.pharmacies.Pharmacy
import pt.ulisboa.ist.pharmacist.domain.pharmacies.PharmacyWithUserData

data class PharmacyWithUserDataDto(
    val pharmacy: PharmacyDto,
    val userRating: Int?,
    val userMarkedAsFavorite: Boolean,
    val userFlagged: Boolean
) {
    constructor(pharmacy: Pharmacy, userRating: Int?, userMarkedAsFavorite: Boolean, userFlagged: Boolean) : this(
        pharmacy = PharmacyDto(pharmacy),
        userRating = userRating,
        userMarkedAsFavorite = userMarkedAsFavorite,
        userFlagged = userFlagged
    )

    constructor(pharmacyWithUserData: PharmacyWithUserData) : this(
        pharmacy = PharmacyDto(pharmacyWithUserData.pharmacy),
        userRating = pharmacyWithUserData.userRating,
        userMarkedAsFavorite = pharmacyWithUserData.userMarkedAsFavorite,
        userFlagged = pharmacyWithUserData.userFlagged
    )
}