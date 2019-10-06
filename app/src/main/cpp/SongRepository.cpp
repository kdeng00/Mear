//
// Created by brahmix on 10/3/19.
//

#include "SongRepository.h"

#include <curl/curl.h>
#include <nlohmann/json.hpp>

namespace repository {
    // TODO: implement this
    std::vector<model::Song> SongRepository::fetchSongs(const std::string& token,
        const std::string& uri)
    {
        std::vector<model::Song> songs;

      return songs;
    }


    model::Song SongRepository::retrieveSong(const std::string& token, const int id) {
        std::string uri("");
        uri.append(std::to_string(id));
        model::Song song;

        CURL *curl;
        CURLcode res;
        struct curl_slist *chunk = NULL;
        curl = curl_easy_init();

        if (!curl) {
            return song;
        }

        auto resp = std::make_unique<char[]>(10000);

        std::string authInfo("Authorization: Bearer ");
        authInfo.append(token);
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

            return song;
        }

        return song;
    }

    model::Song SongRepository::retrieveSong(const std::string& token, const std::string& baseUri,
            const int id) {
        std::string uri(baseUri);
        uri.append("/api/v1/song/");
        uri.append(std::to_string(id));
        model::Song song;

        CURL *curl;
        CURLcode res;
        struct curl_slist *chunk = NULL;
        curl = curl_easy_init();

        if (!curl) {
            return song;
        }

        auto resp = std::make_unique<char[]>(10000);

        std::string authInfo("Authorization: Bearer ");
        authInfo.append(token);
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

            return song;
        }

        return song;
    }

    model::Song SongRepository::retrieveSong(const std::string& token, const std::string& baseUri,
                                             const model::Song& sng) {
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
        authInfo.append(token);
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
