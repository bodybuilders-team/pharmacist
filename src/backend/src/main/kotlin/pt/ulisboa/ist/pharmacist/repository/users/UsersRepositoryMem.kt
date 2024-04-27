package pt.ulisboa.ist.pharmacist.repository.users

import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.stereotype.Repository
import pt.ulisboa.ist.pharmacist.domain.users.User
import pt.ulisboa.ist.pharmacist.repository.MemDataSource
import pt.ulisboa.ist.pharmacist.repository.pharmacies.PharmaciesRepositoryMem.Companion.BANNED_PHARMACY_FLAG_THRESHOLD
import pt.ulisboa.ist.pharmacist.service.exceptions.NotFoundException
import pt.ulisboa.ist.pharmacist.service.utils.OffsetPageRequest

/**
 * Repository for the [User] entity using an in-memory data structure.
 */
@Repository
class UsersRepositoryMem(private val dataSource: MemDataSource) : UsersRepository {

    private val users = dataSource.users

    override fun create(username: String, passwordHash: String): User {
        val userId = dataSource.usersCounter.getAndIncrement()
        val user = User(
            userId = userId,
            username = username,
            passwordHash = passwordHash
        )
        users[userId] = user
        return user
    }

    override fun addFavoritePharmacy(userId: Long, pharmacyId: Long) {
        val user = users[userId] ?: throw NotFoundException("User not found")
        user.favoritePharmacies.add(dataSource.pharmacies[pharmacyId] ?: throw NotFoundException("Pharmacy not found"))
    }

    override fun removeFavoritePharmacy(userId: Long, pharmacyId: Long) {
        val user = users[userId] ?: throw NotFoundException("User not found")
        user.favoritePharmacies.remove(
            dataSource.pharmacies[pharmacyId] ?: throw NotFoundException("Pharmacy not found")
        )
    }

    override fun findByUsername(username: String): User? {
        return users.values.find { it.username == username }
    }

    override fun findById(userId: Long): User? {
        return users[userId]
    }

    override fun findAll(pageable: OffsetPageRequest): Page<User> {
        val usersList = users.values.toList()
        val fromIndex = pageable.offset.toInt()
        val toIndex = (pageable.offset + pageable.pageSize).toInt()
        val pagedList = usersList.subList(fromIndex, toIndex)
        return PageImpl(pagedList, pageable, usersList.size.toLong())
    }

    override fun findAll(): List<User> {
        return users.values.toList()
    }

    override fun delete(user: User) {
        users.remove(user.userId)
    }

    override fun count(): Long {
        return users.size.toLong()
    }

    override fun existsByUsername(username: String): Boolean {
        return users.values.any { it.username == username }
    }

    override fun findByAccessTokenHash(accessToken: String): User? {
        return users.values.find { it.accessTokens.any { token -> token.tokenHash == accessToken } }
    }

    override fun flagPharmacy(userId: Long, pharmacyId: Long) {
        val user = users[userId] ?: throw NotFoundException("User not found")
        val pharmacy = dataSource.pharmacies[pharmacyId] ?: throw NotFoundException("Pharmacy not found")
        val pharmacyCreator = users[pharmacy.creatorId] ?: throw NotFoundException("Pharmacy creator not found")

        user.flaggedPharmacies.add(pharmacy)
        pharmacy.totalFlags++
        if (dataSource.pharmacies
                .filter { it.value.creatorId == pharmacyCreator.userId }
                .count { it.value.totalFlags > BANNED_PHARMACY_FLAG_THRESHOLD } > OBNOXIOUS_USER_THRESHOLD
        )
            pharmacyCreator.suspended = true
    }

    override fun unflagPharmacy(userId: Long, pharmacyId: Long) {
        val user = users[userId] ?: throw NotFoundException("User not found")
        val pharmacy = dataSource.pharmacies[pharmacyId] ?: throw NotFoundException("Pharmacy not found")
        val pharmacyCreator = users[pharmacy.creatorId] ?: throw NotFoundException("Pharmacy creator not found")

        user.flaggedPharmacies.remove(pharmacy)
        pharmacy.totalFlags--
        if (dataSource.pharmacies
                .filter { it.value.creatorId == pharmacyCreator.userId }
                .count { it.value.totalFlags > BANNED_PHARMACY_FLAG_THRESHOLD } <= OBNOXIOUS_USER_THRESHOLD
        )
            pharmacyCreator.suspended = false
    }

    companion object {
        private const val OBNOXIOUS_USER_THRESHOLD = 3
    }
}
