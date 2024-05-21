package pt.ulisboa.ist.pharmacist.repository.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import pt.ulisboa.ist.pharmacist.repository.local.medicines.MedicineDao
import pt.ulisboa.ist.pharmacist.repository.local.medicines.MedicineEntity
import pt.ulisboa.ist.pharmacist.repository.local.medicines.PharmacyMedicineEntity
import pt.ulisboa.ist.pharmacist.repository.local.medicines.PharmacyMedicineFlatEntity
import pt.ulisboa.ist.pharmacist.repository.local.pharmacies.PharmacyConverters
import pt.ulisboa.ist.pharmacist.repository.local.pharmacies.PharmacyDao
import pt.ulisboa.ist.pharmacist.repository.local.pharmacies.PharmacyEntity

@Database(
    entities = [PharmacyEntity::class, MedicineEntity::class, PharmacyMedicineEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(PharmacyConverters::class)
abstract class PharmacistDatabase : RoomDatabase() {
    abstract fun pharmacyDao(): PharmacyDao
    abstract fun medicineDao(): MedicineDao
}