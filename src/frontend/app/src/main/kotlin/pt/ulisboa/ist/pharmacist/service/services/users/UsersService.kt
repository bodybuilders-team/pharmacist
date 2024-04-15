package pt.ulisboa.ist.pharmacist.service.services.users

import com.google.gson.Gson
import okhttp3.OkHttpClient
import pt.ulisboa.ist.pharmacist.service.HTTPService
import pt.ulisboa.ist.pharmacist.service.connection.APIResult
import pt.ulisboa.ist.pharmacist.service.services.users.models.getUsers.GetUsersOutput
import pt.ulisboa.ist.pharmacist.service.services.users.models.login.LoginInput
import pt.ulisboa.ist.pharmacist.service.services.users.models.login.LogoutInput
import pt.ulisboa.ist.pharmacist.service.services.users.models.login.LoginOutput
import pt.ulisboa.ist.pharmacist.service.services.users.models.login.LogoutOutput
import pt.ulisboa.ist.pharmacist.service.services.users.models.refreshToken.RefreshTokenInput
import pt.ulisboa.ist.pharmacist.service.services.users.models.refreshToken.RefreshTokenOutput
import pt.ulisboa.ist.pharmacist.service.services.users.models.register.RegisterInput
import pt.ulisboa.ist.pharmacist.service.services.users.models.register.RegisterOutput
import java.io.IOException

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
     * @param listUsersLink the link to the list users endpoint
     *
     * @return the API result of the get users request
     *
     * @throws UnexpectedResponseException if there is an unexpected response from the server
     * @throws IOException if there is an error while sending the request
     */
    suspend fun getUsers(listUsersLink: String): APIResult<GetUsersOutput> {
        return get<GetUsersOutput>(link = listUsersLink)
    }

    /**
     * Registers the user with the given [email], [username] and [password].
     *
     * @param registerLink the link to the register endpoint
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
        registerLink: String,
        email: String,
        username: String,
        password: String
    ): APIResult<RegisterOutput> =
        post(
            link = registerLink,
            body = RegisterInput(username = username, email = email, password = password)
        )

    /**
     * Logs in the user with the given [username] and [password].
     *
     * @param loginLink the link to the login endpoint
     * @param username the username of the user
     * @param password the password of the user
     *
     * @return the API result of the login request
     *
     * @throws UnexpectedResponseException if there is an unexpected response from the server
     * @throws IOException if there is an error while sending the request
     */
    suspend fun login(
        loginLink: String,
        username: String,
        password: String
    ): APIResult<LoginOutput> =
        post(
            link = loginLink,
            body = LoginInput(username, password)
        )

    /**
     * Logs the user out.
     *
     * @param logoutLink the link to the logout endpoint
     * @param refreshToken the refresh token of the user
     *
     * @return the API result of the logout request
     *
     * @throws UnexpectedResponseException if there is an unexpected response from the server
     * @throws IOException if there is an error while sending the request
     */
    suspend fun logout(
        logoutLink: String,
        refreshToken: String
    ): APIResult<LogoutOutput> =
        post(
            link = logoutLink,
            body = LogoutInput(refreshToken)
        )

    /**
     * Refreshes the access token of the user.
     *
     * @param refreshTokenLink the link to the refresh token endpoint
     * @param refreshToken the refresh token of the user
     *
     * @return the API result of the refresh token request
     *
     * @throws UnexpectedResponseException if there is an unexpected response from the server
     * @throws IOException if there is an error while sending the request
     */
    suspend fun refreshToken(
        refreshTokenLink: String,
        refreshToken: String
    ): APIResult<RefreshTokenOutput> =
        post(
            link = refreshTokenLink,
            body = RefreshTokenInput(refreshToken)
        )
}
