package pt.ulisboa.ist.pharmacist.service.users.dtos

import pt.ulisboa.ist.pharmacist.domain.users.User


/**
 * A User DTO.
 *
 * @property username the username of the user
 */
data class UserDto(
    val username: String
) {
    constructor(user: User) : this(
        username = user.username
    )
}
