//
// Created by brahmix on 10/1/19.
//

#ifndef MEAR_TOK_H
#define MEAR_TOK_H

#include <memory>
#include <string>

#include "model/Token.h"
#include "model/User.h"

namespace manager {

    class Tok {
    public:
        model::Token fetchTokenTrans(const model::User&, const std::string&);
    private:
        std::string fetchLoginUri(const std::string&) noexcept;
        constexpr auto loginEndpoint() noexcept;
        std::string userJsonString(const model::User&);

        static size_t respBodyRetriever(void*, size_t, size_t, char*);
    };
}


#endif //MEAR_TOK_H
