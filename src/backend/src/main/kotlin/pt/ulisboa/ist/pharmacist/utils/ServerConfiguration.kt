package pt.ulisboa.ist.pharmacist.utils

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling

/**
 * Configuration of the server.
 *
 * @property passwordSecret the secret used to sign the passwords
 * @property tokenHashSecret the secret used to sign the token hashes
 */
@Configuration
@EnableScheduling
class ServerConfiguration(
    @Value("\${server.config.secrets.password-secret}")
    val passwordSecret: String,

    @Value("\${server.config.secrets.token-hash-secret}")
    val tokenHashSecret: String,

    )