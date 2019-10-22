//
// Created by brahmix on 10/21/19.
//

#ifndef MEAR_REPEATREPOSITORY_H
#define MEAR_REPEATREPOSITORY_H

#include <string>

#include "BaseRepository.h"
#include "RepeatTypes.h"

namespace repository { namespace local {
    class RepeatRepository : public BaseRepository {
    public:
        RepeatRepository();

        RepeatTypes retrieveRepeatMode(const std::string&);

        void createRepeatTable(const std::string&);
        void updateRepeat(const std::string&);
    private:
        constexpr std::string repeatTable() noexcept;
    };
}}


#endif //MEAR_REPEATREPOSITORY_H
