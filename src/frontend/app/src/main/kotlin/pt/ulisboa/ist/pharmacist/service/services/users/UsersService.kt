package pt.ulisboa.ist.pharmacist.service.services.users

import com.google.gson.Gson
import okhttp3.OkHttpClient
import pt.ulisboa.ist.pharmacist.service.HTTPService
import pt.ulisboa.ist.pharmacist.service.connection.APIResult
import pt.ulisboa.ist.pharmacist.service.services.users.models.login.LoginInput
import pt.ulisboa.ist.pharmacist.service.services.users.models.login.LoginOutput
import pt.ulisboa.ist.pharmacist.service.services.users.models.register.RegisterInput
import pt.ulisboa.ist.pharmacist.service.services.users.models.register.RegisterOutput
import pt.ulisboa.ist.pharmacist.service.utils.Uris
import pt.ulisboa.ist.pharmacist.session.SessionManager
import java.io.IOException

/**
 * The service that handles the users requests.
 *
 * @property apiEndpoint the API endpoint
 * @property httpClient the HTTP client
 * @property jsonEncoder the JSON encoder used to serialize/deserialize objects
 * @property sessionManager the session manager
 */
class UsersService(
    apiEndpoint: String,
    httpClient: OkHttpClient,
    jsonEncoder: Gson,
    val sessionManager: SessionManager
) : HTTPService(apiEndpoint, httpClient, jsonEncoder) {

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
    ): APIResult<RegisterOutput> =
        post(
            link = Uris.USERS,
            body = RegisterInput(username = username, password = password)
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
    ): APIResult<LoginOutput> =
        post(
            link = Uris.USERS_LOGIN,
            body = LoginInput(username, password)
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
            body = LoginInput(username, password)
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
                userId = sessionManager.usedId ?: throw IllegalStateException("No user id"),
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
                userId = sessionManager.usedId ?: throw IllegalStateException("No user id"),
                pharmacyId = pharmacyId
            ),
            token = sessionManager.accessToken ?: throw IllegalStateException("No access token")
        )

    suspend fun flagPharmacy(pharmacyId: Long): APIResult<Unit> =
        put(
            link = Uris.userFlaggedPharmacyById(
                userId = sessionManager.usedId ?: throw IllegalStateException("No user id"),
                pharmacyId = pharmacyId
            ),
            token = sessionManager.accessToken ?: throw IllegalStateException("No access token")
        )

    suspend fun unflagPharmacy(pharmacyId: Long): APIResult<Unit> =
        delete(
            link = Uris.userFlaggedPharmacyById(
                userId = sessionManager.usedId ?: throw IllegalStateException("No user id"),
                pharmacyId = pharmacyId
            ),
            token = sessionManager.accessToken ?: throw IllegalStateException("No access token")
        )
}
