//
// Created by brahmix on 10/3/19.
//

#include "SongRepository.h"

#include <curl/curl.h>
#include <nlohmann/json.hpp>

namespace repository {
    std::vector<model::Song> SongRepository::fetchSongs(const model::Token& token,
        const std::string& uri)
    {
        std::string fullUri(uri);
        if (fullUri.at(fullUri.size()-1) != '/') {
            fullUri.append("/");
        }
        fullUri.append("api/v1/song");
        std::vector<model::Song> songs;

        CURL *curl;
        CURLcode res;
        struct curl_slist *chunk = nullptr;
        curl = curl_easy_init();

        if (!curl) {
            return songs;
        }

        constexpr auto EXPECTED_CHAR_AMOUNT = 100000;
        auto resp = std::make_unique<char[]>(EXPECTED_CHAR_AMOUNT);

        std::string authInfo("Authorization: Bearer ");
        authInfo.append(token.accessToken);
        chunk = curl_slist_append(chunk, authInfo.c_str());

        curl_easy_setopt(curl, CURLOPT_URL, fullUri.c_str());
        curl_easy_setopt(curl, CURLOPT_HTTPHEADER, chunk);
        curl_easy_setopt(curl, CURLOPT_WRITEFUNCTION, respBodyRetriever);
        curl_easy_setopt(curl, CURLOPT_WRITEDATA, resp.get());
        curl_easy_setopt(curl, CURLOPT_SSL_VERIFYPEER, 0L);

        res = curl_easy_perform(curl);
        curl_easy_cleanup(curl);

        if (res == CURLE_OK) {
            auto songsJson = nlohmann::json::parse(resp.get());
            for (auto& songJson: songsJson) {
                model::Song song(songJson["id"].get<int>(), songJson["title"].get<std::string>(),
                        songJson["artist"].get<std::string>(), songJson["album"].get<std::string>(),
                        songJson["genre"].get<std::string>(), songJson["duration"].get<int>(),
                        songJson["year"].get<int>());
                song.coverArtId = songJson["coverart_id"].get<int>();

                songs.push_back(song);
            }
        }

      return songs;
    }


    model::Song SongRepository::retrieveSong(const model::Token& token, const model::Song& sng,
            const std::string& baseUri) {
        std::string uri(baseUri);
        uri.append("/api/v1/song/");
        uri.append(std::to_string(sng.id));
        model::Song song;

        CURL *curl;
        CURLcode res;
        struct curl_slist *chunk = NULL;
        curl = curl_easy_init();

        if (!curl) {
            return sng;
        }

        auto resp = std::make_unique<char[]>(10000);

        std::string authInfo("Authorization: Bearer ");
        authInfo.append(token.accessToken);
        chunk = curl_slist_append(chunk, authInfo.c_str());

        curl_easy_setopt(curl, CURLOPT_URL, uri.c_str());
        curl_easy_setopt(curl, CURLOPT_HTTPHEADER, chunk);
        curl_easy_setopt(curl, CURLOPT_WRITEFUNCTION, respBodyRetriever);
        curl_easy_setopt(curl, CURLOPT_WRITEDATA, resp.get());
        curl_easy_setopt(curl, CURLOPT_SSL_VERIFYPEER, 0L);

        res = curl_easy_perform(curl);
        curl_easy_cleanup(curl);

        if (res == CURLE_OK) {
            auto s = nlohmann::json::parse(resp.get());
            song.id = s["id"].get<int>();
            song.title = s["title"].get<std::string>();
            song.album = s["album"].get<std::string>();
            song.artist = s["artist"].get<std::string>();
            song.genre = s["genre"].get<std::string>();
            song.duration = s["duration"].get<int>();
            song.year = s["year"].get<int>();
            song.coverArtId = s["coverart_id"].get<int>();

            return song;
        }

        return sng;
    }


    size_t SongRepository::respBodyRetriever(void* ptr, size_t size, size_t nmemb, char *e)
    {
        std::memcpy(e, ptr, nmemb);
        e[nmemb] = '\0';

        return nmemb;
    }
}
