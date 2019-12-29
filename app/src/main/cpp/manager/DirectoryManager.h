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

            if (song.disc == 0) {
                s.append("disc1");
            } else {
                s.append("disc");
                s.append(std::to_string(song.disc));
            }

            s.append("/");
            s.append("track");
            if (song.track < 10) {
                s.append("0");
                s.append(std::to_string(song.track));
            } else {
                s.append(std::to_string(song.track));
            }

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
        Str albumDiscPath(const Song& song, const Str& path) {
            std::string songPath = albumPath(song, path);
            songPath.append("/disc");
            if (song.disc == 0) {
                songPath.append("1");
            } else {
                songPath.append(std::to_string(song.disc));
            }

            return songPath.c_str();
        }

        template<typename Str = std::string>
        Str artistPath(const Song& song, const Str& path) {
            Str s = utility::GeneralUtility::appendForwardSlashToUri<std::string>(path);
            s.append(song.albumArtist);

            return s.c_str();
        }


        template<typename Str = std::string, typename B = bool>
        B doesSongExist(const Song& song, const Str& path) {
            std::string s = fullSongPath(song, path);

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
            if (!albumDiscDirectoryExists(song, path)) {
                auto status = mkdir(albumDiscPath(song, path), 0777);
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
            /**
            if (song.disc == 0) {
                albumPath.append("/");
                albumPath.append("disc1");
            } else {
                albumPath.append("/");
                albumPath.append("disc");
                albumPath.append(std::to_string(song.disc));
            }
             */
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
        B albumDiscDirectoryExists(const Song& song, const Str& path) {
            std::string albumDistPath = albumPath(song, path);
            albumDistPath.append("/disc");
            if (song.disc == 0) {
                albumDistPath.append("1");
            } else {
                albumDistPath.append(std::to_string(song.disc));
            }

            DIR *dir = opendir(albumDistPath.c_str());

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
