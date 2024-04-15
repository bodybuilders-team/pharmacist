package pt.ulisboa.ist.pharmacist.service.medicines

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import pt.ulisboa.ist.pharmacist.repository.medicines.MedicinesRepository
import pt.ulisboa.ist.pharmacist.repository.users.RefreshTokensRepository
import pt.ulisboa.ist.pharmacist.repository.users.RevokedAccessTokensRepository
import pt.ulisboa.ist.pharmacist.service.utils.HashingUtils
import pt.ulisboa.ist.pharmacist.utils.JwtProvider
import pt.ulisboa.ist.pharmacist.utils.ServerConfiguration

/**
 * Service that handles the business logic of the medicines.
 *
 * @property pharmaciesRepository the repository of the pharmacies
 * @property refreshTokensRepository the repository of the refresh tokens
 * @property hashingUtils the utils for password operations
 * @property jwtProvider the JWT provider
 * @property config the server configuration
 */
@Service
@Transactional(rollbackFor = [Exception::class])
class MedicinesServiceImpl(
    private val medicinesRepository: MedicinesRepository,
    private val revokedAccessTokensRepository: RevokedAccessTokensRepository,
    private val refreshTokensRepository: RefreshTokensRepository,
    private val hashingUtils: HashingUtils,
    private val jwtProvider: JwtProvider,
    private val config: ServerConfiguration
) : MedicinesService {
    // TODO: Implement the methods of the MedicinesServiceImpl interface
}
