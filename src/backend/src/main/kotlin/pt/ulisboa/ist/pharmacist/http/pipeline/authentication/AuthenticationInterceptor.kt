package pt.ulisboa.ist.pharmacist.http.pipeline.authentication

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerInterceptor
import pt.ulisboa.ist.pharmacist.repository.users.UsersRepository
import pt.ulisboa.ist.pharmacist.service.exceptions.AuthenticationException
import pt.ulisboa.ist.pharmacist.service.utils.HashingUtils

/**
 * Intercepts requests that need authentication.
 *
 * The interceptor checks:
 * 1. If the request has an Authorization header
 * 2. If the token in the header is a bearer token
 * 3. If the token is valid
 */
@Component
class AuthenticationInterceptor(
    private val usersRepository: UsersRepository,
    private val hashingUtils: HashingUtils
) : HandlerInterceptor {

    override fun preHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any
    ): Boolean {
        if (
            handler !is HandlerMethod ||
            (
                    !handler.hasMethodAnnotation(Authenticated::class.java) &&
                            !handler.method.declaringClass.isAnnotationPresent(Authenticated::class.java)
                    )
        ) return true

        val accessToken = parseBearerToken(
            request.getHeader(AUTHORIZATION_HEADER)
                ?: throw AuthenticationException("Missing authorization token")
        ) ?: throw AuthenticationException("Token is not a Bearer Token")

        val tokenHash = hashingUtils.hashToken(accessToken)
        val user = usersRepository.findByAccessTokenHash(accessToken = tokenHash)
            ?: throw AuthenticationException("Invalid access token")

        request.setAttribute(USER_ATTRIBUTE, user)
        request.setAttribute(ACCESS_TOKEN_ATTRIBUTE, accessToken)

        return true
    }

    companion object {
        private const val AUTHORIZATION_HEADER = "Authorization"
        private const val BEARER_TOKEN_PREFIX = "Bearer "

        const val USER_ATTRIBUTE = "user"
        const val ACCESS_TOKEN_ATTRIBUTE = "access_token"

        /**
         * Parses the bearer token.
         *
         * @param token the token to parse
         * @return the parsed bearer token or null if the token is not a bearer token
         */
        fun parseBearerToken(token: String): String? =
            if (!token.startsWith(prefix = BEARER_TOKEN_PREFIX))
                null
            else
                token.substringAfter(delimiter = BEARER_TOKEN_PREFIX)
    }
}