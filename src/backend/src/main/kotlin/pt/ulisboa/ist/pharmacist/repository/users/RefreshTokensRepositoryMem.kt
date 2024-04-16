package pt.ulisboa.ist.pharmacist.repository.users

import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Repository
import pt.ulisboa.ist.pharmacist.domain.users.RefreshToken
import pt.ulisboa.ist.pharmacist.domain.users.User


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

    override fun findByUserAndTokenHash(user: User, tokenHash: String): RefreshToken? {
        TODO("Not yet implemented")
    }

    override fun countByUser(user: User): Long {
        TODO("Not yet implemented")
    }

    override fun getRefreshTokensOfUserOrderedByExpirationDate(user: User, pageable: PageRequest): Page<RefreshToken> {
        TODO("Not yet implemented")
    }
}