package pt.ulisboa.ist.pharmacist.repository.users

import org.springframework.stereotype.Repository
import pt.ulisboa.ist.pharmacist.domain.users.RefreshToken


@Repository
class RefreshTokensRepositoryMem : RefreshTokensRepository {

    private val refreshTokens = mutableListOf<RefreshToken>()

    override fun findByToken(token: String): RefreshToken? {
        return refreshTokens.find { it.tokenHash == token }
    }

    override fun save(refreshToken: RefreshToken): RefreshToken {
        refreshTokens.add(refreshToken)
        return refreshToken
    }

    override fun delete(refreshToken: RefreshToken) {
        refreshTokens.remove(refreshToken)
    }
}