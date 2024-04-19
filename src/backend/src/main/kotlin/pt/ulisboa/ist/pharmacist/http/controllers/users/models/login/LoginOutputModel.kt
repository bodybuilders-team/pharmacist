package pt.ulisboa.ist.pharmacist.http.controllers.users.models.login

import pt.ulisboa.ist.pharmacist.service.users.dtos.login.LoginOutputDto

/**
 * A Login Output Model.
 *
 * @property accessToken the access token of the user
 */
data class LoginOutputModel(
    val accessToken: String
) {
    constructor(loginOutputDto: LoginOutputDto) : this(
        accessToken = loginOutputDto.accessToken
    )
}
