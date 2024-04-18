package pt.ulisboa.ist.pharmacist.domain.pharmacies

import pt.ulisboa.ist.pharmacist.domain.medicines.Medicine

data class MedicineStock (
    val medicine: Medicine,
    val stock: Long
)
