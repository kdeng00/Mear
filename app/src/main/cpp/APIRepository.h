//
// Created by brahmix on 10/12/19.
//

#ifndef MEAR_APIREPOSITORY_H
#define MEAR_APIREPOSITORY_H

#include <string>

#include "BaseRepository.h"
#include "model/APIInfo.h"

namespace repository { namespace local {
    class APIRepository: public BaseRepository {
    public:
        APIRepository();

        model::APIInfo retrieveAPIInfo(const std::string&);

        void createAPiInfoTable(const std::string&);
        void deleteAPIInfo(const model::APIInfo&, const std::string&);
        void saveAPIInfo(const model::APIInfo&, const std::string&);
    private:
        //std::string apiInfoTable();
        std::string apiInfoTable() noexcept;
    };
}}


#endif //MEAR_APIREPOSITORY_H
