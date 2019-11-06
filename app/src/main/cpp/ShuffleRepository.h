//
// Created by brahmix on 10/21/19.
//

#ifndef MEAR_SHUFFLEREPOSITORY_H
#define MEAR_SHUFFLEREPOSITORY_H

#include <string>

#include "BaseRepository.h"
#include "ShuffleTypes.h"

namespace repository { namespace local {
    class ShuffleRepository : public BaseRepository {
    public:
        ShuffleRepository();

        ShuffleTypes retrieveShuffleMode(const std::string&);

        void createShuffleTable(const std::string&);
        void updateShuffle(const std::string&);
    private:
        std::string shuffleTable() noexcept;
    };
}}


#endif //MEAR_SHUFFLEREPOSITORY_H
