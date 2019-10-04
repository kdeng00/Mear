//
// Created by brahmix on 10/1/19.
//

#ifndef MEAR_TOK_H
#define MEAR_TOK_H

#include <string>

#include "model/User.h"

namespace manager {

    class Tok {
    public:
        std::string fetchToken(const model::User&, const std::string&);
    private:
        std::string fetchLoginUri(const std::string&);
        std::string userJsonString(const model::User&);

        static size_t respBodyRetriever(void*, size_t, size_t, char*);
    };
}


#endif //MEAR_TOK_H
