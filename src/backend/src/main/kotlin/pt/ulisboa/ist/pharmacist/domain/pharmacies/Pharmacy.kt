package pt.ulisboa.ist.pharmacist.domain.pharmacies

/**
 * A Pharmacy.
 *
 * @property pharmacyId the pharmacy id
 * @property name the name of the pharmacy
 * @property location the location of the pharmacy
 * @property pictureUrl the url of the picture of the pharmacy
 * @property medicines the medicines of the pharmacy
 */
data class Pharmacy(
    val pharmacyId: Long,
    val name: String,
    val location: Location,
    val pictureUrl: String,
    val medicines: MutableList<MedicineStock> = mutableListOf()
)