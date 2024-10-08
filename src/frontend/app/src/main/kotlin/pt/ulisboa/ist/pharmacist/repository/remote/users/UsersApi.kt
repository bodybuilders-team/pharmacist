package pt.ulisboa.ist.pharmacist.repository.remote.users

import android.content.Context
import okhttp3.OkHttpClient
import pt.ulisboa.ist.pharmacist.repository.network.HTTPService
import pt.ulisboa.ist.pharmacist.repository.network.connection.APIResult
import pt.ulisboa.ist.pharmacist.repository.network.utils.Uris
import pt.ulisboa.ist.pharmacist.repository.remote.users.login.LoginInputDto
import pt.ulisboa.ist.pharmacist.repository.remote.users.login.LoginOutputDto
import pt.ulisboa.ist.pharmacist.repository.remote.users.register.RegisterInputDto
import pt.ulisboa.ist.pharmacist.repository.remote.users.register.RegisterOutputDto
import pt.ulisboa.ist.pharmacist.session.SessionManager
import java.io.IOException
import javax.inject.Inject

/**
 * The api service that handles the users requests.
 *
 * @property httpClient the HTTP client
 * @property sessionManager the session manager
 */
class UsersApi @Inject constructor(
    context: Context,
    httpClient: OkHttpClient,
    sessionManager: SessionManager
) : HTTPService(context, sessionManager, httpClient) {

    /**
     * Registers the user with the given [username] and [password].
     *
     * @param username the username of the user
     * @param password the password of the user
     *
     * @return the API result of the register request
     *
     * @throws UnexpectedResponseException if there is an unexpected response from the server
     * @throws IOException if there is an error while sending the request
     */
    suspend fun register(
        username: String,
        password: String
    ): APIResult<RegisterOutputDto> =
        post(
            link = Uris.USERS,
            body = RegisterInputDto(username = username, password = password)
        )

    /**
     * Logs in the user with the given [username] and [password].
     *
     * @param username the username of the user
     * @param password the password of the user
     *
     * @return the API result of the login request
     *
     * @throws UnexpectedResponseException if there is an unexpected response from the server
     * @throws IOException if there is an error while sending the request
     */
    suspend fun login(
        username: String,
        password: String
    ): APIResult<LoginOutputDto> =
        post(
            link = Uris.USERS_LOGIN,
            body = LoginInputDto(
                username,
                password
            )
        )

    /**
     * Upgrades the account of the user with the given [username] and [password].
     *
     * @param username the username of the user
     * @param password the password of the user
     *
     * @return the API result of the upgrade account request
     */
    suspend fun upgrade(
        username: String,
        password: String
    ): APIResult<Unit> =
        post(
            link = Uris.USERS_UPGRADE,
            token = sessionManager.accessToken ?: throw IllegalStateException("No access token"),
            body = LoginInputDto(
                username,
                password
            )
        )

    /**
     * Logs the user out.
     *
     * @return the API result of the logout request
     *
     * @throws UnexpectedResponseException if there is an unexpected response from the server
     * @throws IOException if there is an error while sending the request
     */
    suspend fun logout(): APIResult<Unit> =
        post(
            link = Uris.USERS_LOGOUT,
            token = sessionManager.accessToken ?: throw IllegalStateException("No access token")
        )

    /**
     * Adds the pharmacy with the given [pharmacyId] to the favorites of the user.
     *
     * @param pharmacyId the id of the pharmacy
     */
    suspend fun addFavorite(pharmacyId: Long): APIResult<Unit> =
        put(
            link = Uris.userFavoritePharmacyById(
                userId = sessionManager.userId ?: throw IllegalStateException("No user id"),
                pharmacyId = pharmacyId
            ),
            token = sessionManager.accessToken ?: throw IllegalStateException("No access token")
        )

    /**
     * Removes the pharmacy with the given [pharmacyId] from the favorites of the user.
     *
     * @param pharmacyId the id of the pharmacy
     */
    suspend fun removeFavorite(pharmacyId: Long): APIResult<Unit> =
        delete(
            link = Uris.userFavoritePharmacyById(
                userId = sessionManager.userId ?: throw IllegalStateException("No user id"),
                pharmacyId = pharmacyId
            ),
            token = sessionManager.accessToken ?: throw IllegalStateException("No access token")
        )

    suspend fun flagPharmacy(pharmacyId: Long): APIResult<Unit> =
        put(
            link = Uris.userFlaggedPharmacyById(
                userId = sessionManager.userId ?: throw IllegalStateException("No user id"),
                pharmacyId = pharmacyId
            ),
            token = sessionManager.accessToken ?: throw IllegalStateException("No access token")
        )

    suspend fun unflagPharmacy(pharmacyId: Long): APIResult<Unit> =
        delete(
            link = Uris.userFlaggedPharmacyById(
                userId = sessionManager.userId ?: throw IllegalStateException("No user id"),
                pharmacyId = pharmacyId
            ),
            token = sessionManager.accessToken ?: throw IllegalStateException("No access token")
        )
}
