package pt.ulisboa.ist.pharmacist.repository.network.services.users.models.getUser

/**
 * The Get User Output Model.
 *
 * @property username the username of the user
 * @property points the points of the user
 */
data class GetUserOutputModel(
    val username: String,
    val points: Int
)

/**
 * The Get User Output.
 */
typealias GetUserOutput = GetUserOutputModel
