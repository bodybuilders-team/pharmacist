package pt.ulisboa.ist.pharmacist.domain.users

import pt.ulisboa.ist.pharmacist.domain.exceptions.InvalidUserException
import pt.ulisboa.ist.pharmacist.domain.medicines.Medicine
import pt.ulisboa.ist.pharmacist.domain.pharmacies.Pharmacy

/**
 * The User entity.
 *
 * @property userId the id of the user
 * @property username the username of the user
 * @property passwordHash the hashed password of the user
 * @property suspended if the user is suspended
 * @property favoritePharmacies the pharmacies marked as favorite by the user
 * @property medicinesToNotify the medicines the user wants to be notified about
 * @property accessTokens the access tokens of the user
 * @property ratings the ratings the user has given to pharmacies
 */
data class User(
    val userId: Long,
    val username: String,
    val passwordHash: String,
    val suspended: Boolean,
    val favoritePharmacies: MutableSet<Pharmacy> = mutableSetOf(),
    val medicinesToNotify: MutableSet<Medicine> = mutableSetOf(),
    val accessTokens: MutableSet<AccessToken> = mutableSetOf(),
    val ratings: MutableMap<Long, Int> = mutableMapOf()
) {

    init {
        if (username.length !in MIN_USERNAME_LENGTH..MAX_USERNAME_LENGTH)
            throw InvalidUserException(
                "Username must be between $MIN_USERNAME_LENGTH and $MAX_USERNAME_LENGTH characters long."
            )

        if (passwordHash.length != PASSWORD_HASH_LENGTH)
            throw InvalidUserException("Password hash must have a length of $PASSWORD_HASH_LENGTH.")
    }

    companion object {
        private const val MIN_USERNAME_LENGTH = 3
        private const val MAX_USERNAME_LENGTH = 128

        const val PASSWORD_HASH_LENGTH = 128
    }
}