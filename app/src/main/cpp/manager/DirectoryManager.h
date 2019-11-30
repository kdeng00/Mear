//
// Created by brahmix on 11/29/19.
//

#ifndef MEAR_DIRECTORYMANAGER_H
#define MEAR_DIRECTORYMANAGER_H

#include <string>

#include "../model/Song.h"

namespace manager {
    class DirectoryManager {
    public:
        template<typename Str = std::string, class Song = model::Song>
        Str fullSongPath(const Song& song, const Str& path) {
            auto s = "";

            return s;
        }

        template<typename Str = std::string, class Song = model::Song>
        Str albumPath(const Song& song, const Str& path) {
            auto ss = "";

            return ss;
        }

        template<typename Str = std::string, class Song = model::Song>
        Str artistPath(const Song& song, const Str& path) {
            return "art";
        }


        template<typename Str = std::string, class Song = model::Song>
        bool doesSongExist(const Song& song, const Str& path) {
            return false;
        }


        template<typename Str = std::string, class Song = model::Song>
        void createSongDirectory(const Song& song, const Str& path) {

        }

        template<typename Str = std::string, class Song = model::Song>
        void deleteSongDirectory(const Song& song, const Str& path) {

        }
    private:
    };
}

#endif //MEAR_DIRECTORYMANAGER_H
