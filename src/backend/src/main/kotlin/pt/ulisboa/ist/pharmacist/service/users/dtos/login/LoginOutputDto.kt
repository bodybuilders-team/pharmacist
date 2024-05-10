package pt.ulisboa.ist.pharmacist.service.users.dtos.login

/**
 * A Login Output DTO.
 *
 * @property accessToken the access token
 */
data class LoginOutputDto(
    val userId: Long,
    val accessToken: String,
    val isSuspended: Boolean
)
