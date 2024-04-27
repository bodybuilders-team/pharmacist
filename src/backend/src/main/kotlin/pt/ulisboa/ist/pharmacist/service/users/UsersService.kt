package pt.ulisboa.ist.pharmacist.service.users

import pt.ulisboa.ist.pharmacist.domain.users.User
import pt.ulisboa.ist.pharmacist.service.exceptions.AlreadyExistsException
import pt.ulisboa.ist.pharmacist.service.exceptions.InvalidLoginException
import pt.ulisboa.ist.pharmacist.service.exceptions.InvalidPasswordException
import pt.ulisboa.ist.pharmacist.service.exceptions.NotFoundException
import pt.ulisboa.ist.pharmacist.service.users.dtos.UserDto
import pt.ulisboa.ist.pharmacist.service.users.dtos.login.LoginOutputDto
import pt.ulisboa.ist.pharmacist.service.users.dtos.register.RegisterOutputDto

/**
 * Service that handles the business logic of the users.
 */
interface UsersService {

    /**
     * Registers a new user.
     *
     * @param username the name of the user
     * @param password the password of the user
     *
     * @return the token for the new user
     * @throws AlreadyExistsException if the user already exists
     * @throws InvalidPasswordException if the password is invalid
     */
    fun register(username: String, password: String): RegisterOutputDto

    /**
     * Logs a user in.
     *
     * @param username the name of the user
     * @param password the password of the user
     *
     * @return the token for the user
     * @throws NotFoundException if the user does not exist
     * @throws InvalidLoginException if the password is incorrect
     */
    fun login(username: String, password: String): LoginOutputDto

    /**
     * Upgrades a guest user to a registered user.
     *
     * @param user the user to upgrade
     * @param username the name of the user
     * @param password the password of the user
     */
    fun upgrade(user: User, username: String, password: String)

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
    fun getUser(userId: Long): UserDto

    /**
     * Adds a favorite pharmacy to the user.
     *
     * @param userId the id of the user
     * @param pharmacyId the id of the pharmacy
     */
    fun addFavoritePharmacy(userId: Long, pharmacyId: Long)

    /**
     * Removes a favorite pharmacy from the user.
     *
     * @param userId the id of the user
     * @param pharmacyId the id of the pharmacy
     */
    fun removeFavoritePharmacy(userId: Long, pharmacyId: Long)

    /**
     * Flags a pharmacy.
     *
     * @param userId the id of the user
     * @param pharmacyId the id of the pharmacy
     */
    fun flagPharmacy(userId: Long, pharmacyId: Long)

    /**
     * Unflags a pharmacy.
     *
     * @param userId the id of the user
     * @param pharmacyId the id of the pharmacy
     */
    fun unflagPharmacy(userId: Long, pharmacyId: Long)
}
