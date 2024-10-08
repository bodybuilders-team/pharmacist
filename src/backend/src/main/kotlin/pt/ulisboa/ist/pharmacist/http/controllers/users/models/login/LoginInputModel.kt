package pt.ulisboa.ist.pharmacist.http.controllers.users.models.login

import jakarta.validation.constraints.Size
import pt.ulisboa.ist.pharmacist.http.controllers.users.models.register.RegisterInputModel.Companion.MAX_PASSWORD_LENGTH
import pt.ulisboa.ist.pharmacist.http.controllers.users.models.register.RegisterInputModel.Companion.MAX_USERNAME_LENGTH
import pt.ulisboa.ist.pharmacist.http.controllers.users.models.register.RegisterInputModel.Companion.MIN_PASSWORD_LENGTH
import pt.ulisboa.ist.pharmacist.http.controllers.users.models.register.RegisterInputModel.Companion.MIN_USERNAME_LENGTH

/**
 * A Login Input Model.
 *
 * @property username the username of the user
 * @property password the password of the user
 */
data class LoginInputModel(
    @field:Size(
        min = MIN_USERNAME_LENGTH,
        max = MAX_USERNAME_LENGTH,
        message = "Username must be between $MIN_USERNAME_LENGTH and $MAX_USERNAME_LENGTH characters long."
    )
    val username: String,

    @field:Size(
        min = MIN_PASSWORD_LENGTH,
        max = MAX_PASSWORD_LENGTH,
        message = "Password must be between $MIN_PASSWORD_LENGTH and $MAX_PASSWORD_LENGTH characters long."
    )
    val password: String
)