package pt.ulisboa.ist.pharmacist.repository.remote.medicines

import pt.ulisboa.ist.pharmacist.domain.medicines.Medicine

data class GetMedicineOutputDto(
    val medicine: Medicine,
    val notificationsActive: Boolean
)