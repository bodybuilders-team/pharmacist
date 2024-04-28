package pt.ulisboa.ist.pharmacist.http.controllers.users

import jakarta.validation.Valid
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestAttribute
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pt.ulisboa.ist.pharmacist.domain.users.User
import pt.ulisboa.ist.pharmacist.http.controllers.users.models.getUser.GetUserOutputModel
import pt.ulisboa.ist.pharmacist.http.controllers.users.models.login.LoginInputModel
import pt.ulisboa.ist.pharmacist.http.controllers.users.models.login.LoginOutputModel
import pt.ulisboa.ist.pharmacist.http.controllers.users.models.register.RegisterInputModel
import pt.ulisboa.ist.pharmacist.http.controllers.users.models.register.RegisterOutputModel
import pt.ulisboa.ist.pharmacist.http.pipeline.authentication.Authenticated
import pt.ulisboa.ist.pharmacist.http.pipeline.authentication.AuthenticationInterceptor
import pt.ulisboa.ist.pharmacist.http.utils.Uris
import pt.ulisboa.ist.pharmacist.service.users.UsersService

/**
 * Controller that handles the requests related to the users.
 *
 * @property usersService the service that handles the business logic related to the users
 */
@RestController
@RequestMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
class UsersController(private val usersService: UsersService) {

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
     * Handles the request to log out a user.
     *
     * @return the response to the request
     */
    @PostMapping(Uris.USERS_LOGOUT)
    @Authenticated
    fun logout(
        @RequestAttribute(AuthenticationInterceptor.USER_ATTRIBUTE) user: User,
        @RequestAttribute(AuthenticationInterceptor.ACCESS_TOKEN_ATTRIBUTE) accessToken: String
    ) {
        usersService.logout(user = user, accessToken = accessToken)
    }

    /**
     * Handles the request to upgrade a guest user to a registered user.
     *
     * @param user the user to upgrade
     * @param userData the data of the user to be upgraded
     */
    @PostMapping(Uris.USERS_UPGRADE)
    @Authenticated
    fun upgrade(
        @RequestAttribute(AuthenticationInterceptor.USER_ATTRIBUTE) user: User,
        @Valid @RequestBody
        userData: LoginInputModel
    ) {
        usersService.upgrade(
            user = user,
            username = userData.username,
            password = userData.password
        )
    }

    /**
     * Handles the request to get a user.
     *
     * @param uid the id of the user to be returned
     *
     * @return the response to the request with the user
     */
    @GetMapping(Uris.USERS_GET_BY_ID)
    @Authenticated
    fun getUser(
        @PathVariable uid: Long
    ): GetUserOutputModel {
        return GetUserOutputModel(usersService.getUser(userId = uid))
    }

    /**
     * Handles the request to add a pharmacy to the user's favorite pharmacies.
     *
     * @param uid the id of the user to add the pharmacy to
     * @param pid the id of the pharmacy to be added
     * @return the response to the request
     */
    @PutMapping(Uris.USER_FAVORITE_PHARMACIES_GET_BY_ID)
    @Authenticated
    fun addFavoritePharmacy(
        @RequestAttribute(AuthenticationInterceptor.USER_ATTRIBUTE) user: User,
        @PathVariable uid: Long,
        @PathVariable pid: Long
    ) {
        if (user.userId != uid)
            throw RuntimeException("User with id ${user.userId} cannot add a pharmacy to user with id $uid")

        usersService.addFavoritePharmacy(user = user, pharmacyId = pid)
    }

    /**
     * Handles the request to remove a pharmacy from the user's favorite pharmacies.
     *
     * @param uid the id of the user to add the pharmacy to
     * @param pid the id of the pharmacy to be added
     * @return the response to the request
     */
    @DeleteMapping(Uris.USER_FAVORITE_PHARMACIES_GET_BY_ID)
    @Authenticated
    fun removeFavoritePharmacy(
        @RequestAttribute(AuthenticationInterceptor.USER_ATTRIBUTE) user: User,
        @PathVariable uid: Long,
        @PathVariable pid: Long
    ) {
        if (user.userId != uid)
            throw RuntimeException("User with id ${user.userId} does not correspond to the path variable $uid")

        usersService.removeFavoritePharmacy(user = user, pharmacyId = pid)
    }

    /**
     * Handles the request to flag a pharmacy.
     *
     * @param uid the id of the user to flag the pharmacy
     * @param pid the id of the pharmacy to be flagged
     */
    @PutMapping(Uris.USER_FLAGGED_PHARMACIES_GET_BY_ID)
    @Authenticated
    fun flagPharmacy(
        @RequestAttribute(AuthenticationInterceptor.USER_ATTRIBUTE) user: User,
        @PathVariable uid: Long,
        @PathVariable pid: Long
    ) {
        if (user.userId != uid)
            throw RuntimeException("User with id ${user.userId} does not correspond to the path variable $uid")

        usersService.flagPharmacy(user = user, pharmacyId = pid)
    }

    /**
     * Handles the request to unflag a pharmacy.
     *
     * @param uid the id of the user to unflag the pharmacy
     * @param pid the id of the pharmacy to be unflagged
     */
    @DeleteMapping(Uris.USER_FLAGGED_PHARMACIES_GET_BY_ID)
    @Authenticated
    fun unflagPharmacy(
        @RequestAttribute(AuthenticationInterceptor.USER_ATTRIBUTE) user: User,
        @PathVariable uid: Long,
        @PathVariable pid: Long
    ) {
        if (user.userId != uid)
            throw RuntimeException("User with id ${user.userId} does not correspond to the path variable $uid")

        usersService.unflagPharmacy(user = user, pharmacyId = pid)
    }

    @PutMapping(Uris.USER_MEDICINE_NOTIFICATIONS)
    @Authenticated
    fun addMedicineNotification(
        @RequestAttribute(AuthenticationInterceptor.USER_ATTRIBUTE) user: User,
        @PathVariable uid: Long,
        @PathVariable mid: Long
    ) {
        if (user.userId != uid)
            throw RuntimeException("User with id ${user.userId} does not correspond to the path variable $uid")

        usersService.addMedicineNotification(user = user, medicineId = mid)
    }

    @DeleteMapping(Uris.USER_MEDICINE_NOTIFICATIONS)
    @Authenticated
    fun removeMedicineNotification(
        @RequestAttribute(AuthenticationInterceptor.USER_ATTRIBUTE) user: User,
        @PathVariable uid: Long,
        @PathVariable mid: Long
    ) {
        if (user.userId != uid)
            throw RuntimeException("User with id ${user.userId} does not correspond to the path variable $uid")

        usersService.removeMedicineNotification(user = user, medicineId = mid)
    }
}