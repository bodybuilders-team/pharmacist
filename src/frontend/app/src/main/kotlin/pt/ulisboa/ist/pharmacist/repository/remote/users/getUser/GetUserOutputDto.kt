package pt.ulisboa.ist.pharmacist.repository.remote.users.getUser

/**
 * The Get User Output Model.
 *
 * @property username the username of the user
 * @property points the points of the user
 */
data class GetUserOutputDto(
    val username: String,
    val points: Int
)
