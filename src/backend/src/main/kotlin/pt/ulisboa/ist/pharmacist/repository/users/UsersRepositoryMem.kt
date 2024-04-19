package pt.ulisboa.ist.pharmacist.repository.users

import org.springframework.data.domain.Page
import org.springframework.stereotype.Repository
import pt.ulisboa.ist.pharmacist.domain.users.User
import pt.ulisboa.ist.pharmacist.repository.MemDataSource
import pt.ulisboa.ist.pharmacist.service.utils.OffsetPageRequest
import java.util.UUID

/**
 * Repository for the [User] entity using an in-memory data structure.
 */
@Repository
class UsersRepositoryMem(dataSource: MemDataSource) : UsersRepository {

    private val users = dataSource.users

    override fun create(username: String, email: String, passwordHash: String): User {
        val id = UUID.randomUUID().toString()
        val user = User(id, username, email, passwordHash)
        users[id] = user
        return user
    }

    override fun findByUsername(username: String): User? {
        return users.values.find { it.username == username }
    }

    override fun findByEmail(email: String): User? {
        return users.values.find { it.email == email }
    }

    override fun findById(id: String): User? {
        return users[id]
    }

    override fun findAll(pageable: OffsetPageRequest): Page<User> {
        val usersList = users.values.toList()
        val fromIndex = pageable.offset.toInt()
        val toIndex = (pageable.offset + pageable.pageSize).toInt()
        val pagedList = usersList.subList(fromIndex, toIndex)
        return Page(pagedList, pageable, usersList.size.toLong())
    }

    override fun delete(user: User) {
        users.remove(user.id)
    }

    override fun count(): Long {
        return users.size.toLong()
    }

    override fun existsByUsername(username: String): Boolean {
        return users.values.any { it.username == username }
    }

    override fun existsByEmail(email: String): Boolean {
        return users.values.any { it.email == email }
    }

    override fun findByUserAndTokenHash(user: User, tokenHash: String): User? {
        return users.values.find { it == user && it.tokenHash == tokenHash }
    }

}
