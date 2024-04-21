package pt.ulisboa.ist.pharmacist.service.users

import pt.ulisboa.ist.pharmacist.domain.users.User
import pt.ulisboa.ist.pharmacist.service.users.dtos.UserDto
import pt.ulisboa.ist.pharmacist.service.users.dtos.UsersDto
import pt.ulisboa.ist.pharmacist.service.users.dtos.login.LoginOutputDto
import pt.ulisboa.ist.pharmacist.service.users.dtos.register.RegisterOutputDto
import pt.ulisboa.ist.pharmacist.service.users.utils.UsersOrder

/**
 * Service that handles the business logic of the users.
 */
interface UsersService {

    /**
     * Adds a favorite pharmacy to the user.
     *
     * @param userId the id of the user
     * @param pharmacyId the id of the pharmacy
     */
    fun addFavoritePharmacy(userId: String, pharmacyId: Long)

    /**
     * Removes a favorite pharmacy from the user.
     *
     * @param userId the id of the user
     * @param pharmacyId the id of the pharmacy
     */
    fun removeFavoritePharmacy(userId: String, pharmacyId: Long)

    /**
     * Gets all users.
     *
     * @param offset the offset of the pagination
     * @param limit the limit of the pagination
     * @param orderBy the order by of the pagination
     * @param ascending if the users should be ordered by points in ascending order
     *
     * @return the DTO with the information of the users
     * @throws InvalidPaginationParamsException if the offset or limit are invalid
     */
    fun getUsers(offset: Int, limit: Int, orderBy: UsersOrder, ascending: Boolean): UsersDto

    /**
     * Registers a new user.
     *
     * @param username the name of the user
     * @param email the email of the user
     * @param password the password of the user
     *
     * @return the JWT token for the new user
     * @throws AlreadyExistsException if the user already exists
     * @throws InvalidPasswordException if the password is invalid
     */
    fun register(username: String, email: String, password: String): RegisterOutputDto

    /**
     * Logs a user in.
     *
     * @param username the name of the user
     * @param password the password of the user
     *
     * @return the JWT token for the user
     * @throws NotFoundException if the user does not exist
     * @throws InvalidLoginException if the password is incorrect
     */
    fun login(username: String, password: String): LoginOutputDto

    /**
     * Logs a user out.
     *
     * @param user the user to log out
     * @param accessToken the access token of the user
     */
    fun logout(user: User, accessToken: String)

    /**
     * Gets the user with the given id.
     *
     * @param userId the id of the user
     *
     * @return the DTO with the user's data
     * @throws NotFoundException if the user does not exist
     */
    fun getUser(userId: String): UserDto
}
