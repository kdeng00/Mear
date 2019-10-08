//
// Created by brahmix on 9/26/19.
//

#ifndef MEAR_USER_H
#define MEAR_USER_H

#include <string>
#include <utility>

namespace model {
class User
{
public:
    User() = default;
    User(const std::string& user, const std::string& pass) :
        username(user), password(pass) { }
    User(const std::string&& user, const std::string&& pass) :
        username(std::move(user)), password(std::move(pass)) {}

    std::string username;
    std::string password;
};
}

#endif //MEAR_USER_H
