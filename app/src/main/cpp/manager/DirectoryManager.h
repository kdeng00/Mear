//
// Created by brahmix on 11/29/19.
//

#ifndef MEAR_DIRECTORYMANAGER_H
#define MEAR_DIRECTORYMANAGER_H

#include <string>
#include <cstdio>
#include <dirent.h>
#include <errno.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <unistd.h>

#include "../model/Song.h"
#include "../utility/GeneralUtility.h"

namespace manager {
    template<class Song = model::Song>
    class DirectoryManager {
    public:
        template<typename Str = std::string>
        std::string fullSongPath(const Song& song, const Str& path) {
            std::string s = utility::GeneralUtility::appendForwardSlashToUri<std::string>(path);
            s.append(song.albumArtist);
            s.append("/");
            s.append(song.album);
            s.append("/");
            s.append(song.filename);
            s.append(".mp3");

            return s;
        }

        template<typename Str = std::string>
        Str albumPath(const Song& song, const Str& path) {
            std::string s = utility::GeneralUtility::appendForwardSlashToUri<std::string>(path);
            s.append(song.albumArtist);
            s.append("/");
            s.append(song.album);

            auto ss = s;

            return ss.c_str();
        }

        template<typename Str = std::string>
        Str artistPath(const Song& song, const Str& path) {
            Str s = utility::GeneralUtility::appendForwardSlashToUri<std::string>(path);
            s.append(song.albumArtist);

            return s.c_str();
        }


        template<typename Str = std::string, typename B = bool>
        B doesSongExist(const Song& song, const Str& path) {
            std::string s = albumPath(song, path);
            s.append("/");
            s.append(song.filename);

            struct stat buffer;
            return (stat (s.c_str(), &buffer) == 0);
        }

        template<typename Str = std::string, typename B = bool>
        B deleteSong(const Song& song, const Str& path) {
            auto s = fullSongPath(song, path);
            auto result = std::remove(s.c_str());

            if (result == 0) {
                return true;
            } else {
                return false;
            }
        }


        template<typename Str = std::string>
        void createSongDirectory(const Song& song, const Str& path) {
            if (!artistDirectoryExists(song, path)) {
                auto status = mkdir(artistPath<std::string>(song, path).c_str(), 0777);
            }
            if (!albumDirectoryExists(song, path)) {
                auto status = mkdir(albumPath(song, path), 0777);
            }
        }

        template<typename Str = std::string>
        void deleteSongDirectory(const Song& song, const Str& path) {

        }
    private:
        template<typename Str = std::string, typename B = bool>
        B albumDirectoryExists(const Song song, const Str& path) {
            auto albumPath = utility::GeneralUtility::appendForwardSlashToUri<std::string>(path);
            albumPath.append(song.albumArtist);
            albumPath.append("/");
            albumPath.append(song.album);
            DIR* dir = opendir(albumPath.c_str());

            if (dir) {
                closedir(dir);
                return true;
            } else if (ENOENT == errno) {
                return false;
            }

            return true;
        }

        template<typename Str = std::string, typename B = bool>
        B artistDirectoryExists(const Song song, const Str& path) {
            auto artistPath = utility::GeneralUtility::appendForwardSlashToUri<std::string>(path);
            artistPath.append(song.albumArtist);
            DIR* dir = opendir(artistPath.c_str());

            if (dir) {
                closedir(dir);
                return true;
            } else if (ENOENT == errno) {
                return false;
            }

            return true;
        }
    };
}

#endif //MEAR_DIRECTORYMANAGER_H
