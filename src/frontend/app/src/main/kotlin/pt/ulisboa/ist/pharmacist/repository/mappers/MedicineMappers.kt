package pt.ulisboa.ist.pharmacist.repository.mappers

import pt.ulisboa.ist.pharmacist.domain.medicines.Medicine
import pt.ulisboa.ist.pharmacist.domain.medicines.MedicineWithClosestPharmacy
import pt.ulisboa.ist.pharmacist.domain.medicines.MedicineWithNotificationStatus
import pt.ulisboa.ist.pharmacist.repository.local.medicines.MedicineEntity
import pt.ulisboa.ist.pharmacist.repository.local.medicines.MedicineWithClosestPharmacyEntity
import pt.ulisboa.ist.pharmacist.repository.local.medicines.PharmacyMedicineEntity
import pt.ulisboa.ist.pharmacist.repository.remote.medicines.GetMedicineOutputDto
import pt.ulisboa.ist.pharmacist.repository.remote.pharmacies.MedicineStockDto

fun GetMedicineOutputDto.toMedicineEntity() = MedicineEntity(
    medicineId = medicine.medicineId,
    name = medicine.name,
    description = medicine.description,
    boxPhotoUrl = medicine.boxPhotoUrl,
    notificationsActive = notificationsActive
)

fun MedicineStockDto.toPharmacyMedicineEntity(pharmacyId: Long) = PharmacyMedicineEntity(
    medicineId = medicine.medicineId,
    pharmacyId = pharmacyId,
    stock = stock
)

fun MedicineEntity.toMedicine() = Medicine(
    medicineId = medicineId,
    name = name,
    description = description,
    boxPhotoUrl = boxPhotoUrl
)

fun MedicineEntity.toMedicineWithClosestPharmacy() = MedicineWithClosestPharmacy(
    medicineId = medicineId,
    name = name,
    description = description,
    boxPhotoUrl = boxPhotoUrl,
    closestPharmacyId = null,
    closestPharmacyName = null
)

fun MedicineWithClosestPharmacyEntity.toMedicineWithClosestPharmacy() = MedicineWithClosestPharmacy(
    medicineId = medicineId,
    name = name,
    description = description,
    boxPhotoUrl = boxPhotoUrl,
    closestPharmacyId = closestPharmacyId,
    closestPharmacyName = closestPharmacyName
)

fun MedicineEntity.toMedicineWithNotificationStatus() = MedicineWithNotificationStatus(
    medicineId = medicineId,
    name = name,
    description = description,
    boxPhotoUrl = boxPhotoUrl,
    notificationsActive = notificationsActive
)