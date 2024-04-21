package pt.ulisboa.ist.pharmacist.domain.medicines

/**
 * A medicine.
 *
 * @property medicineId the id of the medicine
 * @property name the name of the medicine
 * @property description the description of the medicine
 * @property boxPhotoUrl the url of the photo of the medicine box
 */
data class Medicine(
    var medicineId: Long,
    val name: String,
    val description: String,
    val boxPhotoUrl: String
)
