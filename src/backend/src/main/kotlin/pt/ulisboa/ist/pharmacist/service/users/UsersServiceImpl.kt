package pt.ulisboa.ist.pharmacist.service.users

import java.util.UUID
import org.springframework.stereotype.Service
import pt.ulisboa.ist.pharmacist.domain.users.AccessToken
import pt.ulisboa.ist.pharmacist.domain.users.User
import pt.ulisboa.ist.pharmacist.repository.pharmacies.PharmaciesRepository
import pt.ulisboa.ist.pharmacist.repository.users.UsersRepository
import pt.ulisboa.ist.pharmacist.service.exceptions.AlreadyExistsException
import pt.ulisboa.ist.pharmacist.service.exceptions.InvalidArgumentException
import pt.ulisboa.ist.pharmacist.service.exceptions.InvalidLoginException
import pt.ulisboa.ist.pharmacist.service.exceptions.InvalidPaginationParamsException
import pt.ulisboa.ist.pharmacist.service.exceptions.InvalidPasswordException
import pt.ulisboa.ist.pharmacist.service.exceptions.NotFoundException
import pt.ulisboa.ist.pharmacist.service.users.dtos.UserDto
import pt.ulisboa.ist.pharmacist.service.users.dtos.UsersDto
import pt.ulisboa.ist.pharmacist.service.users.dtos.login.LoginOutputDto
import pt.ulisboa.ist.pharmacist.service.users.dtos.register.RegisterOutputDto
import pt.ulisboa.ist.pharmacist.service.users.utils.UsersOrder
import pt.ulisboa.ist.pharmacist.service.utils.HashingUtils
import pt.ulisboa.ist.pharmacist.service.utils.OffsetPageRequest

/**
 * Service that handles the business logic of the users.
 *
 * @property usersRepository the repository of the users
 * @property hashingUtils the utils for password operations
 */
@Service
class UsersServiceImpl(
    private val usersRepository: UsersRepository,
    private val pharmaciesRepository: PharmaciesRepository,
    private val hashingUtils: HashingUtils,
) : UsersService {

    override fun addFavoritePharmacy(userId: String, pharmacyId: Long) {
        usersRepository.findById(userId) ?: throw NotFoundException("User with id $userId not found")
        pharmaciesRepository.findById(pharmacyId) ?: throw NotFoundException("Pharmacy with id $pharmacyId not found")

        usersRepository.addFavoritePharmacy(userId = userId, pharmacyId = pharmacyId)
    }

    override fun removeFavoritePharmacy(userId: String, pharmacyId: Long) {
        usersRepository.findById(userId) ?: throw NotFoundException("User with id $userId not found")
        pharmaciesRepository.findById(pharmacyId) ?: throw NotFoundException("Pharmacy with id $pharmacyId not found")

        usersRepository.removeFavoritePharmacy(userId = userId, pharmacyId = pharmacyId)
    }

    override fun getUsers(offset: Int, limit: Int, orderBy: UsersOrder, ascending: Boolean): UsersDto {
        if (offset < 0) throw InvalidArgumentException("Offset must be a positive integer")
        if (limit < 0) throw InvalidArgumentException("Limit must be a positive integer")

        if (limit > MAX_USERS_LIMIT)
            throw InvalidPaginationParamsException("Limit must be less or equal than $MAX_USERS_LIMIT")

        return UsersDto(
            users = usersRepository.findAll(
                OffsetPageRequest(
                    offset = offset.toLong(),
                    limit = limit,
                    sort = orderBy.toSort(ascending)
                )
            )
                .toList()
                .map(::UserDto),
            totalCount = usersRepository.count().toInt()
        )
    }

    override fun register(username: String, email: String, password: String): RegisterOutputDto {
        if (usersRepository.existsByUsername(username = username))
            throw AlreadyExistsException("User with username $username already exists")

        if (usersRepository.existsByEmail(email = email))
            throw AlreadyExistsException("User with email $email already exists")

        if (password.length < MIN_PASSWORD_LENGTH)
            throw InvalidPasswordException("Password must be at least $MIN_PASSWORD_LENGTH characters long")

        val userId = UUID.randomUUID().toString()

        val user = usersRepository.create(
            userId = userId,
            username = username,
            email = email,
            passwordHash = hashingUtils.hashPassword(
                username = username,
                password = password
            )
        )

        val accessToken = UUID.randomUUID().toString()
        val tokenHash = hashingUtils.hashToken(token = accessToken)

        user.accessTokens.add(AccessToken(tokenHash = tokenHash))

        return RegisterOutputDto(
            userId = userId,
            accessToken = accessToken
        )
    }

    override fun login(username: String, password: String): LoginOutputDto {
        val user = usersRepository
            .findByUsername(username = username)
            ?: throw InvalidLoginException("Invalid username or password")

        if (
            !hashingUtils.checkPassword(
                username = username,
                password = password,
                passwordHash = user.passwordHash
            )
        ) throw InvalidLoginException("Invalid username or password")

        val accessToken = UUID.randomUUID().toString()
        val tokenHash = hashingUtils.hashToken(token = accessToken)

        user.accessTokens.add(AccessToken(tokenHash = tokenHash))

        return LoginOutputDto(
            userId = user.userId,
            accessToken = accessToken
        )
    }

    override fun logout(user: User, accessToken: String) {
        val tokenHash = hashingUtils.hashToken(token = accessToken)

        user.accessTokens.remove(AccessToken(tokenHash = tokenHash))
    }

    override fun getUser(userId: String): UserDto {
        val user = usersRepository
            .findById(userId)
            ?: throw NotFoundException("User with id $userId not found")

        return UserDto(user = user)
    }

    companion object {
        private const val MAX_USERS_LIMIT = 100
        private const val MIN_PASSWORD_LENGTH = 8
    }
}
