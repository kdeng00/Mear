package mear.com.example.mear.repositories

import com.example.mear.models.APIInfo
import com.example.mear.repositories.BaseRepository

class APIRepository: BaseRepository() {

    companion object {
        init {
            System.loadLibrary("native-lib")
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


   fun SaveRecord(api: APIInfo, path: String) {
       return saveAPIInfoRecord(api, path)
   }
}