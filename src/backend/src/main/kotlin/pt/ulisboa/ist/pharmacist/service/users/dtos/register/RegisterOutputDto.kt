package pt.ulisboa.ist.pharmacist.service.users.dtos.register

/**
 * A Register Output DTO.
 *
 * @property username name of the user
 * @property accessToken the access token
 * @property refreshToken the refresh token
 */
data class RegisterOutputDto(
    val username: String,
    val accessToken: String
)
