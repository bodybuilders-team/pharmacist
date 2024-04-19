package pt.ulisboa.ist.pharmacist.service.users

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import pt.ulisboa.ist.pharmacist.domain.users.User
import pt.ulisboa.ist.pharmacist.repository.users.AccessTokensRepository
import pt.ulisboa.ist.pharmacist.repository.users.UsersRepository
import pt.ulisboa.ist.pharmacist.service.exceptions.AlreadyExistsException
import pt.ulisboa.ist.pharmacist.service.exceptions.AuthenticationException
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
import pt.ulisboa.ist.pharmacist.utils.JwtProvider
import java.sql.Timestamp
import java.util.UUID

/**
 * Service that handles the business logic of the users.
 *
 * @property usersRepository the repository of the users
 * @property hashingUtils the utils for password operations
 * @property jwtProvider the JWT provider
 */
@Service
@Transactional(rollbackFor = [Exception::class])
class UsersServiceImpl(
    private val usersRepository: UsersRepository,
    private val accessTokensRepository: AccessTokensRepository,
    private val hashingUtils: HashingUtils,
    private val jwtProvider: JwtProvider
) : UsersService {

    override fun getUsers(offset: Int, limit: Int, orderBy: UsersOrder, ascending: Boolean): UsersDto {
        if (offset < 0 || limit < 0)
            throw InvalidPaginationParamsException("Offset and limit must be positive")

        if (limit > MAX_USERS_LIMIT)
            throw InvalidPaginationParamsException("Limit must be less or equal than $MAX_USERS_LIMIT")

        return UsersDto(
            users = usersRepository
                .let {
                    val pageable = OffsetPageRequest(
                        offset = offset.toLong(),
                        limit = limit,
                        sort = orderBy.toSort(ascending)
                    )

                    usersRepository.findAll(/* pageable = */ pageable)
                }
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

        val accessToken = createToken(user = user)

        return RegisterOutputDto(
            username = username,
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

        val accessToken = createToken(user = user)

        return LoginOutputDto(
            accessToken = accessToken
        )
    }

    override fun logout(accessToken: String) {
        //val user = getUserAndRevokeAccessToken(accessToken = accessToken)

        //TODO: Revoke access token?
    }

    /**
     * Gets the user from the access token and revokes it.
     */
    private fun getUserAndRevokeAccessToken(accessToken: String): User {
        val accessTokenPayload = jwtProvider.getAccessTokenPayloadOrNull(token = accessToken)
            ?: throw AuthenticationException("Invalid access token")

        val user = usersRepository.findByUsername(username = accessTokenPayload.username)
            ?: throw NotFoundException("User not found")

        accessTokensRepository.create(
            tokenHash = hashingUtils.hashToken(token = accessToken),
            user = user,
            expirationDate = Timestamp.from(accessTokenPayload.claims.expiration.toInstant())
        )
        return user
    }

    override fun getUser(userId: String): UserDto {
        val user = usersRepository
            .findById(userId)
            ?: throw NotFoundException("User with id $userId not found")

        return UserDto(user = user)
    }

    /**
     * Creates the access token for the given user.
     *
     * @param user the user to create the tokens for
     * @return the access tokens
     */
    private fun createToken(user: User): String {
        val jwtPayload = JwtProvider.JwtPayload.fromData(username = user.username)
        val accessToken = jwtProvider.createAccessToken(jwtPayload = jwtPayload)

        return accessToken
    }

    companion object {
        private const val MAX_USERS_LIMIT = 100
        private const val MIN_PASSWORD_LENGTH = 8
    }
}
