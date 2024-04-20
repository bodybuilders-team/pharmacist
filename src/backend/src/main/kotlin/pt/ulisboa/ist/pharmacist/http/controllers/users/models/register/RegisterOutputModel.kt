package pt.ulisboa.ist.pharmacist.http.controllers.users.models.register

import pt.ulisboa.ist.pharmacist.service.users.dtos.register.RegisterOutputDto

/**
 * A Register Output Model.
 *
 * @property accessToken the access token of the user
 */
data class RegisterOutputModel(
    val userId: String,
    val accessToken: String
) {
    constructor(registerOutputDto: RegisterOutputDto) : this(
        userId = registerOutputDto.userId,
        accessToken = registerOutputDto.accessToken
    )
}
