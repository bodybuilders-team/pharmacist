package pt.ulisboa.ist.pharmacist.service.users.dtos.refreshToken

/**
 * A Refresh Token Output DTO.
 *
 * @property accessToken the access token
 * @property refreshToken the refresh token
 */
data class RefreshTokenOutputDto(
    val accessToken: String,
    val refreshToken: String
)