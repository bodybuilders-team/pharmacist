package pt.ulisboa.ist.pharmacist.repository.remote.users


/**
 * The Authentication Output Model.
 *
 * @property accessToken the access token
 */
data class AuthenticationOutputDto(
    val userId: Long,
    val accessToken: String,
    val isSuspended: Boolean
)

