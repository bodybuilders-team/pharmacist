package pt.ulisboa.ist.pharmacist.http.controllers.users.models.getUser

import pt.ulisboa.ist.pharmacist.service.users.dtos.UserDto

/**
 * A Get User Output Model.
 *
 * @property username the username of the user
 */
data class GetUserOutputModel(
    val username: String,
) {
    constructor(userDto: UserDto) : this(
        username = userDto.username
    )
}
