package pt.ulisboa.ist.pharmacist.service.users

import org.springframework.stereotype.Service
import pt.ulisboa.ist.pharmacist.domain.users.AccessToken
import pt.ulisboa.ist.pharmacist.domain.users.User
import pt.ulisboa.ist.pharmacist.repository.medicines.MedicinesRepository
import pt.ulisboa.ist.pharmacist.repository.pharmacies.PharmaciesRepository
import pt.ulisboa.ist.pharmacist.repository.users.UsersRepository
import pt.ulisboa.ist.pharmacist.service.exceptions.AlreadyExistsException
import pt.ulisboa.ist.pharmacist.service.exceptions.InvalidLoginException
import pt.ulisboa.ist.pharmacist.service.exceptions.InvalidPasswordException
import pt.ulisboa.ist.pharmacist.service.exceptions.NotFoundException
import pt.ulisboa.ist.pharmacist.service.users.dtos.UserDto
import pt.ulisboa.ist.pharmacist.service.users.dtos.login.LoginOutputDto
import pt.ulisboa.ist.pharmacist.service.users.dtos.register.RegisterOutputDto
import pt.ulisboa.ist.pharmacist.service.utils.HashingUtils
import java.util.UUID

/**
 * Service that handles the business logic of the users.
 *
 * @property usersRepository the repository of the users
 * @property pharmaciesRepository the repository of the pharmacies
 * @property hashingUtils the utils for password operations
 */
@Service
class UsersServiceImpl(
    private val usersRepository: UsersRepository,
    private val pharmaciesRepository: PharmaciesRepository,
    private val medicinesRepository: MedicinesRepository,
    private val hashingUtils: HashingUtils,
) : UsersService {

    override fun register(username: String, password: String): RegisterOutputDto {
        if (usersRepository.existsByUsername(username = username))
            throw AlreadyExistsException("User with username $username already exists")

        if (password.length < MIN_PASSWORD_LENGTH)
            throw InvalidPasswordException("Password must be at least $MIN_PASSWORD_LENGTH characters long")

        val user = usersRepository.create(
            username = username,
            passwordHash = hashingUtils.hashPassword(
                username = username,
                password = password
            )
        )

        val accessToken = UUID.randomUUID().toString()
        val tokenHash = hashingUtils.hashToken(token = accessToken)

        user.accessTokens.add(AccessToken(tokenHash = tokenHash))

        return RegisterOutputDto(
            userId = user.userId,
            accessToken = accessToken
        )
    }

    override fun login(username: String, password: String): LoginOutputDto {
        val user = usersRepository
            .findByUsername(username = username)
            ?: throw InvalidLoginException("Invalid username or password")

        if (
            !hashingUtils.checkPassword(
                username = username,
                password = password,
                passwordHash = user.passwordHash
            )
        ) throw InvalidLoginException("Invalid username or password")

        val accessToken = UUID.randomUUID().toString()
        val tokenHash = hashingUtils.hashToken(token = accessToken)

        user.accessTokens.add(AccessToken(tokenHash = tokenHash))

        return LoginOutputDto(
            userId = user.userId,
            accessToken = accessToken
        )
    }

    override fun upgrade(user: User, username: String, password: String) {
        if (usersRepository.existsByUsername(username = username))
            throw AlreadyExistsException("User with username $username already exists")

        if (password.length < MIN_PASSWORD_LENGTH)
            throw InvalidPasswordException("Password must be at least $MIN_PASSWORD_LENGTH characters long")

        user.username = username
        user.passwordHash = hashingUtils.hashPassword(
            username = username,
            password = password
        )
    }

    override fun logout(user: User, accessToken: String) {
        val tokenHash = hashingUtils.hashToken(token = accessToken)

        user.accessTokens.remove(AccessToken(tokenHash = tokenHash))
    }

    override fun getUser(userId: Long): UserDto {
        val user = usersRepository
            .findById(userId)
            ?: throw NotFoundException("User with id $userId not found")

        return UserDto(user = user)
    }

    override fun addFavoritePharmacy(user: User, pharmacyId: Long) {
        pharmaciesRepository.findById(pharmacyId) ?: throw NotFoundException("Pharmacy with id $pharmacyId not found")

        usersRepository.addFavoritePharmacy(userId = user.userId, pharmacyId = pharmacyId)
    }

    override fun removeFavoritePharmacy(user: User, pharmacyId: Long) {
        pharmaciesRepository.findById(pharmacyId) ?: throw NotFoundException("Pharmacy with id $pharmacyId not found")

        usersRepository.removeFavoritePharmacy(userId = user.userId, pharmacyId = pharmacyId)
    }

    override fun flagPharmacy(user: User, pharmacyId: Long) {
        pharmaciesRepository.findById(pharmacyId) ?: throw NotFoundException("Pharmacy with id $pharmacyId not found")

        usersRepository.flagPharmacy(userId = user.userId, pharmacyId = pharmacyId)
    }

    override fun unflagPharmacy(user: User, pharmacyId: Long) {
        pharmaciesRepository.findById(pharmacyId) ?: throw NotFoundException("Pharmacy with id $pharmacyId not found")

        usersRepository.unflagPharmacy(userId = user.userId, pharmacyId = pharmacyId)
    }

    override fun addMedicineNotification(user: User, medicineId: Long) {
        val medicine = medicinesRepository.findById(medicineId)
            ?: throw NotFoundException("Medicine with id $medicineId not found")

        user.medicinesToNotify.add(medicine)
    }

    override fun removeMedicineNotification(user: User, medicineId: Long) {
        val medicine = medicinesRepository.findById(medicineId)
            ?: throw NotFoundException("Medicine with id $medicineId not found")

        user.medicinesToNotify.remove(medicine)
    }

    companion object {
        private const val MIN_PASSWORD_LENGTH = 8
    }
}
