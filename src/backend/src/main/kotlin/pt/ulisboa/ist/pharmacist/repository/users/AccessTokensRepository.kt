package pt.ulisboa.ist.pharmacist.repository.users

import pt.ulisboa.ist.pharmacist.domain.users.AccessToken

/**
 * Repository for the [AccessToken] entity.
 */
interface AccessTokensRepository  {

    /**
     * Finds a revoked access token by its token.
     *
     * @param token the token of the revoked access token
     * @return the revoked access token with the token
     */
    fun findByToken(token: String): AccessToken?

    /**
     * Saves a revoked access token.
     *
     * @param accessToken the revoked access token to be saved
     * @return the saved revoked access token
     */
    fun save(accessToken: AccessToken): AccessToken

    /**
     * Deletes a revoked access token.
     *
     * @param accessToken the revoked access token to be deleted
     */
    fun delete(accessToken: AccessToken)
}
