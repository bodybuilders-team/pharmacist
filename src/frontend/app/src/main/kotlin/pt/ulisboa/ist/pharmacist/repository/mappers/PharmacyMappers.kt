package pt.ulisboa.ist.pharmacist.repository.mappers

import pt.ulisboa.ist.pharmacist.domain.pharmacies.Pharmacy
import pt.ulisboa.ist.pharmacist.repository.local.pharmacies.PharmacyEntity
import pt.ulisboa.ist.pharmacist.repository.remote.pharmacies.PharmacyWithUserDataDto

fun PharmacyWithUserDataDto.toPharmacyEntity() = PharmacyEntity(
    pharmacyId = pharmacy.pharmacyId,
    name = pharmacy.name,
    location = pharmacy.location,
    pictureUrl = pharmacy.pictureUrl,
    globalRating = pharmacy.globalRating,
    numberOfRatings = pharmacy.numberOfRatings,
    userRating = userRating,
    userMarkedAsFavorite = userMarkedAsFavorite,
    userFlagged = userFlagged
)

fun PharmacyEntity.toPharmacy() = Pharmacy(
    pharmacyId = pharmacyId,
    name = name,
    location = location,
    pictureUrl = pictureUrl,
    globalRating = globalRating,
    numberOfRatings = numberOfRatings,
    userRating = userRating,
    userMarkedAsFavorite = userMarkedAsFavorite,
    userFlagged = userFlagged
)