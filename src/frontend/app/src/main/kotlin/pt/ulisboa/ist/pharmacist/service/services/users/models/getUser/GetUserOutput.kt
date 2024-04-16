package pt.ulisboa.ist.pharmacist.service.services.users.models.getUser

/**
 * The Get User Output Model.
 *
 * @property username the username of the user
 * @property email the email of the user
 * @property points the points of the user
 */
data class GetUserOutputModel(
    val username: String,
    val email: String,
    val points: Int
)

/**
 * The Get User Output.
 */
typealias GetUserOutput = GetUserOutputModel
