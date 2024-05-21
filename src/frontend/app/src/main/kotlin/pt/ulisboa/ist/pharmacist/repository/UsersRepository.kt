package pt.ulisboa.ist.pharmacist.repository

import pt.ulisboa.ist.pharmacist.repository.remote.users.UsersApi
import javax.inject.Inject

class UsersRepository @Inject constructor(
    private val usersNetworkDataSource: UsersApi//,
    //private val usersLocalDataSource: UserDao
) {
    // TODO: Implement the methods
}