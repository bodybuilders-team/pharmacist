package pt.ulisboa.ist.pharmacist.domain.users

/**
 * The AccessToken entity.
 */
data class AccessToken(val tokenHash: String) {

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
