package pt.ulisboa.ist.pharmacist.domain.users

import pt.ulisboa.ist.pharmacist.domain.exceptions.InvalidRefreshTokenException
import java.sql.Timestamp

/**
 * The AccessToken entity.
 *
 * @property id the id of the AccessToken
 * @property user the user that owns the refresh token
 * @property tokenHash the revoked access token
 * @property expirationDate the token's expiration date
 */
data class AccessToken(
    private var id: Int? = null,
    val user: User,
    val tokenHash: String,
    val expirationDate: Timestamp
) {
    init {
        if (tokenHash.length != TOKEN_HASH_LENGTH)
            throw InvalidRefreshTokenException("The token hash must have a length of $TOKEN_HASH_LENGTH")
    }

    companion object {
        const val TOKEN_HASH_LENGTH = 128
    }
}
