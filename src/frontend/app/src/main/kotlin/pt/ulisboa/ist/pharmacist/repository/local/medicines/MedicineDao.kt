package pt.ulisboa.ist.pharmacist.repository.local.medicines

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert

@Dao
interface MedicineDao {

    @Upsert
    suspend fun upsertMedicines(medicines: List<MedicineEntity>)

    @Upsert
    suspend fun upsertPharmacyMedicineList(pharmacyMedicineList: List<PharmacyMedicineEntity>)

    @Update(entity = MedicineEntity::class)
    fun updateMedicineNotificationStatus(
        medicineId: Long,
        notificationsActive: Boolean
    ): MedicineEntity

    @Query("""
        SELECT medicines.medicineId, medicines.name, medicines.description, medicines.boxPhotoUrl, medicines.closestPharmacy, medicines.notificationsActive, pharmacy_medicine.stock
        FROM medicines
        INNER JOIN pharmacy_medicine ON medicines.medicineId = pharmacy_medicine.medicineId
        WHERE pharmacy_medicine.pharmacyId = :pharmacyId
        """)
    fun getPharmacyMedicineByPharmacyId(pharmacyId: Long): List<PharmacyMedicineFlatEntity>

    @Query("SELECT * FROM medicines")
    fun pagingSource(): PagingSource<Int, MedicineEntity>

    @Query("SELECT * FROM medicines WHERE name LIKE :query")
    fun pagingSource(query: String): PagingSource<Int, MedicineEntity>

    @Query("""
        SELECT * FROM medicines
        WHERE medicineId NOT IN (
            SELECT medicineId FROM pharmacy_medicine WHERE pharmacyId = :pharmacyId
        ) AND name LIKE :query
    """)
    fun pagingSourceMedicineNotInPharmacy(query: String, pharmacyId: Long): PagingSource<Int, MedicineEntity>

    @Query("SELECT * FROM medicines WHERE medicineId = :medicineId")
    fun getMedicineById(medicineId: Long): MedicineEntity

    @Query("DELETE FROM medicines")
    suspend fun clearAllMedicines()

    @Query("DELETE FROM pharmacy_medicine")
    suspend fun clearAllPharmacyMedicine()
}