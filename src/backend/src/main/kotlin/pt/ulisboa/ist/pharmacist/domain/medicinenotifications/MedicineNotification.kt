package pt.ulisboa.ist.pharmacist.domain.medicinenotifications

import pt.ulisboa.ist.pharmacist.domain.pharmacies.MedicineStock
import pt.ulisboa.ist.pharmacist.domain.pharmacies.Pharmacy

data class MedicineNotification(
    val medicineStock: MedicineStock,
    val pharmacy: Pharmacy,
)