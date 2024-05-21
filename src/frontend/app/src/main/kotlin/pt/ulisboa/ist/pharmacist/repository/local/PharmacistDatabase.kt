package pt.ulisboa.ist.pharmacist.repository.local

import androidx.room.Database
import androidx.room.RoomDatabase
import pt.ulisboa.ist.pharmacist.repository.local.medicines.MedicineEntity
import pt.ulisboa.ist.pharmacist.repository.local.medicines.MedicineDao
import pt.ulisboa.ist.pharmacist.repository.local.pharmacies.PharmacyEntity
import pt.ulisboa.ist.pharmacist.repository.local.pharmacies.PharmacyDao

@Database(entities = [PharmacyEntity::class, MedicineEntity::class], version = 1)
abstract class PharmacistDatabase : RoomDatabase() {
    abstract fun pharmacyDao(): PharmacyDao
    abstract fun medicineDao(): MedicineDao
}