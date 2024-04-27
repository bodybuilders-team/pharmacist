package pt.ulisboa.ist.pharmacist.service.http.services.users.models.register

/**
 * The Register Input.
 *
 * @property username the user's username
 * @property password the user's password
 */
data class RegisterInput(
    val username: String,
    val password: String
)
