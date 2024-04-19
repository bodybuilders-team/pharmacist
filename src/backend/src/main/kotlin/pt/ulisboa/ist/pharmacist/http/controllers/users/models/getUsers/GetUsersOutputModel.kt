package pt.ulisboa.ist.pharmacist.http.controllers.users.models.getUsers

import pt.ulisboa.ist.pharmacist.http.controllers.users.models.getUser.GetUserOutputModel
import pt.ulisboa.ist.pharmacist.service.users.dtos.UsersDto

/**
 * A Get Users Output Model.
 *
 * @property totalCount the total number of users
 */
data class GetUsersOutputModel(
    val totalCount: Int,
    val users: List<GetUserOutputModel>
) {
    constructor(usersDto: UsersDto) : this(
        totalCount = usersDto.totalCount,
        users = usersDto.users.map { GetUserOutputModel(it) }
    )
}