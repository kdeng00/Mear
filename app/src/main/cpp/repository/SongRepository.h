//
// Created by brahmix on 10/3/19.
//

#ifndef MEAR_SONGREPOSITORY_H
#define MEAR_SONGREPOSITORY_H

#include <string>
#include <vector>

#include "../3rdparty/android/include/curl/curl.h"
#include "../3rdparty/json/single_include/nlohmann/json.hpp"

#include "../model/Song.h"
#include "../model/Token.h"
#include "../utility/GeneralUtility.h"

namespace repository {

    template<class Song = model::Song>
    class SongRepository {
    public:
        template<typename Token = model::Token>
        std::vector<Song> fetchSongs(const Token& token, const std::string& uri) {
            auto fullUri = utility::GeneralUtility::appendForwardSlashToUri<std::string>(uri);
            fullUri.append(songRecordEndpoint());
            std::vector<Song> songs;

            CURL *curl;
            CURLcode res;
            struct curl_slist *chunk = nullptr;
            curl = curl_easy_init();

            if (!curl) {
                return songs;
            }

            std::string resp;

            std::string authInfo("Authorization: Bearer ");
            authInfo.append(token.accessToken);
            constexpr auto contentType = "Content-type: Keep-alive";
            chunk = curl_slist_append(chunk, authInfo.c_str());
            chunk = curl_slist_append(chunk, contentType);

            curl_easy_setopt(curl, CURLOPT_URL, fullUri.c_str());
            curl_easy_setopt(curl, CURLOPT_HTTPHEADER, chunk);
            curl_easy_setopt(curl, CURLOPT_WRITEFUNCTION, respBodyRetriever);
            curl_easy_setopt(curl, CURLOPT_WRITEDATA, &resp);
            curl_easy_setopt(curl, CURLOPT_SSL_VERIFYPEER, 0L);

            res = curl_easy_perform(curl);
            curl_easy_cleanup(curl);

            if (res == CURLE_OK) {
                auto songsJson = nlohmann::json::parse(resp.c_str());
                for (auto& songJson: songsJson) {
                    Song song(songJson["id"].get<int>(), songJson["title"].get<std::string>(),
                            songJson["artist"].get<std::string>(),
                            songJson["album"].get<std::string>(),
                            songJson["album_artist"].get<std::string>(),
                            songJson["genre"].get<std::string>(), songJson["duration"].get<int>(),
                            songJson["year"].get<int>(), songJson["coverart_id"].get<int>());

                    songs.push_back(song);
                }
            }

            return songs;
        }

        template<typename Token = model::Token>
        Song retrieveSong(const Token& token, const Song& sng,
                const std::string& baseUri) {
            auto uri = utility::GeneralUtility::appendForwardSlashToUri(baseUri);
            uri.append(songRecordEndpoint());
            uri.append(std::to_string(sng.id));

            CURL *curl;
            CURLcode res;
            struct curl_slist *chunk = nullptr;
            curl = curl_easy_init();

            if (!curl) {
                return sng;
            }

            std::string resp;

            std::string authInfo("Authorization: Bearer ");
            authInfo.append(token.accessToken);
            constexpr auto contentType = "Content-type: Keep-alive";
            chunk = curl_slist_append(chunk, authInfo.c_str());
            chunk = curl_slist_append(chunk, contentType);

            curl_easy_setopt(curl, CURLOPT_URL, uri.c_str());
            curl_easy_setopt(curl, CURLOPT_HTTPHEADER, chunk);
            curl_easy_setopt(curl, CURLOPT_WRITEFUNCTION, respBodyRetriever);
            curl_easy_setopt(curl, CURLOPT_WRITEDATA, &resp);
            curl_easy_setopt(curl, CURLOPT_SSL_VERIFYPEER, 0L);

            res = curl_easy_perform(curl);
            curl_easy_cleanup(curl);

            if (res == CURLE_OK) {
                auto s = nlohmann::json::parse(resp.c_str());
                Song song(s["id"].get<int>(), s["title"].get<std::string>(),
                        s["artist"].get<std::string>(), s["album"].get<std::string>(),
                        s["album_artist"].get<std::string>(), s["genre"].get<std::string>(),
                        s["duration"].get<int>(), s["year"].get<int>(),
                        s["coverart_id"].get<int>());

                return song;
            }

            return sng;
        }

        template<typename Token = model::Token>
        Song downloadSong(const Token &token, const Song song, const std::string& uri) {
            auto fullUri = utility::GeneralUtility::appendForwardSlashToUri(uri);
            fullUri.append(songDownloadEndpoint());
            fullUri.append(std::to_string(song.id));
            Song downloadedSong;

            CURL *curl = curl_easy_init();
            struct curl_slist *chunk = nullptr;

            std::string data;
            const auto authHeader = utility::GeneralUtility::authHeader(token);
            chunk = curl_slist_append(chunk, authHeader.c_str());
            chunk = curl_slist_append(chunk, "Content-type: Keep-alive");

            curl_easy_setopt(curl, CURLOPT_URL, fullUri.c_str());
            curl_easy_setopt(curl, CURLOPT_HTTPHEADER, chunk);
            curl_easy_setopt(curl, CURLOPT_WRITEFUNCTION, respBodyRetriever);
            curl_easy_setopt(curl, CURLOPT_WRITEDATA, &data);
            curl_easy_setopt(curl, CURLOPT_SSL_VERIFYPEER, 0L);

            auto res = curl_easy_perform(curl);
            curl_easy_cleanup(curl);
            downloadedSong.data = std::move(std::vector<char>(data.begin(), data.end()));
            downloadedSong.filename = downloadedSong.title;
            downloadedSong.filename.append(".mp3");

            return downloadedSong;
        }
    private:
        static size_t respBodyRetriever(void *ptr, size_t size, size_t nmemb, char *e) {
            ((std::string*)e)->append((char*)ptr, size * nmemb);
            return size * nmemb;
        }

        constexpr auto songRecordEndpoint() noexcept { return "api/v1/song/"; }

        constexpr auto songDownloadEndpoint() noexcept { return "api/v1/song/data/"; }
    };
}


#endif //MEAR_SONGREPOSITORY_H
