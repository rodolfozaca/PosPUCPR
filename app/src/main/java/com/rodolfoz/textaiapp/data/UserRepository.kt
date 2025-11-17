/*
 * Rodolfo Zacarias 2025
 *
 * All rights reserved. This software is the property of Rodolfo Zacarias.
 * Reproduction, distribution, or modification without written permission is prohibited.
 *
 * Use is subject to a license agreement. The author is not liable for any
 * direct or indirect damages resulting from use of this software.
 */
package com.rodolfoz.textaiapp.data

import com.rodolfoz.textaiapp.data.model.UserDataModel
import java.security.MessageDigest

/**
 * Repository that abstracts data access to user data.
 *
 * Encapsulates the DAO and provides higher-level operations such as authentication
 * and setting credentials with hashing.
 */
class UserRepository(private val userDataDao: UserDataDao) {

    suspend fun insertUser(user: UserDataModel) {
        userDataDao.insertUser(user)
    }

    suspend fun updateUser(user: UserDataModel) {
        userDataDao.updateUser(user)
    }

    suspend fun deleteUser(user: UserDataModel) {
        userDataDao.deleteUser(user)
    }

    suspend fun getUserById(id: Int): UserDataModel? {
        return userDataDao.getUserById(id)
    }

    suspend fun getUserByLogin(login: String): UserDataModel? {
        return userDataDao.getUserByLogin(login)
    }

    /**
     * Set login and password (hashed) for a user by id.
     */
    suspend fun setCredentialsForUser(id: Int, login: String, rawPassword: String) {
        val user = userDataDao.getUserById(id) ?: return
        user.login = login
        user.password = hash(rawPassword)
        userDataDao.updateUser(user)
    }

    suspend fun setFirebaseUidForUser(id: Int, firebaseUid: String) {
        val user = userDataDao.getUserById(id) ?: return
        user.firebaseUid = firebaseUid
        userDataDao.updateUser(user)
    }

    suspend fun setFirebaseUidForUserByLogin(login: String, firebaseUid: String) {
        val user = userDataDao.getUserByLogin(login) ?: return
        user.firebaseUid = firebaseUid
        userDataDao.updateUser(user)
    }

    /**
     * Authenticate a user by login and raw password. Returns true if credentials match.
     */
    suspend fun authenticate(login: String, rawPassword: String): Boolean {
        val user = userDataDao.getUserByLogin(login) ?: return false
        return user.password == hash(rawPassword)
    }

    private fun hash(input: String): String {
        val bytes = input.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.joinToString("") { "%02x".format(it) }
    }
}
