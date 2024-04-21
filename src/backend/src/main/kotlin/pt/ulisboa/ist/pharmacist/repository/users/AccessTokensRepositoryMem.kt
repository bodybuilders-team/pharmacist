package pt.ulisboa.ist.pharmacist.repository.users

import java.sql.Timestamp
import org.springframework.stereotype.Repository
import pt.ulisboa.ist.pharmacist.domain.users.AccessToken
import pt.ulisboa.ist.pharmacist.domain.users.User
import pt.ulisboa.ist.pharmacist.repository.MemDataSource

@Repository
class AccessTokensRepositoryMem(dataSource: MemDataSource) : AccessTokensRepository {

    val accessTokens = dataSource.accessTokens

    override fun findByToken(token: String): AccessToken? {
        return accessTokens.find { it.tokenHash == token }
    }

    override fun create(user: User, tokenHash: String, expirationDate: Timestamp): AccessToken {
        val accessToken = AccessToken(user, tokenHash, expirationDate)
        accessTokens.add(accessToken)
        return accessToken
    }

    override fun delete(accessToken: AccessToken) {
        accessTokens.remove(accessToken)
    }
}