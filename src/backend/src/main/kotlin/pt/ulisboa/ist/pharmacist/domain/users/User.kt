package pt.ulisboa.ist.pharmacist.domain.users

import pt.ulisboa.ist.pharmacist.domain.exceptions.InvalidUserException
import pt.ulisboa.ist.pharmacist.domain.medicines.Medicine
import pt.ulisboa.ist.pharmacist.domain.pharmacies.Pharmacy

/**
 * The User entity.
 *
 * @property userId the id of the user
 * @property username the username of the user
 * @property email the email of the user
 * @property passwordHash the hashed password of the user
 */
data class User(
    val userId: String,
    val username: String,
    val email: String,
    val passwordHash: String,
    val suspended: Boolean,
    val favoritePharmacies: MutableSet<Pharmacy> = mutableSetOf(),
    val medicinesToNotify: MutableSet<Medicine> = mutableSetOf(),
    val accessTokens: MutableSet<AccessToken> = mutableSetOf(),
    val ratings: MutableMap<Long, UserPharmacyRating> = mutableMapOf()
) {

    init {
        if (username.length !in MIN_USERNAME_LENGTH..MAX_USERNAME_LENGTH)
            throw InvalidUserException(
                "Username must be between $MIN_USERNAME_LENGTH and $MAX_USERNAME_LENGTH characters long."
            )

        if (!email.matches(EMAIL_REGEX.toRegex()))
            throw InvalidUserException("Email must be a valid email address.")

        if (passwordHash.length != PASSWORD_HASH_LENGTH)
            throw InvalidUserException("Password hash must have a length of $PASSWORD_HASH_LENGTH.")
    }

    companion object {
        private const val MIN_USERNAME_LENGTH = 3
        private const val MAX_USERNAME_LENGTH = 40

        private const val EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@(.+)\$"

        const val PASSWORD_HASH_LENGTH = 128
    }
}