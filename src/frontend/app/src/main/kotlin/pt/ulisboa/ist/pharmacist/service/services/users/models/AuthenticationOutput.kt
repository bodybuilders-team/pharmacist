package pt.ulisboa.ist.pharmacist.service.services.users.models


/**
 * The Authentication Output Model.
 *
 * @property accessToken the access token
 * @property refreshToken the refresh token
 */
data class AuthenticationOutput(
    val accessToken: String,
    val refreshToken: String
)

