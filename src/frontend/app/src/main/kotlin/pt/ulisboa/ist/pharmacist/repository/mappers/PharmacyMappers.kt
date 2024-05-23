package pt.ulisboa.ist.pharmacist.repository.mappers

import pt.ulisboa.ist.pharmacist.domain.medicines.Medicine
import pt.ulisboa.ist.pharmacist.domain.medicines.MedicineStock
import pt.ulisboa.ist.pharmacist.domain.pharmacies.Location
import pt.ulisboa.ist.pharmacist.domain.pharmacies.Pharmacy
import pt.ulisboa.ist.pharmacist.repository.local.medicines.PharmacyMedicineFlatEntity
import pt.ulisboa.ist.pharmacist.repository.local.pharmacies.PharmacyEntity
import pt.ulisboa.ist.pharmacist.repository.remote.pharmacies.PharmacyWithUserDataDto

fun PharmacyWithUserDataDto.toPharmacyEntity() = PharmacyEntity(
    pharmacyId = pharmacy.pharmacyId,
    name = pharmacy.name,
    latitude = pharmacy.location.lat,
    longitude = pharmacy.location.lon,
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
    location = Location(lat = latitude, lon = longitude),
    pictureUrl = pictureUrl,
    globalRating = globalRating,
    numberOfRatings = numberOfRatings,
    userRating = userRating,
    userMarkedAsFavorite = userMarkedAsFavorite,
    userFlagged = userFlagged
)

fun PharmacyMedicineFlatEntity.toMedicineStock() = MedicineStock(
    Medicine(
        medicineId = medicineId,
        name = name,
        description = description,
        boxPhotoUrl = boxPhotoUrl
    ),
    stock = stock ?: 0
)