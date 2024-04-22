package pt.ulisboa.ist.pharmacist.utils

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling

/**
 * Configuration of the server.
 *
 * @property passwordSecret the secret used to hash passwords
 * @property tokenHashSecret the secret used to hash tokens
 */
@Configuration
@EnableScheduling
class ServerConfiguration(
    @Value("\${server.config.secrets.password-secret}")
    val passwordSecret: String,

    @Value("\${server.config.secrets.token-hash-secret}")
    val tokenHashSecret: String,

    @Value("\${google.credentials}")
    val googleCredentials: String,

    @Value("\${google.projectId}")
    val googleProjectId: String,

    @Value("\${google.bucketName}")
    val googleBucketName: String,

    @Value("\${google.base-url}")
    val googleUrl: String
)