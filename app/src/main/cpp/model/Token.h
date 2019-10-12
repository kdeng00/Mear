//
// Created by brahmix on 10/6/19.
//

#ifndef MEAR_TOKEN_H
#define MEAR_TOKEN_H

#include <string>
#include <utility>

namespace model {
    class Token
    {
    public:
        Token() = default;
        Token(const std::string& accessToken) : accessToken(accessToken) { }
        Token(const std::string&& accessToken) :
            accessToken(std::move(accessToken)) { }

        std::string accessToken;
    };
}

#endif //MEAR_TOKEN_H
