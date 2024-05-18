package pt.ulisboa.ist.pharmacist.repository.local

import androidx.room.Database
import androidx.room.RoomDatabase
import pt.ulisboa.ist.pharmacist.repository.local.medicines.LocalMedicine
import pt.ulisboa.ist.pharmacist.repository.local.medicines.MedicineDao
import pt.ulisboa.ist.pharmacist.repository.local.pharmacies.LocalPharmacy
import pt.ulisboa.ist.pharmacist.repository.local.pharmacies.PharmacyDao

@Database(entities = [LocalPharmacy::class, LocalMedicine::class], version = 1)
abstract class PharmacistDatabase : RoomDatabase() {
    abstract fun pharmacyDao(): PharmacyDao
    abstract fun medicineDao(): MedicineDao
}