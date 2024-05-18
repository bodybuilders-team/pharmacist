package pt.ulisboa.ist.pharmacist.repository

import javax.inject.Inject

class PharmacistRepository @Inject constructor(
    val uploaderRepository: UploaderRepository,
    val usersRepository: UsersRepository,
    val pharmaciesRepository: PharmaciesRepository,
    val medicinesRepository: MedicinesRepository
)