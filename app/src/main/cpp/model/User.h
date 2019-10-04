//
// Created by brahmix on 9/26/19.
//

#ifndef MEAR_USER_H
#define MEAR_USER_H


namespace model {
struct User
{
    User() = default;
    User(const std::string& user, const std::string& pass) :
        username(user), password(pass) { }

    std::string username;
    std::string password;
};
}

#endif //MEAR_USER_H
