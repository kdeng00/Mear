//
// Created by brahmix on 10/6/19.
//

#ifndef MEAR_USERREPOSITORY_H
#define MEAR_USERREPOSITORY_H

#include <string>

#include "BaseRepository.h"
#include "../model/User.h"


namespace repository { namespace local {
    template<class User = model::User>
    class UserRepository : public BaseRepository {
    public:
        UserRepository() {
            m_tableName = userTable();
        }


        User retrieveUserCredentials(const std::string& appPath) {
            User user;
            try {
                const auto dbPath = pathOfDatabase(appPath);
                SQLite::Database db(dbPath, SQLite::OPEN_READONLY);

                std::string queryString("SELECT * FROM ");
                queryString.append(m_tableName);
                queryString.append(" LIMIT 1");

                SQLite::Statement query(db, queryString);

                auto result = query.executeStep();

                user.username = std::move(query.getColumn(1).getString());
                user.password = std::move(query.getColumn(2).getString());

                return user;

            } catch (std::exception& ex) {
                auto msg = ex.what();
            }

            return user;
        }


        void createUserTable(const std::string& appPath) {
            try {
                const auto dbPath = pathOfDatabase(appPath);
                SQLite::Database db(dbPath, SQLite::OPEN_READWRITE);

                std::string queryString("CREATE TABLE ");
                queryString.append(m_tableName);
                queryString.append(" (Id INTEGER PRIMARY KEY, Username TEXT, Password TEXT)");
                db.exec(queryString);
            } catch (std::exception& ex) {
                auto msg = ex.what();
            }
        }

        void deleteUserTable(const std::string& appPath) {
            try {
                const auto dbPath = pathOfDatabase(appPath);
                SQLite::Database db(dbPath, SQLite::OPEN_READWRITE);

                std::string queryStr("DELETE FROM ");
                queryStr.append(m_tableName);

                db.exec(queryStr);
            } catch (std::exception& ex) {
                auto msg = ex.what();
            }
        }

        void saveUserCred(const User& user, const std::string& appPath) {
            try {
                const auto dbPath = pathOfDatabase(appPath);
                SQLite::Database db(dbPath, SQLite::OPEN_READWRITE);
                std::string queryString("INSERT INTO ");
                queryString.append(m_tableName);
                queryString.append(" (Username, Password) VALUES (?, ?)");

                SQLite::Statement query(db, queryString);
                query.bind(1, user.username);
                query.bind(2, user.password);

                query.exec();
            } catch (std::exception& ex) {
                auto msg = ex.what();
            }
        }
    private:
        const std::string userTable() noexcept { return "User"; }
    };

}}

#endif //MEAR_USERREPOSITORY_H
