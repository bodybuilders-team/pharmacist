package pt.ulisboa.ist.pharmacist.repository.users

import pt.ulisboa.ist.pharmacist.domain.users.RevokedAccessToken

/**
 * Repository for the [RevokedAccessToken] entity.
 */
interface RevokedAccessTokensRepository  {

    /**
     * Finds a revoked access token by its token.
     *
     * @param token the token of the revoked access token
     * @return the revoked access token with the token
     */
    fun findByToken(token: String): RevokedAccessToken?

    /**
     * Saves a revoked access token.
     *
     * @param revokedAccessToken the revoked access token to be saved
     * @return the saved revoked access token
     */
    fun save(revokedAccessToken: RevokedAccessToken): RevokedAccessToken

    /**
     * Deletes a revoked access token.
     *
     * @param revokedAccessToken the revoked access token to be deleted
     */
    fun delete(revokedAccessToken: RevokedAccessToken)
}
