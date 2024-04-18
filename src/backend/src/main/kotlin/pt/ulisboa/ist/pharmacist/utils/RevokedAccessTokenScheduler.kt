package pt.ulisboa.ist.pharmacist.utils

import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import pt.ulisboa.ist.pharmacist.repository.users.AccessTokensRepository
import java.sql.Timestamp
import java.time.Instant

@Component
class AccessTokenScheduler(
    private val AccessTokensRepository: AccessTokensRepository
) {

    @Scheduled(fixedRate = REVOKED_ACCESS_TOKEN_CLEANUP_INTERVAL)
    @Transactional(rollbackFor = [Exception::class])
    fun removeExpiredTokens() {
        AccessTokensRepository.deleteAllByExpirationDateBefore(Timestamp.from(Instant.now()))
    }

    companion object {
        private const val REVOKED_ACCESS_TOKEN_CLEANUP_INTERVAL = 1000L * 60L * 60L * 8L // Every 8 hours
    }
}