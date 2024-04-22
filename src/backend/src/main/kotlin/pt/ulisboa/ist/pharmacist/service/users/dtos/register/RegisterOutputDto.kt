package pt.ulisboa.ist.pharmacist.service.users.dtos.register

/**
 * A Register Output DTO.
 *
 * @property userId the id of the user
 * @property accessToken the access token
 */
data class RegisterOutputDto(
    val userId: Long,
    val accessToken: String
)
