package pt.ulisboa.ist.pharmacist.repository.remote.users.register

/**
 * The Register Input.
 *
 * @property username the user's username
 * @property password the user's password
 */
data class RegisterInputDto(
    val username: String,
    val password: String
)
