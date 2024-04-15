package pt.ulisboa.ist.pharmacist.service.services.users.models.getUsers


/**
 * The Get Users Output Model.
 *
 * @property totalCount the total number of users
 */
data class GetUsersOutput(
    val totalCount: Int,
    val users: List<GetUsersUserModel>
)

