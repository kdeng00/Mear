//
// Created by brahmix on 10/3/19.
//

#ifndef MEAR_SONGREPOSITORY_H
#define MEAR_SONGREPOSITORY_H

#include <string>
#include <vector>

#include "model/Song.h"
#include "model/Token.h"

namespace repository {

    class SongRepository {
    public:
        std::vector<model::Song> fetchSongs(const model::Token&, const std::string&);

        model::Song retrieveSong(const std::string&, const int);
        model::Song retrieveSong(const std::string&, const std::string&, const int);
        model::Song retrieveSong(const std::string&, const std::string&, const model::Song&);
        model::Song retrieveSong(const model::Token&, const model::Song&, const std::string&);
    private:
        static size_t respBodyRetriever(void*, size_t, size_t, char*);
    };
}


#endif //MEAR_SONGREPOSITORY_H