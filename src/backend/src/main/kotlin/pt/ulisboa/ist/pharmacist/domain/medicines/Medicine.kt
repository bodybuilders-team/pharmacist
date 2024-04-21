package pt.ulisboa.ist.pharmacist.domain.medicines

/**
 * A Medicine.
 *
 * @property medicineId the medicine id
 * @property name the name of the medicine
 * @property description the description of the medicine
 * @property boxPhotoUrl the box photo url
 */
data class Medicine(
    val medicineId: Long,
    val name: String,
    val description: String,
    val boxPhotoUrl: String
)
