package com.example.mear.repositories

import com.example.mear.models.Token
import com.example.mear.models.User

class UserRepository : BaseRepository() {

    private external fun logUser(user: User, apiUri: String) : Token

    private external fun retrieveUserCredentials(path: String) : User

    private external fun isUserTableEmpty(path: String): Boolean

    private external fun saveUserCredentials(username: User)


    fun fetchToken(user: User, apiUri: String): Token {
        val usr = logUser(user, apiUri)

        return usr
    }


    fun retrieveCredentials(path: String): User {
        return retrieveUserCredentials(path)
    }


    fun isTableEmpty(path: String): Boolean {
        return isUserTableEmpty(path)
    }


    fun saveCredentials(user: User) {
        saveUserCredentials(user)
    }
}