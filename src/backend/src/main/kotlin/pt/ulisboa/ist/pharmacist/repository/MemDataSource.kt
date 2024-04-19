package pt.ulisboa.ist.pharmacist.repository

import org.springframework.stereotype.Component
import pt.ulisboa.ist.pharmacist.domain.medicines.Medicine
import pt.ulisboa.ist.pharmacist.domain.pharmacies.Pharmacy
import pt.ulisboa.ist.pharmacist.domain.users.AccessToken
import pt.ulisboa.ist.pharmacist.domain.users.User
import java.util.concurrent.atomic.AtomicLong

@Component
class MemDataSource {

    val pharmacies = mutableMapOf<Long, Pharmacy>()
    val medicines = mutableMapOf<Long, Medicine>()

    val users = mutableMapOf<String, User>()
    val accessTokens = mutableListOf<AccessToken>()

    val pharmaciesCounter = AtomicLong(0)
    val medicinesCounter = AtomicLong(0)
}