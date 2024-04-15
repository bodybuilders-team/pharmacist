package pt.ulisboa.ist.pharmacist.domain.users

/**
 * A user of the application.
 *
 * @property username the user's username
 * @property email the user's email
 */
data class User(
    val username: String,
    val email: String
)
