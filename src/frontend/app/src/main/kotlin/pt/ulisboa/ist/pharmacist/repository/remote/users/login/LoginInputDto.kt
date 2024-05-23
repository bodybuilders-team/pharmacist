package pt.ulisboa.ist.pharmacist.repository.remote.users.login

/**
 * The Login Input.
 *
 * @property username the user's username
 * @property password the user's password
 */
data class LoginInputDto(
    val username: String,
    val password: String
)
