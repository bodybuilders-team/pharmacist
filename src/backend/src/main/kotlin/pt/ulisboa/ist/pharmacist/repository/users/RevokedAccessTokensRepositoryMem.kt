package pt.ulisboa.ist.pharmacist.repository.users

import org.springframework.stereotype.Repository
import pt.ulisboa.ist.pharmacist.domain.users.RevokedAccessToken

@Repository
class RevokedAccessTokensRepositoryMem : RevokedAccessTokensRepository {

    private val revokedAccessTokens = mutableListOf<RevokedAccessToken>()

    override fun findByToken(token: String): RevokedAccessToken? {
        return revokedAccessTokens.find { it.tokenHash == token }
    }

    override fun save(revokedAccessToken: RevokedAccessToken): RevokedAccessToken {
        revokedAccessTokens.add(revokedAccessToken)
        return revokedAccessToken
    }

    override fun delete(revokedAccessToken: RevokedAccessToken) {
        revokedAccessTokens.remove(revokedAccessToken)
    }
}