package pt.ulisboa.ist.pharmacist.domain.medicinenotifications

import pt.ulisboa.ist.pharmacist.domain.pharmacies.MedicineStock

data class MedicineNotification(
    val medicineStock: MedicineStock,
    val pharmacyId: Long,
)