package pt.ulisboa.ist.pharmacist.repository.users

import org.springframework.data.domain.Page
import pt.ulisboa.ist.pharmacist.domain.users.User
import pt.ulisboa.ist.pharmacist.service.utils.OffsetPageRequest

/**
 * Repository for the [User] entity.
 */
interface UsersRepository {

    fun create(username: String, passwordHash: String): User

    fun findByUsername(username: String): User?

    fun findById(userId: Long): User?

    fun findAll(pageable: OffsetPageRequest): Page<User>

    fun findAll(): List<User>

    fun delete(user: User)

    fun count(): Long

    fun existsByUsername(username: String): Boolean

    fun findByAccessTokenHash(accessToken: String): User?

    fun addFavoritePharmacy(userId: Long, pharmacyId: Long)

    fun removeFavoritePharmacy(userId: Long, pharmacyId: Long)

    fun flagPharmacy(userId: Long, pharmacyId: Long)

    fun unflagPharmacy(userId: Long, pharmacyId: Long)
}
