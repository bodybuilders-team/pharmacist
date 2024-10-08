package pt.ulisboa.ist.pharmacist.ui.screens.authentication

private const val MIN_USERNAME_LENGTH = 3
const val MAX_USERNAME_LENGTH = 40

private const val MIN_PASSWORD_LENGTH = 8
const val MAX_PASSWORD_LENGTH = 127


/**
 * Validates the username.
 *
 * @param username username
 * @return true if the username is valid, false otherwise
 */
fun validateUsername(username: String): Boolean =
    username.length in MIN_USERNAME_LENGTH..MAX_USERNAME_LENGTH

/**
 * Validates the password.
 *
 * @param password password
 * @return true if the password is valid, false otherwise
 */
fun validatePassword(password: String): Boolean =
    password.length in MIN_PASSWORD_LENGTH..MAX_PASSWORD_LENGTH
