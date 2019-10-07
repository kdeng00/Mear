//
// Created by brahmix on 10/6/19.
//

#ifndef MEAR_TOKEN_H
#define MEAR_TOKEN_H

#include <string>

namespace model {
    class Token
    {
    public:
        Token(const std::string& token) : token(token) { }

        std::string token;
    };
}

#endif //MEAR_TOKEN_H
