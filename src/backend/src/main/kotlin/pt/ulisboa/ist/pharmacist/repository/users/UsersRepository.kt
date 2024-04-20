package pt.ulisboa.ist.pharmacist.repository.users

import org.springframework.data.domain.Page
import pt.ulisboa.ist.pharmacist.domain.users.User
import pt.ulisboa.ist.pharmacist.service.utils.OffsetPageRequest

/**
 * Repository for the [User] entity.
 */
interface UsersRepository {

    fun create(userId: String, username: String, email: String, passwordHash: String): User

    fun addFavoritePharmacy(userId: String, pharmacyId: Long)

    fun removeFavoritePharmacy(userId: String, pharmacyId: Long)

    fun findByUsername(username: String): User?

    fun findByEmail(email: String): User?

    fun findById(id: String): User?

    fun findAll(pageable: OffsetPageRequest): Page<User>

    fun delete(user: User)

    fun count(): Long

    fun existsByUsername(username: String): Boolean

    fun existsByEmail(email: String): Boolean

    /*fun findByUserAndTokenHash(user: User, tokenHash: String): User?*/
}
