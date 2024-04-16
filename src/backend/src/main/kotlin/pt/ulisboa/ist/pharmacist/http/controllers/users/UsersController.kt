package pt.ulisboa.ist.pharmacist.http.controllers.users

import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseCookie
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
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
import pt.ulisboa.ist.pharmacist.http.controllers.users.models.refreshToken.RefreshTokenInputModel
import pt.ulisboa.ist.pharmacist.http.controllers.users.models.refreshToken.RefreshTokenOutputModel
import pt.ulisboa.ist.pharmacist.http.controllers.users.models.register.RegisterInputModel
import pt.ulisboa.ist.pharmacist.http.controllers.users.models.register.RegisterOutputModel
import pt.ulisboa.ist.pharmacist.http.pipeline.authentication.Authenticated
import pt.ulisboa.ist.pharmacist.http.utils.Params
import pt.ulisboa.ist.pharmacist.http.utils.Uris
import pt.ulisboa.ist.pharmacist.service.exceptions.AuthenticationException
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
        @RequestParam(Params.OFFSET_PARAM) offset: Int? = null,
        @RequestParam(Params.LIMIT_PARAM) limit: Int? = null,
        @RequestParam(Params.ORDER_BY_PARAM) orderBy: String? = null,
        @RequestParam(Params.SORT_DIRECTION_PARAM) sortDirection: String? = null
    ): GetUsersOutputModel {
        val usersDTO = usersService.getUsers(
            offset = offset ?: Params.OFFSET_DEFAULT,
            limit = limit ?: Params.LIMIT_DEFAULT,
            orderBy = if (orderBy != null) UsersOrder.valueOf(orderBy) else UsersOrder.POINTS,
            ascending = when (sortDirection ?: Params.SORT_DIR_DESCENDING) {
                Params.SORT_DIR_ASCENDING -> true
                Params.SORT_DIR_DESCENDING -> false
                else -> throw IllegalArgumentException(
                    "Invalid sort order, must be ${Params.SORT_DIR_ASCENDING} or ${Params.SORT_DIR_DESCENDING}"
                )
            }
        )

        return GetUsersOutputModel(
            totalCount = usersDTO.totalCount,
            users = usersDTO.users.map { GetUserOutputModel(it) })
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
        val registerDTO = usersService.register(registerInputDTO = userData.toRegisterInputDTO())

        return RegisterOutputModel(registerDTO)
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
        userData: LoginInputModel,
        response: HttpServletResponse
    ): LoginOutputModel {
        val loginDTO = usersService.login(loginInputDTO = userData.toLoginInputDTO())

        setAuthenticationCookies(response, loginDTO.accessToken, loginDTO.refreshToken)

        return LoginOutputModel(loginDTO)
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
        @RequestAttribute(JwtProvider.ACCESS_TOKEN_ATTRIBUTE) accessToken: String,
        @RequestAttribute(JwtProvider.REFRESH_TOKEN_ATTRIBUTE, required = false) refreshToken: String?,
        response: HttpServletResponse
    ): LogoutUserOutputModel {
        clearAuthenticationCookies(response)

        usersService.logout(
            accessToken = accessToken,
            refreshToken =
            // The refresh token may be received from cookies or from the request body
            (refreshToken ?: logoutUserInputModel?.refreshToken)
                ?: throw AuthenticationException("Refresh token is required")
        )

        return LogoutUserOutputModel("User logged out successfully")
    }

    /**
     * Handles the request to refresh the token of a user.
     *
     * @param refreshTokenInputModel the data of the user to be refreshed
     * @return the response to the request with the new token of the user
     */
    @PostMapping(Uris.USERS_REFRESH_TOKEN)
    @Authenticated
    fun refreshToken(
        @Valid @RequestBody(required = false)
        refreshTokenInputModel: RefreshTokenInputModel?,
        @RequestAttribute(JwtProvider.ACCESS_TOKEN_ATTRIBUTE) accessToken: String,
        @RequestAttribute(JwtProvider.REFRESH_TOKEN_ATTRIBUTE, required = false) refreshToken: String?,
        response: HttpServletResponse
    ): RefreshTokenOutputModel {
        val refreshDTO = usersService.refreshToken(
            accessToken = accessToken,
            // The refresh token may be received from cookies or from the request body
            (refreshToken ?: refreshTokenInputModel?.refreshToken)
                ?: throw AuthenticationException("Refresh token is required")
        )

        setAuthenticationCookies(response, refreshDTO.accessToken, refreshDTO.refreshToken)

        return RefreshTokenOutputModel(refreshDTO)
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
        val userDTO = usersService.getUser(userId = uid)

        return GetUserOutputModel(userDTO)
    }

    companion object {

        private fun setAuthenticationCookies(
            response: HttpServletResponse,
            accessToken: String,
            refreshToken: String
        ) {
            val accessTokenCookie = ResponseCookie.from("access_token", accessToken)
                .httpOnly(true)
                .path("/")
                .maxAge(JwtProvider.accessTokenDuration)
                .sameSite("Strict")
                .build()

            val refreshTokenCookie = ResponseCookie.from("refresh_token", refreshToken)
                .httpOnly(true)
                .path("/")
                .maxAge(JwtProvider.refreshTokenDuration)
                .sameSite("Strict")
                .build()

            response.addCookie(accessTokenCookie)
            response.addCookie(refreshTokenCookie)
        }

        private fun clearAuthenticationCookies(response: HttpServletResponse) {
            val accessTokenCookie = ResponseCookie.from("access_token", "")
                .httpOnly(true)
                .maxAge(0)
                .sameSite("Strict")
                .build()

            val refreshTokenCookie = ResponseCookie.from("refresh_token", "")
                .httpOnly(true)
                .maxAge(0)
                .sameSite("Strict")
                .build()

            response.addCookie(accessTokenCookie)
            response.addCookie(refreshTokenCookie)
        }

        private fun HttpServletResponse.addCookie(cookie: ResponseCookie) {
            this.addHeader(HttpHeaders.SET_COOKIE, cookie.toString())
        }
    }
}