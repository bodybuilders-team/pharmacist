package pt.ulisboa.ist.pharmacist.repository.users

import java.sql.Timestamp
import pt.ulisboa.ist.pharmacist.domain.users.AccessToken
import pt.ulisboa.ist.pharmacist.domain.users.User

/**
 * Repository for the [AccessToken] entity.
 */
interface AccessTokensRepository {

    /**
     * Finds a revoked access token by its token.
     *
     * @param token the token of the revoked access token
     * @return the revoked access token with the token
     */
    fun findByToken(token: String): AccessToken?

    /**
     * Creates a revoked access token.
     *
     * @param accessToken the revoked access token to be saved
     * @return the saved revoked access token
     */
    fun create(user: User, tokenHash: String, expirationDate: Timestamp): AccessToken

    /**
     * Deletes a revoked access token.
     *
     * @param accessToken the revoked access token to be deleted
     */
    fun delete(accessToken: AccessToken)
}
