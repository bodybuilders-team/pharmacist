package pt.ulisboa.ist.pharmacist.service.users.dtos.register

/**
 * A Register Input DTO.
 *
 * @property username name of the user
 * @property password password of the user
 */
data class RegisterInputDto(
    val username: String,
    val password: String
)
