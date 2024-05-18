package pt.ulisboa.ist.pharmacist.repository

import pt.ulisboa.ist.pharmacist.repository.network.services.users.UsersService
import javax.inject.Inject

class UsersRepository @Inject constructor(
    private val usersNetworkDataSource: UsersService//,
    //private val usersLocalDataSource: UserDao
) {
    // TODO: Implement the methods
}