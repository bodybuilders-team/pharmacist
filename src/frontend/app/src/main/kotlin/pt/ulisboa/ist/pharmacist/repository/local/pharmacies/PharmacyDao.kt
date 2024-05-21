package pt.ulisboa.ist.pharmacist.repository.local.pharmacies

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface PharmacyDao {

    @Upsert
    suspend fun upsertPharmacies(pharmacies: List<PharmacyEntity>)

    @Query(
        """
        SELECT pharmacies.pharmacyId, pharmacies.name, pharmacies.location, pharmacies.pictureUrl, pharmacies.globalRating, pharmacies.numberOfRatings,
        pharmacies.userRating, pharmacies.userMarkedAsFavorite, pharmacies.userFlagged
        FROM pharmacies
        JOIN pharmacy_medicine
        ON pharmacies.pharmacyId = pharmacy_medicine.pharmacyId
        WHERE pharmacy_medicine.medicineId = :medicineId
    """
    )
    fun getPagingSourceByMedicineId(medicineId: Long): PagingSource<Int, PharmacyEntity>

    @Query("SELECT * FROM pharmacies")
    fun getAllPharmacies(): List<PharmacyEntity>

    @Query("SELECT * FROM pharmacies WHERE pharmacyId = :id")
    suspend fun getPharmacyById(id: Long): PharmacyEntity

    /*suspend fun listAvailableMedicines(
        pharmacyId: Long,
        limit: Long? = null,
        offset: Long? = null
    ): Flow<List<LocalMedicine>>*/

    @Query("DELETE FROM pharmacies")
    suspend fun clearAllPharmacies()

    @Query("DELETE FROM pharmacy_user")
    suspend fun clearAllPharmacyUserList()
}

