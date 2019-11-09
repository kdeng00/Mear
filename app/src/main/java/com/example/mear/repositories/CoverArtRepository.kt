package com.example.mear.repositories

import com.example.mear.models.Token

class CoverArtRepository : BaseRepository() {

    private external fun retrieveCoverArtImage(token: Token, coverArt: CoverArt, apiUri: String): ByteArray


    fun fetchCoverArtImage(token: Token, coverArt: CoverArt, apiUri: String): ByteArray {
        val img = retrieveCoverArtImage(token, coverArt, apiUri)

        return img
    }

    // TODO: Move this to it's own file later on
    class CoverArt (var id: Int = 0, var title: String = "")
}