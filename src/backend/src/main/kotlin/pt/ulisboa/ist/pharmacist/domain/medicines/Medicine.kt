package pt.ulisboa.ist.pharmacist.domain.medicines

import pt.ulisboa.ist.pharmacist.domain.exceptions.InvalidMedicineException

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
) {
    init {
        if (medicineId < 0)
            throw InvalidMedicineException("Medicine id must be a positive number.")

        if (name.length !in MIN_MEDICINE_NAME_LENGTH..MAX_MEDICINE_NAME_LENGTH)
            throw InvalidMedicineException(
                "Medicine name must be between $MIN_MEDICINE_NAME_LENGTH and $MAX_MEDICINE_NAME_LENGTH characters long."
            )

        if (description.length !in MIN_MEDICINE_DESCRIPTION_LENGTH..MAX_MEDICINE_DESCRIPTION_LENGTH)
            throw InvalidMedicineException(
                "Medicine description must be between $MIN_MEDICINE_DESCRIPTION_LENGTH and $MAX_MEDICINE_DESCRIPTION_LENGTH characters long."
            )

        if (boxPhotoUrl.isNotEmpty() && !URL_REGEX.toRegex().matches(boxPhotoUrl))
            throw InvalidMedicineException("Box photo URL must be a valid URL.")
    }

    companion object {
        private const val MIN_MEDICINE_NAME_LENGTH = 3
        private const val MAX_MEDICINE_NAME_LENGTH = 128
        private const val MIN_MEDICINE_DESCRIPTION_LENGTH = 3
        private const val MAX_MEDICINE_DESCRIPTION_LENGTH = 1024

        private const val URL_REGEX = "^(http|https)://.*$"
    }
}
