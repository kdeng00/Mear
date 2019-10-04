//
// Created by brahmix on 10/3/19.
//

#ifndef MEAR_SONGREPOSITORY_H
#define MEAR_SONGREPOSITORY_H

#include <string>
#include <vector>

#include "model/Song.h"

namespace repository {

    class SongRepository {
    public:
        std::vector<model::Song> fetchSongs(const std::string&, const std::string&);
    private:
    };
}


#endif //MEAR_SONGREPOSITORY_H
