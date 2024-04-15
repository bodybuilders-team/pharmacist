package pt.ulisboa.ist.pharmacist.http.controllers.users.models.refreshToken

import pt.ulisboa.ist.pharmacist.service.users.dtos.refreshToken.RefreshTokenOutputDTO

/**
 * The Refresh Token Output Model.
 *
 * @property accessToken the access token
 * @property refreshToken the refresh token
 */
data class RefreshTokenOutputModel(
    val accessToken: String,
    val refreshToken: String
) {
    constructor(refreshTokenOutputDTO: RefreshTokenOutputDTO) : this(
        accessToken = refreshTokenOutputDTO.accessToken,
        refreshToken = refreshTokenOutputDTO.refreshToken
    )
}
