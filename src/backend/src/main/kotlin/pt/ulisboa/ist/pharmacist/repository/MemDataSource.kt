package pt.ulisboa.ist.pharmacist.repository

import org.springframework.stereotype.Component
import pt.ulisboa.ist.pharmacist.domain.medicines.Medicine
import pt.ulisboa.ist.pharmacist.domain.pharmacies.Pharmacy
import pt.ulisboa.ist.pharmacist.domain.users.User

@Component
class MemDataSource {

    val pharmacies = mutableMapOf<Long, Pharmacy>()
    val medicines = mutableMapOf<Long, Medicine>()

    val users = mutableMapOf<String, User>()
}