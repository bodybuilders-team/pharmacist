package pt.ulisboa.ist.pharmacist.domain.users

import pt.ulisboa.ist.pharmacist.domain.exceptions.InvalidAccessTokenException

/**
 * The AccessToken entity.
 *
 * @property tokenHash the token hash
 */
data class AccessToken(val tokenHash: String) {

    init {
        if (tokenHash.isBlank())
            throw InvalidAccessTokenException("Token hash cannot be blank")

    }

    // Use only  token hash for equals and hashcode, not other fields
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AccessToken) return false

        if (tokenHash != other.tokenHash) return false

        return true
    }

    override fun hashCode(): Int {
        return tokenHash.hashCode()
    }
}
