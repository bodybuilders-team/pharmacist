package pt.ulisboa.ist.pharmacist.utils

import org.springframework.stereotype.Component
import pt.ulisboa.ist.pharmacist.repository.users.AccessTokensRepository

@Component
class AccessTokenScheduler(
    private val accessTokensRepository: AccessTokensRepository
) {

    /*@Scheduled(fixedRate = REVOKED_ACCESS_TOKEN_CLEANUP_INTERVAL)
        fun removeExpiredTokens() {
        AccessTokensRepository.deleteAllByExpirationDateBefore(Timestamp.from(Instant.now()))
    }

    companion object {
        private const val REVOKED_ACCESS_TOKEN_CLEANUP_INTERVAL = 1000L * 60L * 60L * 8L // Every 8 hours
    }*/
}