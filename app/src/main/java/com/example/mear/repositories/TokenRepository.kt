package com.example.mear.repositories

import com.example.mear.models.Token

class TokenRepository: BaseRepository() {

    companion object {
        init {
            System.loadLibrary("native-lib")
        }
    }


    private external fun retrieveTokenRecord(path: String): Token

    private external fun isTokenTableEmpty(path: String): Boolean

    private external fun saveTokenRecord(token: Token, path: String)
    //private external fun deleteTokenRecord(path: String)
    //private external fun updateTokenRecord(token: Token, path: String)


    fun retrieveToken(path: String): Token {
        return retrieveTokenRecord(path)
    }


    fun isTableEmpty(path: String): Boolean {
        return isTokenTableEmpty(path)
    }


    fun saveToken(token: Token, path: String) {
        return saveTokenRecord(token, path)
    }

    // TODO: implement this
    /**
    fun deleteToken(path: String) {
        return deleteTokenRecord(path)
    }

    fun updateToken(token: Token, path: String) {
        return updateTokenRecord(token, path)
    }
    */
}