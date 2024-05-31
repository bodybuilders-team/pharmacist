package pt.ulisboa.ist.pharmacist.repository.local.pharmacies

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import pt.ulisboa.ist.pharmacist.repository.local.medicines.PharmacyMedicineEntity
import pt.ulisboa.ist.pharmacist.repository.local.medicines.PharmacyMedicineFlatEntity

@Dao
interface PharmacyDao {

    @Upsert
    suspend fun upsertPharmacies(pharmacies: List<PharmacyEntity>)

    @Upsert
    suspend fun upsertPharmacy(pharmacy: PharmacyEntity)

    @Upsert
    suspend fun upsertPharmacyMedicineList(pharmacyMedicineList: List<PharmacyMedicineEntity>)

    @Upsert
    suspend fun upsertPharmacyMedicine(pharmacyMedicine: PharmacyMedicineEntity)

    @Upsert(entity = PharmacyMedicineEntity::class)
    suspend fun upsertPharmacyMedicineNoStockList(
        pharmacyMedicineNoStockList: List<PharmacyMedicineNoStockEntity>
    )

    data class PharmacyMedicineNoStockEntity(
        val pharmacyId: Long,
        val medicineId: Long
    )

    @Query("UPDATE pharmacies SET userRating = :userRating WHERE pharmacyId = :pharmacyId")
    suspend fun updateUserRating(pharmacyId: Long, userRating: Int)

    @Update(entity = PharmacyEntity::class)
    suspend fun updateGlobalRating(pharmacyEntityGlobalRating: PharmacyEntityGlobalRating)

    suspend fun updateGlobalRating(
        pharmacyId: Long,
        globalRating: Double,
        numberOfRatings: Array<Int>
    ) {
        updateGlobalRating(PharmacyEntityGlobalRating(pharmacyId, globalRating, numberOfRatings))
    }

    data class PharmacyEntityGlobalRating(
        val pharmacyId: Long,
        val globalRating: Double,
        val numberOfRatings: Array<Int>
    )

    @Query("UPDATE pharmacies SET userFlagged = :isFlagged WHERE pharmacyId = :pharmacyId")
    suspend fun updateUserFlagged(pharmacyId: Long, isFlagged: Boolean)

    @Query("UPDATE pharmacies SET userMarkedAsFavorite = :isFavorite WHERE pharmacyId = :pharmacyId")
    suspend fun updateUserMarkedAsFavorite(pharmacyId: Long, isFavorite: Boolean)

    @Query(
        """
        SELECT pharmacies.pharmacyId, pharmacies.name, pharmacies.latitude, pharmacies.longitude, pharmacies.pictureUrl, pharmacies.globalRating, pharmacies.numberOfRatings,
        pharmacies.userRating, pharmacies.userMarkedAsFavorite, pharmacies.userFlagged
        FROM pharmacies
        JOIN pharmacy_medicine
        ON pharmacies.pharmacyId = pharmacy_medicine.pharmacyId
        WHERE pharmacy_medicine.medicineId = :medicineId
    """
    )
    fun pagingSourceByMedicineId(medicineId: Long): PagingSource<Int, PharmacyEntity>

    @Query(
        """
        SELECT medicines.medicineId, medicines.name, medicines.description, medicines.boxPhotoUrl, medicines.notificationsActive, pharmacy_medicine.stock
        FROM medicines
        INNER JOIN pharmacy_medicine ON medicines.medicineId = pharmacy_medicine.medicineId
        WHERE pharmacy_medicine.pharmacyId = :pharmacyId
        """
    )
    fun pagingSourcePharmacyMedicineByPharmacyId(pharmacyId: Long): PagingSource<Int, PharmacyMedicineFlatEntity>

    @Query("SELECT * FROM pharmacies")
    suspend fun getAllPharmacies(): List<PharmacyEntity>

    @Query("SELECT * FROM pharmacies WHERE pharmacyId = :id")
    suspend fun getPharmacyById(id: Long): PharmacyEntity

    @Query("DELETE FROM pharmacies")
    suspend fun clearAllPharmacies()

    @Query(
        """
        DELETE FROM pharmacies
        WHERE pharmacyId IN (
            SELECT pharmacyId FROM pharmacy_medicine WHERE medicineId = :medicineId
        )
    """
    )
    suspend fun clearAllPharmaciesByMedicineId(medicineId: Long)

    @Query("DELETE FROM pharmacy_medicine")
    suspend fun clearAllPharmacyMedicine()

    @Query("DELETE FROM pharmacy_medicine WHERE pharmacyId = :pharmacyId")
    suspend fun clearPharmacyMedicineByPharmacyId(pharmacyId: Long)
}

