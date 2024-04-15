package pt.ulisboa.ist.pharmacist.service.users

import pt.ulisboa.ist.pharmacist.service.users.dtos.UserDTO
import pt.ulisboa.ist.pharmacist.service.users.dtos.UsersDTO
import pt.ulisboa.ist.pharmacist.service.users.dtos.login.LoginInputDTO
import pt.ulisboa.ist.pharmacist.service.users.dtos.login.LoginOutputDTO
import pt.ulisboa.ist.pharmacist.service.users.dtos.refreshToken.RefreshTokenOutputDTO
import pt.ulisboa.ist.pharmacist.service.users.dtos.register.RegisterInputDTO
import pt.ulisboa.ist.pharmacist.service.users.dtos.register.RegisterOutputDTO
import pt.ulisboa.ist.pharmacist.service.users.utils.UsersOrder

/**
 * Service that handles the business logic of the users.
 */
interface UsersService {

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
    fun getUsers(offset: Int, limit: Int, orderBy: UsersOrder, ascending: Boolean): UsersDTO

    /**
     * Registers a new user.
     *
     * @param registerInputDTO the DTO with the data to create the user
     *
     * @return the JWT token for the new user
     * @throws AlreadyExistsException if the user already exists
     * @throws InvalidPasswordException if the password is invalid
     */
    fun register(registerInputDTO: RegisterInputDTO): RegisterOutputDTO

    /**
     * Logs a user in.
     *
     * @param loginInputDTO the DTO with the data to log the user in
     *
     * @return the JWT token for the user
     * @throws NotFoundException if the user does not exist
     * @throws InvalidLoginException if the password is incorrect
     */
    fun login(loginInputDTO: LoginInputDTO): LoginOutputDTO

    /**
     * Logs a user out.
     *
     * @param accessToken the access token of the user
     * @param refreshToken the refresh token of the user
     *
     * @throws NotFoundException if the refresh token does not exist or if it is expired
     * @throws AuthenticationException if the refresh token is invalid
     */
    fun logout(accessToken: String, refreshToken: String)

    /**
     * Refreshes the JWT token of a user.
     *
     * @param accessToken the access token of the user
     * @param refreshToken the refresh token of the user
     *
     * @return the new JWT token for the user
     * @throws NotFoundException if the refresh token does not exist
     * @throws RefreshTokenExpiredException if the refresh token is expired
     * @throws AuthenticationException if the refresh token is invalid
     */
    fun refreshToken(accessToken: String, refreshToken: String): RefreshTokenOutputDTO

    /**
     * Gets the user with the given id.
     *
     * @param userId the id of the user
     *
     * @return the DTO with the user's data
     * @throws NotFoundException if the user does not exist
     */
    fun getUser(userId: String): UserDTO
}
