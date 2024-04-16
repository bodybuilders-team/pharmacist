package pt.ulisboa.ist.pharmacist.repository.users

import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import pt.ulisboa.ist.pharmacist.domain.users.RefreshToken
import pt.ulisboa.ist.pharmacist.domain.users.User


/**
 * Repository for the [RefreshToken] entity.
 */
interface RefreshTokensRepository {

    /**
     * Finds a refresh token by its token.
     *
     * @param token the token of the refresh token
     * @return the refresh token with the token
     */
    fun findByToken(token: String): RefreshToken?

    /**
     * Saves a refresh token.
     *
     * @param refreshToken the refresh token to be saved
     * @return the saved refresh token
     */
    fun save(refreshToken: RefreshToken): RefreshToken

    /**
     * Deletes a refresh token.
     *
     * @param refreshToken the refresh token to be deleted
     */
    fun delete(refreshToken: RefreshToken)

    fun findByUserAndTokenHash(user: User, tokenHash: String): RefreshToken?

    fun countByUser(user: User): Long

    fun getRefreshTokensOfUserOrderedByExpirationDate(user: User, pageable: PageRequest): Page<RefreshToken>
}
