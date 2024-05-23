package pt.ulisboa.ist.pharmacist.repository.local.medicines

import androidx.room.Entity

@Entity(tableName = "pharmacy_medicine", primaryKeys = ["pharmacyId", "medicineId"])
data class PharmacyMedicineEntity(
    val pharmacyId: Long,
    val medicineId: Long,
    val stock: Long?
)
