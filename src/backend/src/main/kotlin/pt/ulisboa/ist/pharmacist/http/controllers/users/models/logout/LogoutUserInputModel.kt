package pt.ulisboa.ist.pharmacist.http.controllers.users.models.logout

/**
 * A Logout User Input Model.
 *
 * @property accessToken the refresh token of the user
 */
data class LogoutUserInputModel(
    val accessToken: String
)
