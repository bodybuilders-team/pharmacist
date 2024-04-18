package pt.ulisboa.ist.pharmacist.service.users

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import pt.ulisboa.ist.pharmacist.domain.users.AccessToken
import pt.ulisboa.ist.pharmacist.domain.users.User
import pt.ulisboa.ist.pharmacist.repository.users.AccessTokensRepository
import pt.ulisboa.ist.pharmacist.repository.users.UsersRepository
import pt.ulisboa.ist.pharmacist.service.exceptions.AlreadyExistsException
import pt.ulisboa.ist.pharmacist.service.exceptions.AuthenticationException
import pt.ulisboa.ist.pharmacist.service.exceptions.InvalidLoginException
import pt.ulisboa.ist.pharmacist.service.exceptions.InvalidPaginationParamsException
import pt.ulisboa.ist.pharmacist.service.exceptions.InvalidPasswordException
import pt.ulisboa.ist.pharmacist.service.exceptions.NotFoundException
import pt.ulisboa.ist.pharmacist.service.users.dtos.UserDTO
import pt.ulisboa.ist.pharmacist.service.users.dtos.UsersDTO
import pt.ulisboa.ist.pharmacist.service.users.dtos.login.LoginInputDTO
import pt.ulisboa.ist.pharmacist.service.users.dtos.login.LoginOutputDTO
import pt.ulisboa.ist.pharmacist.service.users.dtos.register.RegisterInputDTO
import pt.ulisboa.ist.pharmacist.service.users.dtos.register.RegisterOutputDTO
import pt.ulisboa.ist.pharmacist.service.users.utils.UsersOrder
import pt.ulisboa.ist.pharmacist.service.utils.HashingUtils
import pt.ulisboa.ist.pharmacist.service.utils.OffsetPageRequest
import pt.ulisboa.ist.pharmacist.utils.JwtProvider
import pt.ulisboa.ist.pharmacist.utils.ServerConfiguration
import java.sql.Timestamp

/**
 * Service that handles the business logic of the users.
 *
 * @property usersRepository the repository of the users
 * @property hashingUtils the utils for password operations
 * @property jwtProvider the JWT provider
 * @property config the server configuration
 */
@Service
@Transactional(rollbackFor = [Exception::class])
class UsersServiceImpl(
    private val usersRepository: UsersRepository,
    private val AccessTokensRepository: AccessTokensRepository,
    private val hashingUtils: HashingUtils,
    private val jwtProvider: JwtProvider,
    private val config: ServerConfiguration
) : UsersService {

    override fun getUsers(offset: Int, limit: Int, orderBy: UsersOrder, ascending: Boolean): UsersDTO {
        if (offset < 0 || limit < 0)
            throw InvalidPaginationParamsException("Offset and limit must be positive")

        if (limit > MAX_USERS_LIMIT)
            throw InvalidPaginationParamsException("Limit must be less or equal than $MAX_USERS_LIMIT")

        return UsersDTO(
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
                .map(::UserDTO),
            totalCount = usersRepository.count().toInt()
        )
    }

    override fun register(registerInputDTO: RegisterInputDTO): RegisterOutputDTO {
        if (usersRepository.existsByUsername(username = registerInputDTO.username))
            throw AlreadyExistsException("User with username ${registerInputDTO.username} already exists")

        if (usersRepository.existsByEmail(email = registerInputDTO.email))
            throw AlreadyExistsException("User with email ${registerInputDTO.email} already exists")

        if (registerInputDTO.password.length < MIN_PASSWORD_LENGTH)
            throw InvalidPasswordException("Password must be at least $MIN_PASSWORD_LENGTH characters long")

        val user = usersRepository.save(
            User(
                username = registerInputDTO.username,
                email = registerInputDTO.email,
                passwordHash = hashingUtils.hashPassword(
                    username = registerInputDTO.username,
                    password = registerInputDTO.password
                )
            )
        )

        val accessToken = createToken(user = user)

        return RegisterOutputDTO(
            username = registerInputDTO.username,
            accessToken = accessToken
        )
    }

    override fun login(loginInputDTO: LoginInputDTO): LoginOutputDTO {
        val user = usersRepository
            .findByUsername(username = loginInputDTO.username)
            ?: throw InvalidLoginException("Invalid username or password")

        if (
            !hashingUtils.checkPassword(
                username = loginInputDTO.username,
                password = loginInputDTO.password,
                passwordHash = user.passwordHash
            )
        ) throw InvalidLoginException("Invalid username or password")

        val accessToken = createToken(user = user)

        return LoginOutputDTO(
            accessToken = accessToken
        )
    }

    override fun logout(accessToken: String, refreshToken: String) {
        val user = getUserAndRevokeAccessToken(accessToken = accessToken)

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

        val accessTokenEntity = AccessToken(
            tokenHash = hashingUtils.hashToken(token = accessToken),
            user = user,
            expirationDate = Timestamp.from(accessTokenPayload.claims.expiration.toInstant())
        )

        AccessTokensRepository.save(accessTokenEntity)
        return user
    }

    override fun getUser(userId: Long): UserDTO {
        val user = usersRepository
            .findById(userId)
            ?: throw NotFoundException("User with id $userId not found")

        return UserDTO(user = user)
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

    /**
     * The tokens of a user.
     *
     * @property accessToken the access token
     * @property refreshToken the refresh token
     */
    private data class Tokens(
        val accessToken: String,
        val refreshToken: String
    )

    companion object {
        private const val MAX_USERS_LIMIT = 100
        private const val MIN_PASSWORD_LENGTH = 8
    }
}
