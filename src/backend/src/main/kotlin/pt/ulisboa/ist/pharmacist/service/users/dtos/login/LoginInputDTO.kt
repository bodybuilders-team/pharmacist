package pt.ulisboa.ist.pharmacist.service.users.dtos.login

/**
 * A Login Input DTO.
 *
 * @property username name of the user
 * @property password password of the user
 */
data class LoginInputDTO(
    val username: String,
    val password: String
)
