//
// Created by brahmix on 10/6/19.
//

#ifndef MEAR_USERREPOSITORY_H
#define MEAR_USERREPOSITORY_H

#include <string>

#include "BaseRepository.h"
#include "model/User.h"


namespace repository { namespace local {

    class UserRepository : public BaseRepository {
    public:
        UserRepository();

        model::User retrieveUserCredentials(const std::string&);

        bool doesUserTableExist(const std::string&);

        void createUserTable(const std::string&);
        void deleteUserTable(const std::string&);
        void saveUserCred(const model::User&, const std::string&);
    private:
        std::string userTable();
    };

}}

#endif //MEAR_USERREPOSITORY_H
