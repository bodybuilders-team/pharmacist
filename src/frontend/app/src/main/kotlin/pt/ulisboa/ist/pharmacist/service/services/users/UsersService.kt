package pt.ulisboa.ist.pharmacist.service.services.users

import com.google.gson.Gson
import java.io.IOException
import okhttp3.OkHttpClient
import pt.ulisboa.ist.pharmacist.service.HTTPService
import pt.ulisboa.ist.pharmacist.service.connection.APIResult
import pt.ulisboa.ist.pharmacist.service.services.users.models.getUsers.GetUsersOutput
import pt.ulisboa.ist.pharmacist.service.services.users.models.login.LoginInput
import pt.ulisboa.ist.pharmacist.service.services.users.models.login.LoginOutput
import pt.ulisboa.ist.pharmacist.service.services.users.models.logout.LogoutOutput
import pt.ulisboa.ist.pharmacist.service.services.users.models.register.RegisterInput
import pt.ulisboa.ist.pharmacist.service.services.users.models.register.RegisterOutput
import pt.ulisboa.ist.pharmacist.service.utils.Uris

/**
 * The service that handles the users requests.
 *
 * @property apiEndpoint the API endpoint
 * @property httpClient the HTTP client
 * @property jsonEncoder the JSON encoder used to serialize/deserialize objects
 */
class UsersService(
    apiEndpoint: String,
    httpClient: OkHttpClient,
    jsonEncoder: Gson
) : HTTPService(apiEndpoint, httpClient, jsonEncoder) {

    /**
     * Gets all the users.
     *
     * @return the API result of the get users request
     *
     * @throws UnexpectedResponseException if there is an unexpected response from the server
     * @throws IOException if there is an error while sending the request
     */
    suspend fun getUsers(): APIResult<GetUsersOutput> {
        return get<GetUsersOutput>(link = Uris.USERS)
    }

    /**
     * Registers the user with the given [email], [username] and [password].
     *
     * @param email the email of the user
     * @param username the username of the user
     * @param password the password of the user
     *
     * @return the API result of the register request
     *
     * @throws UnexpectedResponseException if there is an unexpected response from the server
     * @throws IOException if there is an error while sending the request
     */
    suspend fun register(
        email: String,
        username: String,
        password: String
    ): APIResult<RegisterOutput> =
        post(
            link = Uris.USERS,
            body = RegisterInput(username = username, email = email, password = password)
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
     * Logs the user out.
     *
     * @return the API result of the logout request
     *
     * @throws UnexpectedResponseException if there is an unexpected response from the server
     * @throws IOException if there is an error while sending the request
     */
    suspend fun logout(
        accessToken: String
    ): APIResult<LogoutOutput> = post(link = Uris.USERS_LOGOUT, token = accessToken)

}
