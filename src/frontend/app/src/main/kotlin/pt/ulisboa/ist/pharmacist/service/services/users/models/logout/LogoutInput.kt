package pt.ulisboa.ist.pharmacist.service.services.users.models.login

/**
 * The Logout Input.
 *
 * @property refreshToken the user's refresh token
 */
data class LogoutInput(
    val refreshToken: String
)
