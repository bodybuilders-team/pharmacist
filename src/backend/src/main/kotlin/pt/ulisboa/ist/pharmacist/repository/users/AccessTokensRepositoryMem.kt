package pt.ulisboa.ist.pharmacist.repository.users

import org.springframework.stereotype.Repository
import pt.ulisboa.ist.pharmacist.domain.users.AccessToken

@Repository
class AccessTokensRepositoryMem : AccessTokensRepository {

    private val accessTokens = mutableListOf<AccessToken>()

    override fun findByToken(token: String): AccessToken? {
        return accessTokens.find { it.tokenHash == token }
    }

    override fun save(accessToken: AccessToken): AccessToken {
        accessTokens.add(accessToken)
        return accessToken
    }

    override fun delete(accessToken: AccessToken) {
        accessTokens.remove(accessToken)
    }
}