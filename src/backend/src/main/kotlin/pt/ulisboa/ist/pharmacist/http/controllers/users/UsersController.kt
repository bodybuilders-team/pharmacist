package pt.ulisboa.ist.pharmacist.http.controllers.users

import jakarta.validation.Valid
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestAttribute
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import pt.ulisboa.ist.pharmacist.http.controllers.users.models.getUser.GetUserOutputModel
import pt.ulisboa.ist.pharmacist.http.controllers.users.models.getUsers.GetUsersOutputModel
import pt.ulisboa.ist.pharmacist.http.controllers.users.models.login.LoginInputModel
import pt.ulisboa.ist.pharmacist.http.controllers.users.models.login.LoginOutputModel
import pt.ulisboa.ist.pharmacist.http.controllers.users.models.logout.LogoutUserInputModel
import pt.ulisboa.ist.pharmacist.http.controllers.users.models.logout.LogoutUserOutputModel
import pt.ulisboa.ist.pharmacist.http.controllers.users.models.register.RegisterInputModel
import pt.ulisboa.ist.pharmacist.http.controllers.users.models.register.RegisterOutputModel
import pt.ulisboa.ist.pharmacist.http.pipeline.authentication.Authenticated
import pt.ulisboa.ist.pharmacist.http.utils.Params
import pt.ulisboa.ist.pharmacist.http.utils.Uris
import pt.ulisboa.ist.pharmacist.service.exceptions.InvalidArgumentException
import pt.ulisboa.ist.pharmacist.service.users.UsersService
import pt.ulisboa.ist.pharmacist.service.users.utils.UsersOrder
import pt.ulisboa.ist.pharmacist.utils.JwtProvider

/**
 * Controller that handles the requests related to the users.
 *
 * @property usersService the service that handles the business logic related to the users
 */
@RestController
@RequestMapping(produces = ["application/json"])
class UsersController(private val usersService: UsersService) {

    /**
     * Handles the request to get all the users.
     *
     * @param offset the offset of the users to be returned
     * @param limit the limit of the users to be returned
     * @param orderBy the order by of the users to be returned
     * @param sortDirection if the users should be ordered by points in ascending order
     *
     * @return the response to the request with all the users
     */
    @GetMapping(Uris.USERS)
    fun getUsers(
        @RequestParam(Params.OFFSET_PARAM, defaultValue = Params.OFFSET_DEFAULT.toString()) offset: Int,
        @RequestParam(Params.LIMIT_PARAM, defaultValue = Params.LIMIT_DEFAULT.toString()) limit: Int,
        @RequestParam(Params.ORDER_BY_PARAM, required = false) orderBy: String?,
        @RequestParam(Params.SORT_DIRECTION_PARAM, defaultValue = Params.SORT_DIR_DESCENDING) sortDirection: String
    ): GetUsersOutputModel {
        val usersDto = usersService.getUsers(
            offset = offset,
            limit = limit,
            orderBy = if (orderBy != null) UsersOrder.valueOf(orderBy) else UsersOrder.POINTS,
            ascending = when (sortDirection) {
                Params.SORT_DIR_ASCENDING -> true
                Params.SORT_DIR_DESCENDING -> false
                else -> throw InvalidArgumentException(
                    "Invalid sort order, must be ${Params.SORT_DIR_ASCENDING} or ${Params.SORT_DIR_DESCENDING}"
                )
            }
        )

        return GetUsersOutputModel(usersDto)
    }

    /**
     * Handles the request to register a new user.
     *
     * @param userData the data of the user to be created
     * @return the response to the request with the created user
     */
    @PostMapping(Uris.USERS)
    fun register(
        @Valid @RequestBody
        userData: RegisterInputModel
    ): RegisterOutputModel {
        return RegisterOutputModel(
            usersService.register(
                username = userData.username,
                email = userData.email,
                password = userData.password
            )
        )
    }

    /**
     * Handles the request to log in a user.
     *
     * @param userData the data of the user to be logged in
     * @return the response to the request with the token of the logged-in user
     */
    @PostMapping(Uris.USERS_LOGIN)
    fun login(
        @Valid @RequestBody
        userData: LoginInputModel
    ): LoginOutputModel {
        return LoginOutputModel(
            usersService.login(
                username = userData.username,
                password = userData.password
            )
        )
    }

    /**
     * Handles the request to add a pharmacy to the user's favorite pharmacies.
     *
     * @param uid the id of the user to add the pharmacy to
     * @param pid the id of the pharmacy to be added
     * @return the response to the request
     */
    @PutMapping(Uris.USER_FAVORITE_PHARMACIES_GET_BY_ID)
    fun addFavoritePharmacy(
        @PathVariable uid: String,
        @PathVariable pid: Long
    ) {
        usersService.addFavoritePharmacy(userId = uid, pharmacyId = pid)
    }

    /**
     * Handles the request to remove a pharmacy from the user's favorite pharmacies.
     *
     * @param uid the id of the user to add the pharmacy to
     * @param pid the id of the pharmacy to be added
     * @return the response to the request
     */
    @DeleteMapping(Uris.USER_FAVORITE_PHARMACIES_GET_BY_ID)
    fun removeFavoritePharmacy(
        @PathVariable uid: String,
        @PathVariable pid: Long
    ) {
        usersService.removeFavoritePharmacy(userId = uid, pharmacyId = pid)
    }

    /**
     * Handles the request to log out a user.
     *
     * @param logoutUserInputModel the data of the user to be logged out
     * @return the response to the request
     */
    @PostMapping(Uris.USERS_LOGOUT)
    @Authenticated
    fun logout(
        @Valid @RequestBody(required = false)
        logoutUserInputModel: LogoutUserInputModel?,
        @RequestAttribute(JwtProvider.ACCESS_TOKEN_ATTRIBUTE) accessToken: String
    ): LogoutUserOutputModel {
        usersService.logout(accessToken = accessToken)

        return LogoutUserOutputModel("User logged out successfully")
    }

    /**
     * Handles the request to get a user.
     *
     * @param uid the id of the user to be returned
     * @return the response to the request with the user
     */
    @GetMapping(Uris.USERS_GET_BY_ID)
    fun getUser(
        @PathVariable uid: String
    ): GetUserOutputModel {
        return GetUserOutputModel(usersService.getUser(userId = uid))
    }
}