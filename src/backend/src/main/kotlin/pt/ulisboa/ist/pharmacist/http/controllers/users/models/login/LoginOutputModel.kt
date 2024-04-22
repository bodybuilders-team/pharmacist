package pt.ulisboa.ist.pharmacist.http.controllers.users.models.login

import pt.ulisboa.ist.pharmacist.service.users.dtos.login.LoginOutputDto

/**
 * A Login Output Model.
 *
 * @property userId the id of the user
 * @property accessToken the access token of the user
 */
data class LoginOutputModel(
    val userId: Long,
    val accessToken: String
) {
    constructor(loginOutputDto: LoginOutputDto) : this(
        userId = loginOutputDto.userId,
        accessToken = loginOutputDto.accessToken
    )
}
