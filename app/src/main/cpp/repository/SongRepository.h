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
#include "BaseRepository.h"
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
                    song.filename = "";
                    song.path = "";
                    song.track = songJson["track"].get<int>();
                    song.disc = songJson["disc"].get<int>();
                    song.downloaded = false;

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
                song.track = s["track"].get<int>();
                song.disc = s["disc"].get<int>();
                song.filename = "";
                song.path = "";
                song.downloaded = false;

                return song;
            }

            return sng;
        }

        template<typename Token = model::Token, typename API = model::APIInfo>
        Song downloadSong(const Token &token, const Song song, const API& uri) {
            auto fullUri = utility::GeneralUtility::appendForwardSlashToUri(uri.uri);
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
            downloadedSong.filename = "track";
            if (song.track < 10) {
                downloadedSong.filename.append("0");
                downloadedSong.filename.append(std::to_string(song.track));
            } else {
                downloadedSong.filename.append(std::to_string(song.track));
            }
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

    namespace local {
        template<class Song = model::Song>
        class SongRepository : public BaseRepository {
        public:
            SongRepository() {
                m_tableName = songTable();
            }


            std::vector<Song> retrieveAllSongs(const std::string& appPath) {
                std::vector<Song> downloadedSongs;
                try {
                    // TODO: left off here

                } catch (std::exception& ex) {
                    auto msg = ex.what();
                }

                return downloadedSongs;
            }


            Song retrieveSong(const Song& song, const std::string& appPath) {
                Song retrievedSong;

                try {
                    const auto dbPath = pathOfDatabase(appPath);
                    SQLite::Database db(dbPath, SQLite::OPEN_READONLY);

                    std::string queryString("SELECT * FROM ");
                    queryString.append(m_tableName);
                    queryString.append(" WHERE Id = ?");
                    queryString.append(" LIMIT 1");

                    SQLite::Statement query(db, queryString);
                    query.bind(1, song.id);
                    auto result = query.executeStep();

                    retrievedSong.id = query.getColumn(0).getInt();
                    retrievedSong.title = query.getColumn(1).getString();
                    retrievedSong.artist = query.getColumn(2).getString();
                    retrievedSong.album = query.getColumn(3).getString();
                    retrievedSong.albumArtist = query.getColumn(4).getString();
                    retrievedSong.genre = query.getColumn(5).getString();
                    retrievedSong.year = query.getColumn(6).getInt();
                    retrievedSong.duration = query.getColumn(7).getInt();
                    retrievedSong.track = query.getColumn(8).getInt();
                    retrievedSong.disc = query.getColumn(9).getInt();
                    retrievedSong.filename = query.getColumn(10).getString();
                    retrievedSong.path = query.getColumn(11).getString();
                    retrievedSong.coverArtId = query.getColumn(12).getInt();
                    retrievedSong.downloaded = true;

                } catch (std::exception& ex) {
                    auto msg = ex.what();
                }

                return retrievedSong;
            }


            void createSongTable(const std::string& appPath) {
                try {
                    const auto dbPath = pathOfDatabase(appPath);
                    SQLite::Database db(dbPath, SQLite::OPEN_READWRITE);

                    std::string queryString("CREATE TABLE ");
                    queryString.append(m_tableName);
                    queryString.append(" (Id INTEGER, Title TEXT, Artist TEXT, Album TEXT, ");
                    queryString.append("AlbumArtist TEXT, Genre TEXT, Year INTEGER, ");
                    queryString.append("Duration INTEGER, Track INTEGER, Disc INTEGER, ");
                    queryString.append("Filename TEXT, Path TEXT, CoverArtId INTEGER)");

                    db.exec(queryString);
                } catch (std::exception& ex) {
                    auto msg = ex.what();
                }
            }

            void deleteSongFromTable(const Song& song, const std::string& appPath) {
                try {
                    const auto dbPath = pathOfDatabase(appPath);
                    SQLite::Database db(dbPath, SQLite::OPEN_READWRITE);

                    std::string queryString("DELETE FROM ");
                    queryString.append(m_tableName);
                    queryString.append(" WHERE Id = ?");
                    // queryString.append("Duration, Track, Disc, Filename, Path, CoverArtId)");
                    SQLite::Statement query(db, queryString);
                    query.bind(1, song.id);

                    query.exec();
                } catch (std::exception& ex) {
                    auto msg = ex.what();
                }
            }

            void saveSong(const Song& song, const std::string& appPath) {
                try {
                    const auto dbPath = pathOfDatabase(appPath);
                    SQLite::Database db(dbPath, SQLite::OPEN_READWRITE);

                    std::string queryString("INSERT INTO ");
                    queryString.append(m_tableName);
                    queryString.append(" (Id, Title, Artist, Album, AlbumArtist, Genre, Year, ");
                    queryString.append("Duration, Track, Disc, Filename, Path, CoverArtId) ");
                    queryString.append("VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

                    SQLite::Statement query(db, queryString);
                    query.bind(1, song.id);
                    query.bind(2, song.title);
                    query.bind(3, song.artist);
                    query.bind(4, song.album);
                    query.bind(5, song.albumArtist);
                    query.bind(6, song.genre);
                    query.bind(7, song.year);
                    query.bind(8, song.duration);
                    query.bind(9, song.track);
                    query.bind(10, song.disc);
                    query.bind(11, song.filename);
                    query.bind(12, song.path);
                    query.bind(13, song.coverArtId);

                    query.exec();
                } catch (std::exception& ex) {
                    auto msg = ex.what();
                }
            }
        private:
            constexpr auto songTable() noexcept { return "Song"; }
        };
    }
}


#endif //MEAR_SONGREPOSITORY_H
