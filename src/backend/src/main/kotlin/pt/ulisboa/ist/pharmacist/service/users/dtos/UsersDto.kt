package pt.ulisboa.ist.pharmacist.service.users.dtos

/**
 * A Users DTO.
 *
 * @property users the list of users DTOs
 * @property totalCount the total number of users
 */
data class UsersDto(
    val users: List<UserDto>,
    val totalCount: Int
)
