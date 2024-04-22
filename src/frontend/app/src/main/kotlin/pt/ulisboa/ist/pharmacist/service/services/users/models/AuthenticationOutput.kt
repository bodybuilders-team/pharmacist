package pt.ulisboa.ist.pharmacist.service.services.users.models


/**
 * The Authentication Output Model.
 *
 * @property accessToken the access token
 */
data class AuthenticationOutput(
    val userId: Long,
    val accessToken: String
)

