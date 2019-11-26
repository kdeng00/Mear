//
// Created by brahmix on 10/3/19.
//

#include "SongRepository.h"

#include <curl/curl.h>
#include <nlohmann/json.hpp>

namespace repository {
    /**
    std::vector<model::Song> SongRepository::fetchSongs(const model::Token& token,
        const std::string& uri)
    {
    }
     */


    /**
    model::Song SongRepository::retrieveSong(const model::Token& token, const model::Song& sng,
            const std::string& baseUri) {
    }
     */


    /**
    size_t SongRepository::respBodyRetriever(void* ptr, size_t size, size_t nmemb, char *e)
    {
        ((std::string*)e)->append((char*)ptr, size * nmemb);
        return size * nmemb;
    }
     */
}
