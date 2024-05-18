package pt.ulisboa.ist.pharmacist.repository.local.pharmacies

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import pt.ulisboa.ist.pharmacist.domain.pharmacies.Location

@Dao
interface PharmacyDao {

    // TODO: Implement this
    @Query("SELECT * FROM pharmacies")
    suspend fun getPharmacies(
        medicineId: Long? = null,
        location: Location? = null,
        orderBy: String? = null,
        limit: Long? = null,
        offset: Long? = null
    ): Flow<List<LocalPharmacy>>

    @Query("SELECT * FROM pharmacies WHERE pharmacyId = :id")
    suspend fun getPharmacyById(id: Long): Flow<LocalPharmacy>

    /*suspend fun listAvailableMedicines(
        pharmacyId: Long,
        limit: Long? = null,
        offset: Long? = null
    ): Flow<List<LocalMedicine>>*/

    // TODO: Finish implementing this
}

