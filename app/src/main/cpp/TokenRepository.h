//
// Created by brahmix on 10/6/19.
//

#ifndef MEAR_TOKENREPOSITORY_H
#define MEAR_TOKENREPOSITORY_H

#include <string>

#include "model/Token.h"
#include "BaseRepository.h"

namespace repository { namespace local {
    class TokenRepository: public BaseRepository {
    public:
        TokenRepository();

        model::Token retrieveToken(const std::string&);

        void createTokenTable(const std::string&);
        void saveToken(const model::Token&, const std::string&);
    private:
        std::string tokenTable() noexcept;
    };
}}


#endif //MEAR_TOKENREPOSITORY_H
