package pt.ulisboa.ist.pharmacist.service.medicines

import pt.ulisboa.ist.pharmacist.domain.pharmacies.Location
import pt.ulisboa.ist.pharmacist.service.medicines.dtos.GetMedicinesWithClosestPharmacyOutputDto
import pt.ulisboa.ist.pharmacist.service.pharmacies.dtos.MedicineDto

/**
 * Service that handles the business logic of the medicines.
 */
interface MedicinesService {

    /**
     * Gets the medicines.
     *
     * @param substring the substring to search for in the name of the medicines
     * @param location the location to get the closest pharmacy to
     * @param offset the offset of the medicines to get
     * @param limit the limit of the medicines to get
     */
    fun getMedicinesWithClosestPharmacy(
        substring: String,
        location: Location?,
        offset: Int,
        limit: Int
    ): GetMedicinesWithClosestPharmacyOutputDto

    /**
     * Creates a new medicine.
     *
     * @param name the name of the medicine
     * @param description the description of the medicine
     * @param boxPhotoUrl the box photo of the medicine
     * @return the medicine created
     */
    fun createMedicine(name: String, description: String, boxPhotoUrl: String): MedicineDto

    /**
     * Gets a medicine by its id.
     *
     * @param medicineId the id of the medicine
     * @return the medicine
     */
    fun getMedicineById(medicineId: Long): MedicineDto
}
