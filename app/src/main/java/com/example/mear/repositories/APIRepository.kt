package com.example.mear.repositories

import android.net.Uri
import com.example.mear.models.APIInfo
import com.example.mear.models.Song
import com.example.mear.models.Token

class APIRepository: BaseRepository() {

    companion object {
        init {
            System.loadLibrary("native-lib")
        }


        fun retrieveSongStreamHeader(token: Token): MutableMap<String, String> {
            val hddr: MutableMap<String, String> = mutableMapOf()
            hddr["Authorization"] = "Bearer ${token.accessToken}"
            hddr["Content-type"] = "Keep-alive"

            return hddr
        }


        fun retrieveSongStreamUri(apiInfo: APIInfo, song: Song): Uri {
            val uriStr = "${apiInfo.uri}/api/v${apiInfo.version}/song/stream/${song.id}"

            return Uri.parse(uriStr)
        }
    }


    private external fun retrieveAPIInfoRecord(path: String): APIInfo

    private external fun isAPIInfoTableEmpty(path: String): Boolean

    private external fun saveAPIInfoRecord(api: APIInfo, path: String)


    fun retrieveRecord(path: String): APIInfo {
        return retrieveAPIInfoRecord(path)
    }


    fun isTableEmpty(path: String): Boolean {
        return isAPIInfoTableEmpty(path)
    }


   fun saveRecord(api: APIInfo, path: String) {
       return saveAPIInfoRecord(api, path)
   }
}